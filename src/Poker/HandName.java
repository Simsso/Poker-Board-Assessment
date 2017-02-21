package Poker;

/**
 * Created by Denk on 21/02/17.
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

    public final String shortForm;

    HandName(String shortForm) {
        this.shortForm = shortForm;
    }

    @Override
    public String toString() {
        return this.shortForm;
    }
}
