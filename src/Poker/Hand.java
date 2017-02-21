package Poker;

/**
 * Created by Denk on 21/02/17.
 */
public class Hand {
    public final HandName name;
    public final Card[] cards;

    public Hand(HandName name, Card[] cards) {
        if (cards.length != 5) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.cards = cards;
    }
}
