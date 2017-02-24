package com.timodenk.poker;

public enum Suit {
    HEARTS('♥'),
    DIAMONDS('♦'),
    SPADES('♠'),
    CLUBS('♣');

    public final char shortForm;

    Suit(char shortForm) {
        this.shortForm = shortForm;
    }

    @Override
    public String toString() {
        return String.valueOf(this.shortForm);
    }
}