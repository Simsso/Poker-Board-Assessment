package com.timodenk.poker;

public class DeckCard extends Card implements Comparable<Card> {
    public final Deck deck;

    /**
     * Only possible constructor defining all properties of a card.
     * Constructor is defined with package visibility because cards should always be taken from a {@link Deck} instead of creating them.
     * @param rank The rank (e.g. 2, 3, ...)
     * @param suit The suit (e.g. spades, diamonds, ...)
     */
    DeckCard(Deck deck, Rank rank, Suit suit) {
        super(rank, suit);
        this.deck = deck;
    }

    public boolean equals(DeckCard card) {
        return super.equals(card) && this.deck == card.deck;
    }
}
