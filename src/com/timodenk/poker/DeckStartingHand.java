package com.timodenk.poker;

public class DeckStartingHand extends StartingHand {
    public final DeckCard card1, card2;
    public final Deck deck;

    public DeckStartingHand(final Deck deck) {
        super(null, null);
        card1 = null;
        card2 = null;
        this.deck = deck;
    }

    public DeckStartingHand(final DeckCard card1, final DeckCard card2) {
        super(card1, card2);
        this.card1 = card1;
        this.card2 = card2;
        if (card1.deck != card2.deck) {
            throw new IllegalArgumentException("Cards must be taken from the same deck");
        }
        this.deck = card1.deck;
    }

    public DeckStartingHand(final Deck deck, final Card paramCard1, Card paramCard2) throws DeckStateException {
        this(deck.takeCard(paramCard1), deck.takeCard(paramCard2));
    }
}
