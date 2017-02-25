package com.timodenk.poker;

public class PocketCards {
    public Card card1, card2;

    public PocketCards(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    @Override
    public String toString() {
        return card1 + " " + card2;
    }
}
