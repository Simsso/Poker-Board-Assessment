package com.timodenk.poker;

/**
 * A single card, mostly used in combination with the {@link Deck} that the card has been taken from.
 * Comparable to other cards ({@link Comparable}).
 */
public class Card implements Comparable<Card> {
    final Rank rank; // the card's rank
    final Suit suit; // the card's suit

    /**
     * Only possible constructor defining all properties of a card.
     *
     * @param rank The rank (e.g. 2, 3, ...)
     * @param suit The suit (e.g. spades, diamonds, ...)
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public boolean equals(Card card) {
        return this.rank == card.rank && this.suit == card.suit;
    }

    public Card getPermutation(Suit[] permutation) {
        return new Card(this.rank, this.suit.getPermutation(permutation));
    }

    /**
     * One card can be compared to another card based on the card's ranks.
     *
     * @param o Another card.
     * @return 0 if both cards have the same rank. 1 if this card has a higher rank. -1 if the other card has a higher rank.
     */
    @Override
    public int compareTo(Card o) {
        return this.rank.ordinal() - o.rank.ordinal();
    }

    /**
     * Converts the card into a string.
     *
     * @return String containing both suit and rank information. The suit is using the UTF symbols for card suits.
     */
    @Override
    public String toString() {
        return String.format("%s%s", this.rank.toString(), this.suit.toString());
    }

    /**
     * Converts the card into a string with ascii symbols and a comma between rank and suit.
     *
     * @return String containing both suit and rank information. The suit is using the UTF symbols for card suits.
     */
    public String toCommaAsciiString() {
        return String.format("'%s','%s'", this.rank.toString(), this.suit.toAscii());
    }
}
