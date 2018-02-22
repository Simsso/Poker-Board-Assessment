package com.timodenk.poker;

public class CommunityCards {
    private static CommunityCards[] ALL_COMBINATIONS = null;

    private Card[] flop = new Card[3];
    private Card turn, river;

    public static final int COMBINATIONS_COUNT = (int)Util.binomial(52, 5),
        CARDS_COUNT = 5;

    public CommunityCards(Card[] flop, Card turn, Card river) {
        setFlop(flop);
        setTurn(turn);
        setRiver(river);
    }

    public Card[] getFlop() {
        return flop;
    }

    public Card getTurn() {
        return turn;
    }

    public Card getRiver() {
        return river;
    }

    public Card[] getAll() {
        return new Card[] { flop[0], flop[1], flop[2], turn, river };
    }

    public Card[] getAllAndAppend(StartingHand hand) {
        return new Card[] { flop[0], flop[1], flop[2], turn, river, hand.card1, hand.card2 };
    }

    public void setFlop(Card[] flop) {
        if (flop.length != 3) {
            throw new IllegalArgumentException("Flop array must contain exactly 3 cards. Only " + flop.length + " were found.");
        }
        this.flop = flop;
    }

    public void setTurn(Card turn) {
        this.turn = turn;
    }

    public void setRiver(Card river) {
        this.river = river;
    }

    private static CommunityCards[] initCombinations() {
        CommunityCards[] communityCards = new CommunityCards[COMBINATIONS_COUNT];
        Deck deck = new Deck();
        for (int i = 0, n = 0; i < Deck.CARDS_COUNT; i++) {
            for (int j = i + 1; j < Deck.CARDS_COUNT; j++) {
                for (int k = j + 1; k < Deck.CARDS_COUNT; k++) {
                    for (int l = k + 1; l < Deck.CARDS_COUNT; l++) {
                        for (int m = l + 1; m < Deck.CARDS_COUNT; m++, n++) {
                            communityCards[n] = new CommunityCards(
                                    new Card[] { deck.getNthCard(i), deck.getNthCard(j), deck.getNthCard(k) },
                                    deck.getNthCard(l),
                                    deck.getNthCard(m));
                        }
                    }
                }
            }
        }
        return communityCards;
    }

    public static CommunityCards[] getAllCombinations() {
        return ((ALL_COMBINATIONS == null) ? (ALL_COMBINATIONS = initCombinations()) : ALL_COMBINATIONS);
    }

    @Override
    public String toString() {
        return this.flop[0] + " " + this.flop[1] + " " + this.flop[2] + " | " + this.turn + " | " + this.river;
    }
}
