package com.timodenk.poker;

public class Card implements Comparable<Card> {
    final Rank rank;
    final Suit suit;

    Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public int compareTo(Card o) {
        return this.rank.ordinal() - o.rank.ordinal();
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.rank.toString(), this.suit.toString());
    }
}