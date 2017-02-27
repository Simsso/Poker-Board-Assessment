package com.timodenk.poker;

public class DeckStateException extends Exception {
    private Card card = null;

    private Rank rank = null;
    private Suit suit = null;

    DeckStateException(Card card) {
        this.card = card;
    }

    DeckStateException(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public DeckStateException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        if (card != null) {
            return "Card was taken from the deck more than once: " + card.toString();
        }
        else if (rank != null && suit != null) {
            return "Card was already taken from the deck: " + (new Card(rank, suit)).toString();
        }
        else {
            return super.toString();
        }
    }
}
