package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

public class Program {
    public static void main(String[] args) {
        //WinRate.analyseWinRates(5000, 1);
        //WinRate.analyseWinRates(100000, 1);

        Deck deck = new Deck();

        try {
            System.out.println(WinRate.winRateFor(
                    deck,
                    // pocket cards
                    deck.takeCard(Rank.SIX, Suit.DIAMONDS),
                    deck.takeCard(Rank.EIGHT, Suit.HEARTS),
                    new Card[] { // community cards
                            deck.takeCard(Rank.THREE, Suit.CLUBS),
                            deck.takeCard(Rank.FOUR, Suit.SPADES),
                            deck.takeCard(Rank.FIVE, Suit.HEARTS),
                    },
                    500000, // iterations
                    2)); // opponents
        } catch (DeckStateException e) {
            e.printStackTrace();
        }

        deck.shuffle();

        System.out.println();

        try {
            System.out.println(WinRate.winRateFor(
                    deck,
                    deck.takeCard(Rank.NINE, Suit.CLUBS),
                    deck.takeCard(Rank.EIGHT, Suit.CLUBS),
                    new Card[] {
                            deck.takeCard(Rank.JACK, Suit.CLUBS),
                            deck.takeCard(Rank.TEN, Suit.CLUBS)
                    },
                    100000,
                    8).toTable());
        } catch (DeckStateException e) {
            e.printStackTrace();
        }

        deck.shuffle();

        System.out.println();

        try {
            System.out.println(WinRate.winRateFor(
                    deck,
                    deck.takeCard(Rank.SEVEN, Suit.CLUBS),
                    deck.takeCard(Rank.TWO, Suit.CLUBS),
                    100000,
                    8).toTable());
        } catch (DeckStateException e) {
            e.printStackTrace();
        }
    }

    private static void pokerHandProbabilities(int iterations) {
        int[] handNameOccurrences = new int[HandName.values().length];
        for (int i = 0; i < iterations; i++) {
            Deck deck = new Deck();

            Card[] cards = new Card[0];
            try {
                cards = deck.getNCards(7);
                // not going to throw because the deck has just been created above
            } catch (DeckStateException e) {
                e.printStackTrace();
            }
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