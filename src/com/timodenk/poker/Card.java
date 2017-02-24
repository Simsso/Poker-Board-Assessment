package com.timodenk.poker;

public class Card implements Comparable<Card> {
    public final Rank rank;
    public final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public int compareTo(Card o) {
        return this.rank.ordinal() - o.rank.ordinal();
    }

    @Override
    public String toString() {
        return this.rank.toString() + this.suit.toString();
    }
}