package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Class containing the actual assessment logic that {@link Assessment} uses.
 * This logic is outsourced because the assessment has to be conducted on multiple threads.
 * The worker pool that all threads access contains objects of this type (and therefore requires an implementation of {@link Callable}.
 */
class AssessmentCallable implements Callable<Outcome[]> {
    // one deck to work with (all cards will be taken from this deck and the deck will be shuffled/reset subsequent to every iteration)
    private final Deck deck;

    // initial pocket cards of all players holding (holding null for randomly generated ones)
    private final DeckStartingHand[] startingHandsInitial;

    // pocket cards of all players, modified very frequently during execution (for every iteration)
    private DeckStartingHand[] pocketCards;

    // taken cards (cards that are not available anymore / taken from the deck, but not at any of the players hands or on the board)
    private final DeckCard[] takenCards,

    // initial community cards (null values for cards that are not known yet)
    communityCardsInitial;

    // community cards without null values (cards to work with; will be modified during every iteration unless all cards are predetermined)
    private DeckCard[] communityCards = new DeckCard[5];

    private final int iterations, // number of iterations (for this thread, not for the entire assessment)
            playerCount; // number of players (equal to the length of the {@code pocketCards} attribute)

    /**
     * Constructor for a thread's assessment task.
     * @param deck A deck of cards to work with. All other cards that are passed need to be taken from that deck.
     * @param pocketCards Array of {@link StartingHand} objects. The array length determines the number of players who have not folded their cards yet.
     * @param communityCards Array holding the community cards (null for unknown cards or shorter length).
     * @param takenCards Array of cards that are known to be not in the game anymore (e.g. flashed or openly folded cards). These cards will not be taken from the deck for random filling of pocket or community cards.
     * @param iterations Number of iterations for this thread, not for the entire assessment.
     */
    AssessmentCallable(final Deck deck, final DeckStartingHand[] pocketCards, final DeckCard[] communityCards, final DeckCard[] takenCards, final int iterations) {
        this.deck = deck;
        this.startingHandsInitial = pocketCards;
        this.communityCardsInitial = communityCards;
        this.takenCards = takenCards;

        this.iterations = iterations;

        this.playerCount = pocketCards.length;
    }

    /**
     * Executes the actual statistical analysis of a game situation in a single thread.
     * @return {@link Outcome} object for every element in the {@code pocketCards} parameter that was passed to the constructor of this class.
     * @throws DeckStateException Thrown if the number of players is too high, the same card occurs twice, passed cards are not members of the passed deck, or for any other deck related anomalies.
     */
    @Override
    public Outcome[] call() throws DeckStateException {
        Hand[] playerHands = new Hand[playerCount];

        Outcome[] outcome = new Outcome[playerCount]; // initialize outcome array
        for (int i = 0; i < playerCount; i++) {
            outcome[i] = new Outcome();
        }

        for (int i = 0; i < iterations; i++) {
            deck.shuffle(); // reset deck for every iteration

            deck.takeCards(communityCardsInitial); // community cards can not be taken by other players
            deck.takeCards(takenCards); // known to be not in the deck anymore

            fillPocketCards();
            fillCommunityCards();

            Card[] tmp7Cards = new Card[7];
            for (int j = 0; j < playerCount; j++) {
                join(tmp7Cards, communityCards, pocketCards[j]);
                try {
                    playerHands[j] = Poker.getBestHand(tmp7Cards);
                }
                catch (NullPointerException e) {
                    throw new DeckStateException("Same card was taken from the deck multiple times.");
                }
            }

            List<Hand> winningHands = getWinningHands(playerHands);

            for (int j = 0; j < playerCount; j++) {
                Outcome playerOutcome = outcome[j];
                if (winningHands.contains(playerHands[j])) {
                    if (winningHands.size() == 1) {
                        playerOutcome.addWin(playerHands[j]);
                    }
                    else {
                        playerOutcome.addSplit(playerHands[j]);
                    }
                }
                else {
                    playerOutcome.addLoss(playerHands[j]);
                }
            }
        }
        return outcome;
    }

    /**
     * In order to assess a players situation at a table it is necessary to find the best hand which the player can build out of their pocket cards and the community cards.
     * Therefore both need to be joined into one array of cards for further processing by the {@link Poker} class.
     * @param out Array of all seven cards.
     * @param communityCards Five community cards.
     * @param playerCards Two pocket cards of a player.
     */
    private static void join(Card[] out, Card[] communityCards, StartingHand playerCards) {
        System.arraycopy(communityCards, 0, out, 0, 5);
        out[5] = playerCards.card1;
        out[6] = playerCards.card2;
    }

    /**
     * For every iteration the pocket cards of all players will be filled, where they are set to {@code null}.
     * @throws DeckStateException Thrown if there is an error with the deck of cards (e.g. not enough cards available for the number of players).
     */
    private void fillPocketCards() throws DeckStateException {
        pocketCards = new DeckStartingHand[playerCount];
        for (int i = 0; i < playerCount; i++) {
            DeckCard card1, card2;
            if (startingHandsInitial[i].card1 == null) {
                card1 = deck.getNextCard();
            }
            else {
                card1 = startingHandsInitial[i].card1;
                deck.takeCards(card1);
            }

            if (startingHandsInitial[i].card2 == null) {
                card2 = deck.getNextCard();
            }
            else {
                card2 = startingHandsInitial[i].card2;
                deck.takeCards(card2);
            }
            pocketCards[i] = new DeckStartingHand(card1, card2);
        }
    }

    /**
     * For every iteration the unknown community cards will be determined by taking a random cards from the deck.
     * Needs to be called even if all community cards are known, because they have to be taken from the deck.
     * @throws DeckStateException Thrown if there occurs an error when taking cards from the deck.
     */
    private void fillCommunityCards() throws DeckStateException {
        deck.takeCards(communityCardsInitial); // community cards can not be taken by other players
        for (int i = 0; i < 5; i++) {
            if (!(i < communityCardsInitial.length) || communityCardsInitial[i] == null) {
                this.communityCards[i] = deck.getNextCard();
            }
            else {
                this.communityCards[i] = communityCardsInitial[i];
            }
        }
    }

    /**
     * After building all player's hands the best hand(s) need to be found.
     * In case of a split pot that can be several hands, in most of the cases it is just one hand (e.g. a Royal Flush).
     * @param hands Hands of all players.
     * @return One hand if one player has the best hand or in case of a split pot the player's hands that participate at the split.
     */
    private List<Hand> getWinningHands(final Hand[] hands) {
        List<Hand> sortedHands = new ArrayList<Hand>();
        Collections.addAll(sortedHands, hands);

        Collections.sort(sortedHands); // best has highest index

        for (int i = 0; i < sortedHands.size() - 1; i++) {
            if (sortedHands.get(i).compareTo(sortedHands.get(i + 1)) != 0) {
                for (int j = 0; j <= i; ) {
                    sortedHands.remove(sortedHands.get(i));
                    i--;
                }
            }
        }
        return sortedHands;
    }
}
