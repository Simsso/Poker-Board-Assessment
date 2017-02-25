package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

public class Program {
    public static void main(String[] args) {
        Deck deck = new Deck();
        try {
            PocketCards[] pocketCards = new PocketCards[] {
                    new PocketCards(
                            deck.takeCard(Rank.ACE, Suit.CLUBS),
                            deck.takeCard(Rank.FIVE, Suit.CLUBS)
                    ),
                    new PocketCards(
                            deck.takeCard(Rank.JACK, Suit.CLUBS),
                            deck.takeCard(Rank.TEN, Suit.DIAMONDS)
                    )/*,
                    new PocketCards(
                            deck.takeCard(Rank.ACE, Suit.SPADES),
                            deck.takeCard(Rank.TWO, Suit.HEARTS)
                    )*/
            };

            Outcome[] playerOutcomes = WinRate.winRateFor(
                    deck,
                    pocketCards,
                    new Card[] { // community cards
                            deck.takeCard(Rank.EIGHT, Suit.DIAMONDS),
                            deck.takeCard(Rank.SEVEN, Suit.CLUBS),
                            deck.takeCard(Rank.TWO, Suit.CLUBS),
                            deck.takeCard(Rank.ACE, Suit.DIAMONDS)

                    },
                    new Card[] { // folded / known cards
                            deck.takeCard(Rank.ACE, Suit.SPADES),
                            deck.takeCard(Rank.TWO, Suit.HEARTS)
                    },
                    50000);

            for (int i = 0; i < playerOutcomes.length; i++) {
                System.out.println(pocketCards[i] + " " + playerOutcomes[i].getWinRate());
            }
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