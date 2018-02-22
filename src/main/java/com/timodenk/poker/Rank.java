package com.timodenk.poker;

/**
 * Poker card rank enumeration.
 * Can be compared with another rank.
 */
public enum Rank implements Comparable<Rank> {
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    TEN('T'),
    JACK('J'),
    QUEEN('Q'),
    KING('K'),
    ACE('A');

    public final char shortForm;

    /**
     * Constructor for an enum entry.
     * @param shortForm Short form for the rank, e.g. Ace is 'A'.
     */
    Rank(char shortForm) {
        this.shortForm = shortForm;
    }

    /**
     * @return The short form attribute.
     */
    @Override
    public String toString() {
        return String.valueOf(this.shortForm);
    }
}
