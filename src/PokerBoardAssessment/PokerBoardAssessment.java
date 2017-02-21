package PokerBoardAssessment;

import Poker.*;

/**
 * Created by Denk on 21/02/17.
 */
public class PokerBoardAssessment {
    private static final int ITERATIONS = 100000000;


    public static void main(String[] args) {
        int[] handNameOccurences = new int[HandName.values().length];
        for (int i = 0; i < ITERATIONS; i++) {
            Deck deck = new Deck();

            Card[] fiveCards = deck.getNCards(7);
            Hand hand = Poker.getBestHandFromCards(fiveCards);
            handNameOccurences[hand.name.ordinal()]++;
            if (i % (ITERATIONS / 100) == 0) {
                System.out.print(String.valueOf(i) + "\t");
                System.out.println(hand.toString());
            }
        }

        System.out.println();
        for (int i = 0; i < handNameOccurences.length; i++) {
            System.out.println(Math.round(((double)handNameOccurences[i] / (double)ITERATIONS) * 100000000d) / 1000000d
                    + "% \t"
                    + HandName.values()[i]
                    + " (" + handNameOccurences[i] + ")");
        }
    }
}
