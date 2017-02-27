package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

public class Program {
    public static void main(String[] args) {
        Deck deck = new Deck();

        Outcome[] playerOutcomes;
        try {
            playerOutcomes = Assessment.assess(
                    deck,
                    new PocketCards[] {
                            new PocketCards(
                                    deck.takeCard(Rank.ACE, Suit.SPADES),
                                    deck.takeCard(Rank.QUEEN, Suit.SPADES)
                            ),
                            null
                    },
                    new Card[] {
                            deck.takeCard(Rank.KING, Suit.SPADES),
                            deck.takeCard(Rank.SEVEN, Suit.DIAMONDS),
                            deck.takeCard(Rank.TEN, Suit.SPADES)
                    },
                    new Card[0],
                    100000
            );
            System.out.println(playerOutcomes[0].toTable());
            System.out.println(HandName.ROYAL_FLUSH + ": " + playerOutcomes[0].getWinRate(HandName.ROYAL_FLUSH));
        } catch (DeckStateException e) {
            e.getMessage();
        }

        System.out.println();
        deck.shuffle();

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

            playerOutcomes = Assessment.assess(
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
            e.getMessage();
        }

        System.out.println();

        System.out.println(Assessment.assess()[0].toTable());

        System.out.println();

        PocketCardsOutcome[] outcomes = Assessment.significantPocketCards(1, Assessment.DEFAULT_ITERATIONS);
        for (PocketCardsOutcome pocketCardsOutcome : outcomes) {
            System.out.println(pocketCardsOutcome.toString());
        }
    }
}