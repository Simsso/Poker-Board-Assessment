package com.timodenk.poker;

/**
 * Exceptions that occur when working with a {@link Deck}.
 */
public class DeckStateException extends Exception {
    private Card card = null; // the card which caused the exception

    private Rank rank = null; // the rank of the card which caused the exception
    private Suit suit = null; // the suit of the card which caused the exception

    /**
     * Constructor taking a card that caused the exception.
     * @param card The card.
     */
    DeckStateException(Card card) {
        this.card = card;
    }

    /**
     * Constructor that takes rank and suit that occured in context of the exception.
     * @param rank The rank.
     * @param suit The suit.
     */
    DeckStateException(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Constructor with an error description.
     * @param message Error description (message).
     */
    public DeckStateException(String message) {
        super(message);
    }

    /**
     * Summarizes the exception.
     * @return A string providing all information about the exception.
     */
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
