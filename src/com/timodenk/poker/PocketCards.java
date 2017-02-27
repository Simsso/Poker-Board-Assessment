package com.timodenk.poker;

/**
 * The {@link PocketCards} class contains two {@link Card}s.
 * Both cards should be taken from the same {@link Deck}.
 */
public class PocketCards {
    public Card card1, card2; // both cards

    /**
     * Two pocker cards (no specific order).
     * @param card1 A card.
     * @param card2 Another card.
     */
    public PocketCards(Card card1, Card card2) {
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
}
