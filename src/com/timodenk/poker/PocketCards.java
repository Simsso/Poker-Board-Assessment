package com.timodenk.poker;

public class PocketCards {
    public Card card1, card2;

    public PocketCards(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    public boolean isSuited() {
        return card1.suit == card2.suit;
    }

    public boolean isPair() {
        return card1.rank == card2.rank;
    }

    @Override
    public String toString() {
        return String.format("%s %s", card1, card2);
    }
}
