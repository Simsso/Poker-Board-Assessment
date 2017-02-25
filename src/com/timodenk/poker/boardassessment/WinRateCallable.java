package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

class WinRateCallable implements Callable<Outcome[]> {
    private final PocketCards[] pocketCardsInitial;
    private PocketCards[] pocketCards;
    private final Deck deck;
    private final Card[] communityCardsInitial, // for later usage it is necessary to know which community cards were set / known
        takenCards;
    private Card[] communityCards = new Card[5];

    private final int iterations, playerCount;

    // only pocket cards played information
    WinRateCallable(final Deck deck, final PocketCards[] pocketCards, final int iterations) {
        this(deck, pocketCards, new Card[5], new Card[0], iterations);
    }

    // more card information available
    WinRateCallable(final Deck deck, final PocketCards[] pocketCards, final Card[] communityCards, final Card[] takenCards, final int iterations) {
        this.deck = deck;
        this.pocketCardsInitial = pocketCards;
        this.communityCardsInitial = communityCards;
        this.takenCards = takenCards;

        this.iterations = iterations;

        this.playerCount = pocketCards.length;
    }

    @Override
    public Outcome[] call() throws Exception {
        Hand[] playerHands = new Hand[playerCount];

        Outcome[] outcome = new Outcome[playerCount]; // initialize outcome array
        for (int i = 0; i < playerCount; i++) {
            outcome[i] = new Outcome();
        }

        for (int i = 0; i < iterations; i++) {
            deck.shuffle();

            deck.takeCards(communityCardsInitial); // community cards can not be taken by other players
            deck.takeCards(takenCards); // known to be not in the deck anymore
            fillPocketCards();
            fillCommunityCards();

            Card[] tmp7Cards = new Card[7];
            for (int j = 0; j < playerCount; j++) {
                joinCommunityAndPocketCards(tmp7Cards, communityCards, pocketCards[j].card1, pocketCards[j].card2);
                try {
                    playerHands[j] = Poker.getBestHandFromCards(tmp7Cards);
                }
                catch (NullPointerException e) {
                    throw new DeckStateException("Same card was taken multiple times.");
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

    private static void joinCommunityAndPocketCards(Card[] out, Card[] communityCards, Card playerCard1, Card playerCard2) {
        for (int i = 0; i < 5; i++) {
            out[i] = communityCards[i];
        }
        out[5] = playerCard1;
        out[6] = playerCard2;
    }

    private void fillPocketCards() throws DeckStateException {
        pocketCards = new PocketCards[playerCount];
        for (int i = 0; i < playerCount; i++) {
            Card card1, card2;
            if (pocketCardsInitial[i].card1 == null) {
                card1 = deck.getNextCard();
            }
            else {
                card1 = pocketCardsInitial[i].card1;
                deck.takeCards(card1);
            }

            if (pocketCardsInitial[i].card2 == null) {
                card2 = deck.getNextCard();
            }
            else {
                card2 = pocketCardsInitial[i].card2;
                deck.takeCards(card2);
            }
            pocketCards[i] = new PocketCards(card1, card2);
        }
    }

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

    // returns one hand if one player has the best hand or in case of a split pot the player's hands that participate at the split
    private List<Hand> getWinningHands(final Hand[] hands) {
        List<Hand> sortedHands = new ArrayList<Hand>();
        for (int i = 0; i < hands.length; i++) {
            sortedHands.add(hands[i]);
        }

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
