package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * This static class provides methods for assessment of given poker situation.
 * It also standardizes several situations like "one player, random cards, no opponents".
 * However, the most generalized function is called {@code assess} which takes any possible poker situation and assesses the outcome for all players.
 * All outcome determinations are based on statistical observation. This is the random generation of all undefined cards and outcome observation multiple times.
 *
 * The class is optionally performing the statistical observation on multiple threads for higher performance.
 */
class Assessment {

    public static final int THREAD_COUNT = 12, // number of threads
            DEFAULT_ITERATIONS = 10000; // number of default iterations per game situation

    /**
     * Determines the win probabilities for all possible starting hands statistically.
     * All starting hands means that for example ("6 of spades", "7 of spades") will be assessed as well as ("7 of spades", "6 of spades") although the outcome is the same since the order does not matter.
     * This approach is by far less inefficient than possible.
     * @param opponents Number of opponents (randomly generated pocket cards) that play against the pocket cards.
     * @param iterations Number of iterations; the higher, the more precise is the outcome.
     * @return Array of {@link StartingHandOutcome} objects holding all possible starting hands with their statistical outcome.
     */
    static StartingHandOutcome[] allPocketCards(int opponents, int iterations) {
        List<StartingHandOutcome> outcomes = new ArrayList<StartingHandOutcome>();
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                for (Suit suit1 : Suit.values()) {
                    for (Suit suit2 : Suit.values()) {
                        if (rank1 == rank2 && suit1 == suit2) {
                            continue; // not possible
                        }
                        Deck deck = new Deck();
                        try {
                            DeckStartingHand startingHand = new DeckStartingHand(deck.takeCard(rank1, suit1), deck.takeCard(rank2, suit2));
                            outcomes.add(new StartingHandOutcome(startingHand, assess(deck, onePlayerPocketCards(startingHand, opponents), iterations)[0]));
                        } catch (DeckStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return outcomes.toArray(new StartingHandOutcome[0]);
    }

    /**
     * Statistically determines the average outcome for pocket hands.
     * Opposed to trying all possible hands it takes only significant ones, meaning e.g. ("two of spades", "three of clubs") is considered the same as ("two of spades", "three of hearts"), since the win probabilities for both pocket hands are the same.
     * In other words it determines the outcome for all ranks combinations suited and off-suit.
     * @param opponents Number of opponents (randomly generated pocket cards) that play against the pocket cards.
     * @param iterations Number of iterations; the higher, the more precise is the outcome.
     * @return Array of {@link StartingHandOutcome} objects holding all possible starting hands with their statistical outcome.
     */
    static StartingHandOutcome[] significantPocketCards(int opponents, int iterations) {
        StartingHand[] significantPocketCards = StartingHand.getSignificant();
        StartingHandOutcome[] outcomes = new StartingHandOutcome[significantPocketCards.length];
        for (int i = 0; i < significantPocketCards.length; i++) {
            try {
                StartingHand startingHand = significantPocketCards[i];
                Deck deck = new Deck();
                DeckStartingHand startingHandSameDeck = null;
                startingHandSameDeck = new DeckStartingHand(deck, startingHand.card1, startingHand.card2);
                outcomes[i] = new StartingHandOutcome(startingHandSameDeck, Assessment.assess(deck, onePlayerPocketCards(startingHandSameDeck, opponents), iterations)[0]);
            } catch (DeckStateException e) {
                e.printStackTrace();
            }
        }
        return outcomes;
    }

    /**
     * Statistically assesses any given poker situation for one player where all cards are unknown.
     * Useful for determining the probability for hands.
     * The method is multi-threaded.
     * @return Array of {@link Outcome} objects of which each is connected to exactly one {@link DeckStartingHand} object (in the same order as passed in the pocket cards parameter).
     */
    static Outcome[] assess() {
        return assess(new Deck(), new DeckStartingHand[] { null }, DEFAULT_ITERATIONS);
    }

    /**
     * Statistically assesses any given poker situation for all participating players where the community cards are unknown.
     * The method is multi-threaded.
     * @param deck A deck of cards that the other cards which are passed as parameters are taken from.
     * @param pocketCards Array holding the pocket cards of the players at the table. The number of array element defines the number of players, for unknown cards or pocket hands leave the value at {@code null}.
     * @param iterations Number of iterations for determining the outcomes. Common are values higher than 10,000.
     * @return Array of {@link Outcome} objects of which each is connected to exactly one {@link StartingHand} object (in the same order as passed in the pocket cards parameter).
     */
    static Outcome[] assess(final Deck deck, final DeckStartingHand[] pocketCards, final int iterations) {
        return assess(deck, pocketCards, null, new DeckCard[0], iterations);
    }

    /**
     * Statistically assesses any given poker situation for all participating players.
     * The method is multi-threaded.
     * @param deck A deck of cards that the other cards which are passed as parameters are taken from.
     * @param pocketCards Array holding the pocket cards of the players at the table. The number of array element defines the number of players, for unknown cards or pocket hands leave the value at {@code null}.
     * @param communityCards Array of community cards. For unknown cards leave the value at null. If all cards are unknown null can be passed.
     * @param takenCards Array of cards that are and not in the game anymore; like flashed or folded cards.
     * @param iterations Number of iterations for determining the outcomes. Common are values higher than 10,000.
     * @return Array of {@link Outcome} objects of which each is connected to exactly one {@link StartingHand} object (in the same order as passed in the pocket cards parameter).
     */
    static Outcome[] assess(final Deck deck, final DeckStartingHand[] pocketCards, final DeckCard[] communityCards, final DeckCard[] takenCards, final int iterations) {
        final long start = System.nanoTime(); // performance

        final int playerCount = pocketCards.length;

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

        Set<Callable<Outcome[]>> outcomesCallableSet = new HashSet<Callable<Outcome[]>>();

        for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
            int currentThreadIterations = iterations / THREAD_COUNT;
            if (threadId == THREAD_COUNT - 1) {
                currentThreadIterations += iterations % THREAD_COUNT;
            }

            // objects for each thread
            Deck threadDeck = deck.clone();
            DeckCard[] threadCommunityCards = new DeckCard[5];
            DeckCard[] threadTakenCards = new DeckCard[takenCards.length];
            DeckStartingHand[] threadPocketCards = new DeckStartingHand[playerCount];

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
                        threadPocketCards[i] = new DeckStartingHand(null, null);
                    }
                    else if (pocketCards[i].card1 != null && pocketCards[i].card2 == null) {
                        threadPocketCards[i] = new DeckStartingHand(threadDeck.getCardLike(pocketCards[i].card1), null);
                    }
                    else if (pocketCards[i].card1 == null && pocketCards[i].card2 != null) {
                        threadPocketCards[i] = new DeckStartingHand(null, threadDeck.getCardLike(pocketCards[i].card2));
                    }
                    else {
                        threadPocketCards[i] = new DeckStartingHand(
                                threadDeck.getCardLike(pocketCards[i].card1),
                                threadDeck.getCardLike(pocketCards[i].card2));
                    }
                }
            } catch (DeckStateException e) {
                e.printStackTrace();
            }

            outcomesCallableSet.add(new AssessmentCallable(
                    threadDeck, // all threads have different deck objects with the same properties
                    threadPocketCards,
                    threadCommunityCards,
                    threadTakenCards,
                    currentThreadIterations));
        }

        try {
            List<Future<Outcome[]>> futures = executorService.invokeAll(outcomesCallableSet);

            for (Future<Outcome[]> threadOutcomeFuture : futures) {
                Outcome[] threadOutcome = threadOutcomeFuture.get();
                for (int i = 0; i < playerCount; i++) {
                    outcome[i].merge(threadOutcome[i]);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        // performance measurements
        //System.out.printf("%9.4f µs (x%d hands)\n", (double)(System.nanoTime() - start) / 1e3 / Outcome.getCount(outcome), Outcome.getCount(outcome));
        return outcome;
    }

    /**
     * Frequently pocket cards of one player are known but the pocket cards of all other players shall be determined randomly for each iteration.
     * Since the {@code assess} method requests an array of pocket hands, many methods need to create such an array based on one {@link StartingHand} object and a number of opponents.
     * The function generates the array with null values for all opponent pocket cards (telling the {@code assess} method to randomly generate those).
     * @param player Pocket cards of the player.
     * @param opponents Number of opponents playing against the player.
     * @return Array of {@link StartingHand} objects with null for all opponents and the first element being the passed player's pocket cards.
     */
    private static DeckStartingHand[] onePlayerPocketCards(DeckStartingHand player, int opponents) {
        DeckStartingHand[] pocketCards = new DeckStartingHand[opponents + 1];
        pocketCards[0] = player;
        return pocketCards;
    }

    static StartingHandOutcome[][] getStartingHandsHeadsUp(int iterations, OutputStream out, OutputStream status) {
        long startNanos = System.nanoTime();
        StartingHand[] allPockets1 = StartingHand.getAll(),
                allPockets2 = StartingHand.getAll();

        try {
            out.write("win\t".getBytes());
            for (int j = 0; j < StartingHand.ALL_COUNT; j++) {
                    out.write((allPockets2[j] + "\t").getBytes());
            }
            out.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        StartingHandOutcome[][] outcomes = new StartingHandOutcome[StartingHand.ALL_COUNT][StartingHand.ALL_COUNT];
        for (int i = 0; i < StartingHand.ALL_COUNT; i++) {
            try {
                if (i > 0) status.write(String.format("%04d %8.4fs hours remaining (%07.6f progress)\n", i, (System.nanoTime() - startNanos) / 1e9f / 3600.0 / i * (StartingHand.ALL_COUNT - i), (double)i / StartingHand.ALL_COUNT).getBytes()); // progress update
                out.write((allPockets1[i] + "\t").getBytes());

                for (int j = 0; j < StartingHand.ALL_COUNT; j++) {
                    DeckStartingHand pocket1 = null, pocket2 = null;
                    try {
                        Deck deck = new Deck();
                        pocket1 = deck.takeCardsLike(allPockets1[i]);
                                pocket2 = deck.takeCardsLike(allPockets2[j]);
                        outcomes[i][j] = new StartingHandOutcome(pocket1, Assessment.assess(deck, new DeckStartingHand[] { pocket1, pocket2 }, iterations)[0]);
                        out.write(String.format("%9.8f", outcomes[i][j].getWinRate()).getBytes(StandardCharsets.UTF_8));
                    } catch (DeckStateException e) {
                        outcomes[i][j] = null; // combination not possible
                        out.write("null".getBytes(StandardCharsets.UTF_8));
                    }
                    out.write("\t".getBytes(StandardCharsets.UTF_8));
                }
                out.write("\n".getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outcomes;
    }
}
