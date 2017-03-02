package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

public class PocketCardAnalysis {
    static PocketCards[] getSignificantPocketCards() {
        return getSignificantPocketCards(Suit.DIAMONDS, Suit.SPADES);
    }

    static PocketCards[] getSignificantPocketCards(Suit suit1, Suit suit2) {
        if (suit1 == suit2) {
            throw new IllegalArgumentException("Suits must be different");
        }

        PocketCards[] pocketCards = new PocketCards[169];
        int i = 0;
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                if (rank2.ordinal() > rank1.ordinal()) {
                    continue; // combination occurs the other way around
                }

                pocketCards[i++] = new PocketCards(new Card(rank1, suit1), new Card(rank2, suit2));

                if (rank1 != rank2) {
                    // suited combinations possible
                    pocketCards[i++] = new PocketCards(new Card(rank1, suit1), new Card(rank2, suit1));
                }
            }
        }
        return pocketCards;
    }

    static PocketCardsOutcome[][] getSignificantPocketCardsHeadsUp(int iterations) {
        PocketCards[] allPockets1 = getSignificantPocketCards(Suit.CLUBS, Suit.DIAMONDS),
                allPockets2 = getSignificantPocketCards(Suit.SPADES, Suit.HEARTS);

        PocketCardsOutcome[][] outcomes = new PocketCardsOutcome[allPockets1.length][allPockets2.length];
        for (int i = 0; i < allPockets1.length; i++) {
            System.out.println((double)i / allPockets1.length);
            for (int j = 0; j < allPockets2.length; j++) {
                try {
                    Deck deck = new Deck();
                    PocketCards pocket1 = null;
                    pocket1 = deck.takeCardsLike(allPockets1[i]);
                    PocketCards pocket2 = deck.takeCardsLike(allPockets2[j]);
                    outcomes[i][j] = new PocketCardsOutcome(pocket1, Assessment.assess(deck, new PocketCards[] { pocket1, pocket2 }, iterations)[0]);
                } catch (DeckStateException e) {
                    e.printStackTrace();
                }
            }
        }
        return outcomes;
    }
}
