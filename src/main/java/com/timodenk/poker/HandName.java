package com.timodenk.poker;

/**
 * Enumeration for hand names, e.g. Royal Flush, Four of a Kind, ...
 */
public enum HandName {
    // smaller ordinal represents a better hand
    ROYAL_FLUSH("Royal Flush"),
    STRAIGHT_FLUSH("Straight Flush"),
    FOUR_OF_A_KIND("Four of a Kind"),
    FULL_HOUSE("Full House"),
    FLUSH("Flush"),
    STRAIGHT("Straight"),
    THREE_OF_A_KIND("Three of a Kind"),
    TWO_PAIR("Two Pair"),
    PAIR("Pair"),
    HIGH_CARD("High Card");

    public final String name;

    /**
     * Constructor
     * @param name The name of the hand as a {@link String}.
     */
    HandName(String name) {
        this.name = name;
    }

    /**
     * Getter method for the {@code name} attribute.
     * @return The {@code name} attribute.
     */
    @Override
    public String toString() {
        return this.name;
    }
}
