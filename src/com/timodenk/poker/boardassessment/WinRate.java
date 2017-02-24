package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

class WinRate {
    private static final int THREAD_COUNT = 4;

    static void analyseAllPocketCardCombinations(int iterations, int opponents) {
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                for (Suit suit1 : Suit.values()) {
                    for (Suit suit2 : Suit.values()) {
                        if (rank1 == rank2 && suit1 == suit2) {
                            continue; // not possible
                        }
                        Deck deck = new Deck();
                        Outcome outcome = null;
                        try {
                            outcome = winRateFor(deck, deck.takeCard(rank1, suit1), deck.takeCard(rank2, suit2), iterations, opponents);
                        } catch (DeckStateException e) {
                            e.printStackTrace();
                        }
                        logWithTabs(rank1, rank2, (suit1 == suit2) ? outcome : null, (suit1 == suit2) ? null : outcome);
                    }
                }
            }
        }
    }

    static void analyseWinRates(int iterations, int opponents) {
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                if (rank2.ordinal() > rank1.ordinal()) {
                    continue; // combination occurs the other way around
                }

                // representative for off-suit combinations
                Suit suit1 = Suit.CLUBS;
                Suit suit2 = Suit.SPADES;

                Deck deck1 = new Deck();
                Outcome suited = null;
                Outcome offSuit = null;
                try {
                    offSuit = winRateFor(deck1, deck1.takeCard(rank1, suit1), deck1.takeCard(rank2, suit2), iterations, opponents);
                } catch (DeckStateException e) {
                    e.printStackTrace();
                }

                if (rank1 != rank2) {
                    // suited combinations possible
                    suit2 = suit1;
                    Deck deck2 = new Deck();
                    try {
                        suited = winRateFor(deck2, deck2.takeCard(rank1, suit1), deck2.takeCard(rank2, suit2), iterations, opponents);
                    } catch (DeckStateException e) {
                        e.printStackTrace();
                    }
                }

                logWithTabs(rank1, rank2, suited, offSuit);
            }
        }
    }

    static Outcome winRateFor(final Deck deck, final Card pocketCard1, final Card pocketCard2, final int iterations, final int opponents) {
        return winRateFor(deck, pocketCard1, pocketCard2, null, iterations, opponents);
    }

    static Outcome winRateFor(final Deck deck, final Card pocketCard1, final Card pocketCard2, final Card[] communityCards, final int iterations, final int opponents) {
        long start = System.nanoTime(); // performance

        if (opponents < 1) {
            return null;
        }

        Outcome outcome = new Outcome();

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);

                // these threads may not prevent the JVM from exiting
                t.setDaemon(true);
                return t;
            }
        });


        Set<Callable<Outcome>> callables = new HashSet<Callable<Outcome>>();

        for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
            int currentThreadIterations = iterations / THREAD_COUNT;
            if (threadId == THREAD_COUNT - 1) {
                currentThreadIterations += iterations % THREAD_COUNT;
            }

            Deck threadDeck = deck.clone();
            Card[] threadCommunityCards = new Card[5];

            // copy community cards to threadCommunityCards
            for (int i = 0; i < 5; i++) {
                if (communityCards == null || communityCards.length < i + 1 || communityCards[i] == null) {
                    threadCommunityCards[i] = null;
                }
                else {
                    try {
                        threadCommunityCards[i] = threadDeck.getCardLike(communityCards[i]);
                    } catch (DeckStateException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                callables.add(new WinRateCallable(
                        threadDeck, // all threads have different deck objects with the same properties
                        threadDeck.getCardLike(pocketCard1),
                        threadDeck.getCardLike(pocketCard2),
                        threadCommunityCards,
                        currentThreadIterations, opponents));
            } catch (DeckStateException e) {
                e.printStackTrace();
            }
        }

        try {
            List<Future<Outcome>> futures = executorService.invokeAll(callables);

            for (Future<Outcome> threadOutcome  : futures) {
                outcome.merge(threadOutcome.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        finally {
            executorService.shutdown();
        }

        // performance measurements
        System.out.printf("%8.2f µs (%d iterations)\n", (System.nanoTime() - start) / 10e3 / (double)(outcome.getCount()), outcome.getCount());

        return outcome;
    }

    private static void log(Rank rank1, Rank rank2, Outcome suited, Outcome offSuit) {
        System.out.println(rank1 + " " + rank2);
        if (suited != null) {
            System.out.println("\tsuited: " + (suited.getWinRate() * 100) + "% | " + (suited.getSplitRate() * 100) + "%");
        }
        System.out.println("\toff-suit: " + (offSuit.getWinRate() * 100) + "% | " + (offSuit.getSplitRate() * 100) + "%");
        System.out.println();
    }

    private static void logWithTabs(Rank rank1, Rank rank2, Outcome suited, Outcome offSuit) {
        if (offSuit != null) {
            System.out.println(rank1 + " " + rank2 + " (off-suit)\t" + offSuit.getWinRate() + "\t" + offSuit.getSplitRate());
        }
        if (suited != null) {
            System.out.println(rank1 + " " + rank2 + " (suited)\t" + suited.getWinRate() + "\t" + suited.getSplitRate());
        }
    }
}
