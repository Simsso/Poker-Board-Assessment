package com.timodenk.poker;

/**
 * The {@link StartingHand} class contains two {@link Card}s.
 * For some applications both cards should be taken from the same {@link Deck}.
 */
public class StartingHand {
    public Card card1, card2; // both cards

    public static final int ALL_COUNT = 1326;

    /**
     * Two poker cards (no specific order).
     *
     * @param card1 A card.
     * @param card2 Another card.
     */
    public StartingHand(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    /**
     * @return True if both pocker cards have the same suit.
     */
    public boolean isSuited() {
        return card1.suit == card2.suit;
    }

    /**
     * @return True if both cards have the same rank.
     */
    public boolean isPair() {
        return card1.rank == card2.rank;
    }

    /**
     * @return Rank and suit of both cards separated by a whitespace.
     */
    @Override
    public String toString() {
        return String.format("%s %s", card1, card2);
    }

    public static StartingHand[] getSignificant() {
        return getSignificant(Suit.DIAMONDS, Suit.SPADES);
    }

    public static StartingHand[] getSignificant(Suit suit1, Suit suit2) {
        if (suit1 == suit2) {
            throw new IllegalArgumentException("Suits must be different");
        }

        StartingHand[] pocketCards = new StartingHand[169];
        int i = 0;
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                if (rank2.ordinal() > rank1.ordinal()) {
                    continue; // combination occurs the other way around
                }

                pocketCards[i++] = new StartingHand(new Card(rank1, suit1), new Card(rank2, suit2));

                if (rank1 != rank2) {
                    // suited combinations possible
                    pocketCards[i++] = new StartingHand(new Card(rank1, suit1), new Card(rank2, suit1));
                }
            }
        }
        return pocketCards;
    }

    public static StartingHand[] getAll() {
        StartingHand[] pocketCards = new StartingHand[ALL_COUNT];
        int i = 0;
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                if (rank2.ordinal() > rank1.ordinal()) {
                    continue; // combination occurs the other way around
                }

                for (Suit suit1 : Suit.values()) {
                    for (Suit suit2 : Suit.values()) {
                        if (rank1.ordinal() == rank2.ordinal() && suit1.ordinal() == suit2.ordinal()) {
                            continue; // no suited pairs
                        }
                        if (rank1.ordinal() == rank2.ordinal() && suit2.ordinal() > suit1.ordinal()) {
                            continue; // no flippable combination for pairs (e.g. not both, (2D,2H) and (2H,2D)
                        }

                        pocketCards[i++] = new StartingHand(new Card(rank1, suit1), new Card(rank2, suit2));
                    }
                }
            }
        }
        return pocketCards;
    }
}
