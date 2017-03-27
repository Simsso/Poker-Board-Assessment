package com.timodenk.poker.boardassessment;

import com.timodenk.poker.CommunityCards;
import com.timodenk.poker.StartingHand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

class StartingHandAnalysis {
    private static final int THREAD_COUNT = 12; // number of threads

    static void start() {
        CommunityCards[] allCommunityCardCombinations = CommunityCards.getAllCombinations();
        StartingHand[] startingHands = StartingHand.getAll();

        StartingHandOutcome[] outcomes = new StartingHandOutcome[startingHands.length];
        for (int i = 0; i < outcomes.length; i++) {
            outcomes[i] = new StartingHandOutcome(startingHands[i]); // init outcome objects
        }

        // start threads
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);

                // these threads may not prevent the JVM from exiting
                t.setDaemon(true);
                return t;
            }
        });

        Set<Callable<StartingHandOutcome[]>> outcomesCallableSet = new HashSet<Callable<StartingHandOutcome[]>>();

        for (int threadId = 0, communityIndex = 0; threadId < THREAD_COUNT; threadId++) {
            int communitiesCount = allCommunityCardCombinations.length / THREAD_COUNT  +
                    ((threadId < THREAD_COUNT - 1) ? 0 : (allCommunityCardCombinations.length % THREAD_COUNT));
            outcomesCallableSet.add(
                    new StartingHandAnalysisCallable(
                            threadId,
                            Arrays.copyOfRange(
                                    allCommunityCardCombinations, communityIndex, communityIndex + communitiesCount),
                            startingHands));
            communityIndex += communitiesCount;
        }

        try {
            List<Future<StartingHandOutcome[]>> futures = executorService.invokeAll(outcomesCallableSet);

            for (Future<StartingHandOutcome[]> threadOutcomeFuture : futures) {
                StartingHandOutcome[] threadOutcome = threadOutcomeFuture.get();
                for (int i = 0; i < startingHands.length; i++) {
                    outcomes[i].merge(threadOutcome[i]);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        for (StartingHandOutcome outcome : outcomes) {
            System.out.println(outcome);
        }
    }
}
