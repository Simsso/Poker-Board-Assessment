package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

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

    public static final int THREAD_COUNT = 4, // number of threads
            DEFAULT_ITERATIONS = 10000; // number of default iterations per game situation

    /**
     * Determines the win probabilities for all possible starting hands statistically.
     * All starting hands means that for example ("6 of spades", "7 of spades") will be assessed as well as ("7 of spades", "6 of spades") although the outcome is the same since the order does not matter.
     * This approach is by far less inefficient than possible.
     * @param opponents Number of opponents (randomly generated pocket cards) that play against the pocket cards.
     * @param iterations Number of iterations; the higher, the more precise is the outcome.
     * @return Array of {@link PocketCardsOutcome} objects holding all possible starting hands with their statistical outcome.
     */
    static PocketCardsOutcome[] allPocketCards(int opponents, int iterations) {
        List<PocketCardsOutcome> outcomes = new ArrayList<PocketCardsOutcome>();
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                for (Suit suit1 : Suit.values()) {
                    for (Suit suit2 : Suit.values()) {
                        if (rank1 == rank2 && suit1 == suit2) {
                            continue; // not possible
                        }
                        Deck deck = Card.getDeck();
                        try {
                            PocketCards pocketCards = new PocketCards(deck.takeCard(rank1, suit1), deck.takeCard(rank2, suit2));
                            outcomes.add(new PocketCardsOutcome(pocketCards, assess(deck, onePlayerPocketCards(pocketCards, opponents), iterations)[0]));
                        } catch (DeckStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return outcomes.toArray(new PocketCardsOutcome[0]);
    }

    /**
     * Statistically determines the average outcome for pocket hands.
     * Opposed to trying all possible hands it takes only significant ones, meaning e.g. ("two of spades", "three of clubs") is considered the same as ("two of spades", "three of hearts"), since the win probabilities for both pocket hands are the same.
     * In other words it determines the outcome for all ranks combinations suited and off-suit.
     * @param opponents Number of opponents (randomly generated pocket cards) that play against the pocket cards.
     * @param iterations Number of iterations; the higher, the more precise is the outcome.
     * @return Array of {@link PocketCardsOutcome} objects holding all possible starting hands with their statistical outcome.
     */
    static PocketCardsOutcome[] significantPocketCards(int opponents, int iterations) {
        List<PocketCardsOutcome> outcomes = new ArrayList<PocketCardsOutcome>();
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                if (rank2.ordinal() > rank1.ordinal()) {
                    continue; // combination occurs the other way around
                }

                // representative for off-suit combinations
                Suit suit1 = Suit.CLUBS;
                Suit suit2 = Suit.SPADES;

                try {
                    Deck deck1 = Card.getDeck();
                    PocketCards pocketCards = new PocketCards(deck1.takeCard(rank1, suit1), deck1.takeCard(rank2, suit2));
                    PocketCardsOutcome outcome = new PocketCardsOutcome(pocketCards, assess(
                            deck1,
                            onePlayerPocketCards(pocketCards, opponents),
                            iterations)[0]);
                    outcomes.add(outcome);

                } catch (DeckStateException e) {
                    e.printStackTrace();
                }

                if (rank1 != rank2) {
                    // suited combinations possible
                    suit2 = suit1;
                    try {
                        Deck deck2 = Card.getDeck();
                        PocketCards pocketCards = new PocketCards(deck2.takeCard(rank1, suit1), deck2.takeCard(rank2, suit2));
                        PocketCardsOutcome outcome = new PocketCardsOutcome(pocketCards, assess(
                                deck2,
                                onePlayerPocketCards(pocketCards, opponents),
                                iterations)[0]);
                        outcomes.add(outcome);

                    } catch (DeckStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return outcomes.toArray(new PocketCardsOutcome[0]);
    }

    /**
     * Statistically assesses any given poker situation for one player where all cards are unknown.
     * Useful for determining the probability for hands.
     * The method is multi-threaded.
     * @return Array of {@link Outcome} objects of which each is connected to exactly one {@link PocketCards} object (in the same order as passed in the pocket cards parameter).
     */
    static Outcome[] assess() {
        return assess(Card.getDeck(), new PocketCards[] { null }, DEFAULT_ITERATIONS);
    }

    /**
     * Statistically assesses any given poker situation for all participating players where the community cards are unknown.
     * The method is multi-threaded.
     * @param deck A deck of cards that the other cards which are passed as parameters are taken from.
     * @param pocketCards Array holding the pocket cards of the players at the table. The number of array element defines the number of players, for unknown cards or pocket hands leave the value at {@code null}.
     * @param iterations Number of iterations for determining the outcomes. Common are values higher than 10,000.
     * @return Array of {@link Outcome} objects of which each is connected to exactly one {@link PocketCards} object (in the same order as passed in the pocket cards parameter).
     */
    static Outcome[] assess(final Deck deck, final PocketCards[] pocketCards, final int iterations) {
        return assess(deck, pocketCards, null, new Card[0], iterations);
    }

    /**
     * Statistically assesses any given poker situation for all participating players.
     * The method is multi-threaded.
     * @param deck A deck of cards that the other cards which are passed as parameters are taken from.
     * @param pocketCards Array holding the pocket cards of the players at the table. The number of array element defines the number of players, for unknown cards or pocket hands leave the value at {@code null}.
     * @param communityCards Array of community cards. For unknown cards leave the value at null. If all cards are unknown null can be passed.
     * @param takenCards Array of cards that are and not in the game anymore; like flashed or folded cards.
     * @param iterations Number of iterations for determining the outcomes. Common are values higher than 10,000.
     * @return Array of {@link Outcome} objects of which each is connected to exactly one {@link PocketCards} object (in the same order as passed in the pocket cards parameter).
     */
    static Outcome[] assess(final Deck deck, final PocketCards[] pocketCards, final Card[] communityCards, final Card[] takenCards, final int iterations) {
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

        Set<Callable<Outcome[]>> outcomesCallableSet = new HashSet<Callable<Outcome[]>>();

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
     * Since the {@code assess} method requests an array of pocket hands, many methods need to create such an array based on one {@link PocketCards} object and a number of opponents.
     * The function generates the array with null values for all opponent pocket cards (telling the {@code assess} method to randomly generate those).
     * @param player Pocket cards of the player.
     * @param opponents Number of opponents playing against the player.
     * @return Array of {@link PocketCards} objects with null for all opponents and the first element being the passed player's pocket cards.
     */
    private static PocketCards[] onePlayerPocketCards(PocketCards player, int opponents) {
        PocketCards[] pocketCards = new PocketCards[opponents + 1];
        pocketCards[0] = player;
        return pocketCards;
    }
}
