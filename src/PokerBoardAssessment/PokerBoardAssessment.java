package PokerBoardAssessment;

import Poker.*;

/**
 * Created by Denk on 21/02/17.
 */
public class PokerBoardAssessment {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            Deck deck = new Deck();
            Card[] fiveCards = deck.getNCards(5);
            printCards(fiveCards);
            System.out.println(": " + Poker.getHandFromCards(fiveCards).name);
        }
    }

    private static void printCards(Card[] c) {
        for (int i = 0; i < c.length; i++) {
            if (i != 0) {
                System.out.print(" ");
            }
            System.out.print(c[i].toString());
        }
    }
}
