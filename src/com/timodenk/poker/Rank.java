package com.timodenk.poker;

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

    Rank(char shortForm) {
        this.shortForm = shortForm;
    }

    @Override
    public String toString() {
        return String.valueOf(this.shortForm);
    }
}
