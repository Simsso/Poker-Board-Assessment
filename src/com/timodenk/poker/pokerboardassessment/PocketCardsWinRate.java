package com.timodenk.poker.pokerboardassessment;

import com.timodenk.poker.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

class PocketCardsWinRate {
    private static final int THREAD_COUNT = 8;

    static void analyseAllWinRates(int iterations, int opponents) {
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                for (Suit suit1 : Suit.values()) {
                    for (Suit suit2 : Suit.values()) {
                        if (rank1 == rank2 && suit1 == suit2) {
                            continue; // not possible
                        }

                        Outcome outcome = winRateFor(rank1, suit1, rank2, suit2, iterations, opponents);
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

                long stop, start = System.nanoTime();

                Suit suit1 = Suit.CLUBS;
                Suit suit2 = Suit.SPADES;
                Outcome suited = null,
                        offSuit = winRateFor(rank1, suit1, rank2, suit2, iterations, opponents);

                if (rank1 != rank2) {
                    suit2 = Suit.CLUBS;
                    suited = winRateFor(rank1, suit1, rank2, suit2, iterations, opponents);
                }

                stop = System.nanoTime();

                System.out.printf("%8.2f µs\n", (stop - start) / 10e3 / (double)(offSuit.getCount() + ((suited == null) ? 0 : suited.getCount())));

                logWithTabs(rank1, rank2, suited, offSuit);
            }
        }
    }

    private static Outcome winRateFor(final Rank rank1, final Suit suit1, final Rank rank2, final Suit suit2, final int iterations, final int opponents) {
        if (opponents < 1) {
            return null;
        }

        Outcome outcome = new Outcome();

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        Set<Callable<Outcome>> callables = new HashSet<Callable<Outcome>>();

        for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
            int currentThreadIterations = iterations / THREAD_COUNT;
            if (threadId == THREAD_COUNT - 1) {
                currentThreadIterations += iterations % THREAD_COUNT;
            }
            callables.add(new WinRateCallable(rank1, suit1, rank2, suit2, currentThreadIterations, opponents));
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
            System.out.println("Shutdown");
        }
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
