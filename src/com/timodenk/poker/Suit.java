package com.timodenk.poker;

/**
 * Poker card suit enumeration.
 * Suits can not be compared with each other.
 */
public enum Suit {
    HEARTS('\u2665'), // UTF symbol for hearts
    DIAMONDS('\u2666'), // UTF symbol for diamonds
    SPADES('\u2660'), // UTF symbol for spades
    CLUBS('\u2663'); // UTF symbol for clubs

    public final char shortForm; // short form (UTF character)

    /**
     * Constructor method.
     * @param shortForm Charakter that describes the suit (here the corresponding UTF symbol).
     */
    Suit(char shortForm) {
        this.shortForm = shortForm;
    }

    /**
     * @return The attribute {@code shortForm}.
     */
    @Override
    public String toString() {
        return String.valueOf(this.shortForm);
    }
}