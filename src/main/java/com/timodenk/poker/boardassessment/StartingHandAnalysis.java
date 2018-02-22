package com.timodenk.poker.boardassessment;

import com.timodenk.poker.CommunityCards;
import com.timodenk.poker.StartingHand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

class StartingHandAnalysis {
    private static final int THREAD_COUNT = 8; // number of threads

    static void start(OutputStream out, OutputStream log) {
        CommunityCards[] allCommunityCardCombinations = CommunityCards.getAllCombinations();
        StartingHand[] startingHands = StartingHand.getAll();

        Outcome[][] outcomes = new Outcome[startingHands.length][startingHands.length];
        for (int i = 0; i < outcomes.length; i++) { // init outcome array
            for (int j = 0; j < outcomes.length; j++) {
                // matrix contains results
                // row plays against column
                outcomes[i][j] = new Outcome(false);
            }
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

        Set<Callable<Outcome[][]>> outcomesCallableSet = new HashSet<>();

        for (int threadId = 0, communityIndex = 0; threadId < THREAD_COUNT; threadId++) {
            int communitiesCount = allCommunityCardCombinations.length / THREAD_COUNT  +
                    ((threadId < THREAD_COUNT - 1) ? 0 : (allCommunityCardCombinations.length % THREAD_COUNT));
            outcomesCallableSet.add(
                    new StartingHandAnalysisCallable(
                            threadId,
                            Arrays.copyOfRange(
                                    allCommunityCardCombinations, communityIndex, communityIndex + communitiesCount),
                            startingHands,
                            log));
            communityIndex += communitiesCount;
        }

        allCommunityCardCombinations = null; // not required anymore

        // free up community card combinations array memory
        Runtime r = Runtime.getRuntime();
        r.gc();

        try {
            List<Future<Outcome[][]>> futures = executorService.invokeAll(outcomesCallableSet);

            for (Future<Outcome[][]> threadOutcomeFuture : futures) {
                Outcome[][] threadOutcome = threadOutcomeFuture.get();

                for (int i = 0; i < startingHands.length; i++) {
                    for (int j = 0; j < startingHands.length; j++) {
                        outcomes[i][j].merge(threadOutcome[i][j]);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        try {
            out.write("\t".getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < outcomes.length; i++) {
                out.write((startingHands[i].toString() + "\t\t").getBytes(StandardCharsets.UTF_8));
            }
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < outcomes.length; i++) {
            try {
                StringBuilder outputLine = new StringBuilder();
                outputLine.append(startingHands[i].toString());
                for (int j = 0; j < outcomes[i].length; j++) {
                    outputLine.append("\t" + outcomes[i][j].toValueString());
                }

                if (i != outcomes.length - 1)
                    outputLine.append(System.lineSeparator());

                out.write(outputLine.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads an array of outcome arrays from a raw output file (not to be confused with a binary .dat file).
     * @param path The file path.
     * @return The stored outcomes.
     */
    static Outcome[][] loadFromRawOutputFile(String path) {
        Outcome[][] outcome = new Outcome[StartingHand.ALL_COUNT][];
        try (FileReader reader = new FileReader(path)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            int i = 0, ctr = 0;
            while (bufferedReader.ready()) {
                ctr++;
                String line = bufferedReader.readLine();
                if (line.trim().length() == 0) {
                    continue;
                }
                if (ctr == 1) {
                    continue; // skip header
                }
                outcome[i] = new Outcome[StartingHand.ALL_COUNT];

                String[] parts = line.split("\\s+"); // split at whitespace (e.g. tab, space)
                for (int j = parts.length - 1, k = StartingHand.ALL_COUNT; j >= 2; j--) {
                    if (parts[j].trim().length() == 0) {
                        continue;
                    }

                    int total = Integer.parseInt(parts[j].trim()),
                            split = Integer.parseInt(parts[--j].trim()),
                            win = Integer.parseInt(parts[--j].trim());

                    if (((total != 0) && (total != 1712304)) || ((win + split) > total)) {
                        throw new IllegalArgumentException("Data file corrupted.");
                    }

                    int loss = total - win - split;
                    outcome[i][--k] = new Outcome(win, split, loss);
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outcome;
    }
}
