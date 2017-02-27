package com.timodenk.poker;

public enum Suit {
    HEARTS('\u2665'),
    DIAMONDS('\u2666'),
    SPADES('\u2660'),
    CLUBS('\u2663');

    public final char shortForm;

    Suit(char shortForm) {
        this.shortForm = shortForm;
    }

    @Override
    public String toString() {
        return String.valueOf(this.shortForm);
    }
}