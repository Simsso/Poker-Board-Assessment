package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

public class Program {
    public static void main(String[] args) {

        Deck deck = new Deck();
        // [FOUND] AC 2C wins against TS 9S (0.083) which wins against 2H 2D (0.077) which wins against AC 2S (0.237)
        try {
            Outcome[] outcomes;
            PocketCards pc1 = new PocketCards(deck.takeCard(Rank.ACE, Suit.CLUBS), deck.takeCard(Rank.TWO, Suit.CLUBS)),
                    pc2 = new PocketCards(deck.takeCard(Rank.TEN, Suit.SPADES), deck.takeCard(Rank.NINE, Suit.SPADES));
            outcomes = Assessment.assess(deck, new PocketCards[] { pc1, pc2 }, 1000000);

            System.out.println(pc1 + ": " + outcomes[0].getWinRate());
            System.out.println(pc2 + ": " + outcomes[1].getWinRate());
        } catch (DeckStateException e) {
            e.printStackTrace();
        }

        deck.shuffle();
        System.out.println();

        try {
            Outcome[] outcomes;
            PocketCards pc1 = new PocketCards(deck.takeCard(Rank.TEN, Suit.SPADES), deck.takeCard(Rank.NINE, Suit.SPADES)),
                    pc2 = new PocketCards(deck.takeCard(Rank.TWO, Suit.HEARTS), deck.takeCard(Rank.TWO, Suit.DIAMONDS));
            outcomes = Assessment.assess(deck, new PocketCards[] { pc1, pc2 }, 1000000);

            System.out.println(pc1 + ": " + outcomes[0].getWinRate());
            System.out.println(pc2 + ": " + outcomes[1].getWinRate());
        } catch (DeckStateException e) {
            e.printStackTrace();
        }

        deck.shuffle();
        System.out.println();

        try {
            Outcome[] outcomes;
            PocketCards pc1 = new PocketCards(deck.takeCard(Rank.TWO, Suit.HEARTS), deck.takeCard(Rank.TWO, Suit.DIAMONDS)),
                    pc2 = new PocketCards(deck.takeCard(Rank.ACE, Suit.CLUBS), deck.takeCard(Rank.TWO, Suit.CLUBS));
            outcomes = Assessment.assess(deck, new PocketCards[] { pc1, pc2 }, 1000000);

            System.out.println(pc1 + ": " + outcomes[0].getWinRate());
            System.out.println(pc2 + ": " + outcomes[1].getWinRate());
        } catch (DeckStateException e) {
            e.printStackTrace();
        }

        deck.shuffle();
        System.out.println();

        // AC 2C: 0.538644
        // TS 9S: 0.457355

        // TS 9S: 0.530047
        // 2H 2D: 0.452776

        // 2H 2D: 0.611991
        // AC 2C: 0.37674


        final long start = System.nanoTime();
        final int iterations = 47232;
        PocketCardsOutcome[][] outcomeTable = PocketCardAnalysis.getSignificantPocketCardsHeadsUp(iterations);
        System.out.printf("\t");
        PocketCards[] differentSuit = PocketCardAnalysis.getSignificantPocketCards(Suit.SPADES, Suit.HEARTS);
        for (int i = 0; i < differentSuit.length; i++) {
            System.out.printf(differentSuit[i].toString() + "\t");
        }
        System.out.println();
        for (PocketCardsOutcome[] row : outcomeTable) {
            System.out.printf(row[0].getPocketCards().toString() + "\t");
            for (PocketCardsOutcome outcome : row) {
                System.out.printf("%6f\t", outcome.getWinRate());
            }
            System.out.println();
        }

        System.out.println((System.nanoTime() - start) / 1e9f / iterations);

        PocketCardsOutcome[] outcomes = Assessment.significantPocketCards(1, Assessment.DEFAULT_ITERATIONS);
        for (PocketCardsOutcome pocketCardsOutcome : outcomes) {
            System.out.println(pocketCardsOutcome.toString());
        }


        Outcome[] playerOutcomes;

        try {
            playerOutcomes = Assessment.assess(
                    deck,
                    new PocketCards[]{
                            new PocketCards(
                                    deck.takeCard(Rank.ACE, Suit.SPADES),
                                    deck.takeCard(Rank.EIGHT, Suit.SPADES)
                            ),
                            new PocketCards(
                                    deck.takeCard(Rank.KING, Suit.SPADES),
                                    deck.takeCard(Rank.FIVE, Suit.SPADES)
                            ),
                            null,
                    },
                    new Card[]{
                            deck.takeCard(Rank.SIX, Suit.SPADES),
                            deck.takeCard(Rank.SEVEN, Suit.HEARTS),
                            deck.takeCard(Rank.FOUR, Suit.DIAMONDS),
                            deck.takeCard(Rank.JACK, Suit.SPADES),
                            null
                    },
                    new Card[]{
                            deck.takeCard(Rank.QUEEN, Suit.HEARTS),
                            deck.takeCard(Rank.JACK, Suit.CLUBS),
                            deck.takeCard(Rank.SIX, Suit.HEARTS),
                            deck.takeCard(Rank.QUEEN, Suit.CLUBS),
                            deck.takeCard(Rank.JACK, Suit.DIAMONDS),
                            deck.takeCard(Rank.THREE, Suit.DIAMONDS),
                            deck.takeCard(Rank.ACE, Suit.CLUBS),
                            deck.takeCard(Rank.EIGHT, Suit.DIAMONDS),
                            deck.takeCard(Rank.KING, Suit.CLUBS),
                            deck.takeCard(Rank.THREE, Suit.CLUBS),
                    },
                    100000
            );

            for (Outcome outcome : playerOutcomes) {
                System.out.println(outcome);
            }
        } catch (DeckStateException e) {
            e.printStackTrace();
        }

        System.out.println();
        deck.shuffle();

        try {
            playerOutcomes = Assessment.assess(
                    deck,
                    new PocketCards[]{
                            new PocketCards(
                                    deck.takeCard(Rank.ACE, Suit.SPADES),
                                    deck.takeCard(Rank.QUEEN, Suit.SPADES)
                            ),
                            null
                    },
                    new Card[]{
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
            PocketCards[] pocketCards = new PocketCards[]{
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
                    new Card[]{ // community cards
                            deck.takeCard(Rank.EIGHT, Suit.DIAMONDS),
                            deck.takeCard(Rank.SEVEN, Suit.CLUBS),
                            deck.takeCard(Rank.TWO, Suit.CLUBS),
                            deck.takeCard(Rank.ACE, Suit.DIAMONDS)

                    },
                    new Card[]{ // folded / known cards
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
    }
}