package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

public class PokerBoardAssessment {
    public static void main(String[] args) {
        //PocketCardsWinRate.analyseAllWinRates(5000, 4);
        PocketCardsWinRate.analyseWinRates(100000, 1);
    }

    private static void pokerHandProbabilities(int iterations) {
        int[] handNameOccurrences = new int[HandName.values().length];
        for (int i = 0; i < iterations; i++) {
            Deck deck = new Deck();

            Card[] cards = deck.getNCards(7);
            Hand hand = Poker.getBestHandFromCards(cards);
            handNameOccurrences[hand.name.ordinal()]++;
            if (i % (iterations / 100) == 0) {
                System.out.print(String.valueOf(i) + "\t");
                System.out.println(hand.toString());
            }
        }

        System.out.println();
        for (int i = 0; i < handNameOccurrences.length; i++) {
            System.out.println(Math.round(((double)handNameOccurrences[i] / (double)iterations) * 100000000d) / 1000000d
                    + "% \t"
                    + HandName.values()[i]
                    + " (" + handNameOccurrences[i] + ")");
        }
    }
}