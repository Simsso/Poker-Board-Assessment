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
                            outcome = winRateFor(deck, onePlayerPocketCards(new PocketCards(deck.takeCard(rank1, suit1), deck.takeCard(rank2, suit2)), opponents), iterations)[0];
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
                    offSuit = winRateFor(
                            deck1,
                            onePlayerPocketCards(
                                    new PocketCards(
                                            deck1.takeCard(rank1, suit1),
                                            deck1.takeCard(rank2, suit2)),
                                    opponents),
                            iterations)[0];
                } catch (DeckStateException e) {
                    e.printStackTrace();
                }

                if (rank1 != rank2) {
                    // suited combinations possible
                    suit2 = suit1;
                    Deck deck2 = new Deck();
                    try {
                        suited = winRateFor(
                                deck2,
                                onePlayerPocketCards(
                                        new PocketCards(
                                                deck2.takeCard(rank1, suit1),
                                                deck2.takeCard(rank2, suit2)),
                                        opponents),
                                iterations)[0];
                    } catch (DeckStateException e) {
                        e.printStackTrace();
                    }
                }

                logWithTabs(rank1, rank2, suited, offSuit);
            }
        }
    }

    static Outcome[] winRateFor(final Deck deck, final PocketCards[] pocketCards, final int iterations) {
        return winRateFor(deck, pocketCards, null, new Card[0], iterations);
    }

    static Outcome[] winRateFor(final Deck deck, final PocketCards[] pocketCards, final Card[] communityCards, final Card[] takenCards, final int iterations) {
        long start = System.nanoTime(); // performance

        int playerCount = pocketCards.length;

        if (pocketCards.length < 1) {
            return null;
        }

        Outcome outcome[] = new Outcome[playerCount]; // declare and initialize (took a while to find that NullPointerException...)
        for (int i = 0; i < playerCount; i++) { outcome[i] = new Outcome(); }

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);

                // these threads may not prevent the JVM from exiting
                t.setDaemon(true);
                return t;
            }
        });


        Set<Callable<Outcome[]>> callables = new HashSet<Callable<Outcome[]>>();

        for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
            int currentThreadIterations = iterations / THREAD_COUNT;
            if (threadId == THREAD_COUNT - 1) {
                currentThreadIterations += iterations % THREAD_COUNT;
            }

            // objects for each thread
            Deck threadDeck = deck.clone();
            Card[] threadCommunityCards = new Card[5];
            Card[] threadTakenCards = new Card[takenCards.length];
            PocketCards[] threadPocketCards = new PocketCards[playerCount];

            try {
                // copy community cards to threadCommunityCards
                for (int i = 0; i < 5; i++) {
                    if (communityCards == null || communityCards.length < i + 1 || communityCards[i] == null) {
                        threadCommunityCards[i] = null;
                    }
                    else {
                        threadCommunityCards[i] = threadDeck.getCardLike(communityCards[i]);
                    }
                }

                // copy taken cards to thread array
                for (int i = 0; i < takenCards.length; i++) {
                    threadTakenCards[i] = threadDeck.getCardLike(takenCards[i]);
                }

                // copy pocket cards to threadPocketCards (null values possible for not determined cards)
                for (int i = 0; i < playerCount; i++) {
                    if (pocketCards[i] == null) {
                        threadPocketCards[i] = new PocketCards(null, null);
                    }
                    else if (pocketCards[i].card1 != null && pocketCards[i].card2 == null) {
                        threadPocketCards[i] = new PocketCards(threadDeck.getCardLike(pocketCards[i].card1), null);
                    }
                    else if (pocketCards[i].card1 == null && pocketCards[i].card2 != null) {
                        threadPocketCards[i] = new PocketCards(null, threadDeck.getCardLike(pocketCards[i].card2));
                    }
                    else {
                        threadPocketCards[i] = new PocketCards(
                                threadDeck.getCardLike(pocketCards[i].card1),
                                threadDeck.getCardLike(pocketCards[i].card2));
                    }
                }
            } catch (DeckStateException e) {
                e.printStackTrace();
            }

            callables.add(new WinRateCallable(
                    threadDeck, // all threads have different deck objects with the same properties
                    threadPocketCards,
                    threadCommunityCards,
                    threadTakenCards,
                    currentThreadIterations));
        }

        try {
            List<Future<Outcome[]>> futures = executorService.invokeAll(callables);

            for (Future<Outcome[]> threadOutcomeFuture : futures) {
                Outcome[] threadOutcome = threadOutcomeFuture.get();
                for (int i = 0; i < playerCount; i++) {
                    outcome[i].merge(threadOutcome[i]);
                }
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
        System.out.printf("%9.4f µs (x%d hands)\n", (double)(System.nanoTime() - start) / 1e3 / Outcome.getCount(outcome), Outcome.getCount(outcome));
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

    static PocketCards[] onePlayerPocketCards(PocketCards player, int opponents) {
        PocketCards[] pocketCards = new PocketCards[opponents + 1];
        pocketCards[0] = player;
        return pocketCards;
    }
}
