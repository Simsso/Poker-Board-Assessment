package com.timodenk.poker;

/**
 * Poker card suit enumeration.
 * Suits can not be compared with each other.
 */
public enum Suit {
    HEARTS('\u2665', 'H'), // UTF symbol for hearts
    DIAMONDS('\u2666', 'D'), // UTF symbol for diamonds
    SPADES('\u2660', 'S'), // UTF symbol for spades
    CLUBS('\u2663', 'C'); // UTF symbol for clubs

    public final char shortForm, // short form (UTF character)
        ascii;

    /**
     * Constructor method.
     * @param shortForm Character that describes the suit (here the corresponding UTF symbol).
     */
    Suit(char shortForm, char ascii) {
        this.shortForm = shortForm;
        this.ascii = ascii;
    }

    /**
     * @return The attribute {@code shortForm}.
     */
    @Override
    public String toString() {
        return String.valueOf(this.shortForm);
    }

    public String toAscii() {
        return String.valueOf(this.ascii);
    }

    public Suit getPermutation(Suit[] permutation) {
        Suit[] base = Suit.values();
        if (permutation.length != base.length) {
            throw new IllegalArgumentException("Permutation array must have length 4");
        }
        for (int i = 0; i < base.length; i++) {
            if (base[i] == this) {
                return permutation[i];
            }
        }
        throw new IllegalArgumentException("Could not apply permutation");
    }

    public static Suit[][] getPermutations() {
        return Util.permute(Suit.values());
    }
}