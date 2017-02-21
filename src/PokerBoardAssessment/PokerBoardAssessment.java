package PokerBoardAssessment;

import Poker.*;

/**
 * Created by Denk on 21/02/17.
 */
public class PokerBoardAssessment {
    private static final int ITERATIONS = 1000000;


    public static void main(String[] args) {
        int[] handNameOccurrences = new int[HandName.values().length];
        for (int i = 0; i < ITERATIONS; i++) {
            Deck deck = new Deck();

            Card[] fiveCards = deck.getNCards(7);
            Hand hand = Poker.getBestHandFromCards(fiveCards);
            handNameOccurrences[hand.name.ordinal()]++;
            if (i % (ITERATIONS / 100) == 0) {
                System.out.print(String.valueOf(i) + "\t");
                System.out.println(hand.toString());
            }
        }

        System.out.println();
        for (int i = 0; i < handNameOccurrences.length; i++) {
            System.out.println(Math.round(((double)handNameOccurrences[i] / (double)ITERATIONS) * 100000000d) / 1000000d
                    + "% \t"
                    + HandName.values()[i]
                    + " (" + handNameOccurrences[i] + ")");
        }
    }
}