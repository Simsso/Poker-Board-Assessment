package PokerBoardAssessment;

import Poker.*;

/**
 * Created by Denk on 22/02/17.
 */
class PocketCardsWinRate {
    static void analyseWinRates(int iterations, int opponents) {
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                if (rank2.ordinal() > rank1.ordinal()) {
                    continue; // combination occurs the other way around
                }

                Suit suit1 = Suit.CLUBS;
                Suit suit2 = Suit.SPADES;
                Outcome suited = null,
                        offSuit = winRateFor(rank1, suit1, rank2, suit2, iterations, opponents);

                if (rank1 != rank2) {
                    suit2 = Suit.CLUBS;
                    suited = winRateFor(rank1, suit1, rank2, suit2, iterations, opponents);
                }

                logWithTabs(rank1, rank2, suited, offSuit);
            }
        }
    }

    private static Outcome winRateFor(Rank rank1, Suit suit1, Rank rank2, Suit suit2, int iterations, int opponents) {
        if (opponents < 1) {
            return new Outcome();
        }

        Card[] tmp7Cards = new Card[7],
                communityCards,
                opponentCards = new Card[opponents * 2]; // two cards for each opponent

        Hand[] opponentHands = new Hand[opponents];

        Outcome outcome = new Outcome();

        for (int i = 0; i < iterations; i++) {
            Deck deck = new Deck();
            Card card1 = deck.getCard(rank1, suit1),
                    card2 = deck.getCard(rank2, suit2);

            communityCards = deck.getNCards(5);

            joinCommunityAndPocketCards(tmp7Cards, communityCards, card1, card2);

            Hand playerHand = Poker.getBestHandFromCards(tmp7Cards);

            boolean wonSoFar = false;
            for (int j = 0; j < opponents; j++) {
                joinCommunityAndPocketCards(tmp7Cards, communityCards, deck.getNextCard(), deck.getNextCard());
                opponentHands[j] = Poker.getBestHandFromCards(tmp7Cards);
                int compare = opponentHands[j].compareTo(playerHand);
                if (compare > 0) {
                    // opponent is better
                    outcome.addLoss();
                    break;
                }
                if (compare < 0) { // won at least against one opponent
                    wonSoFar = true;
                }
            }

            if (wonSoFar) {
                outcome.addWin();
            }
            else {
                outcome.addSplit();
            }
        }
        return outcome;
    }

    private static void joinCommunityAndPocketCards(Card[] out, Card[] communityCards, Card playerCard1, Card playerCard2) {
        for (int i = 0; i < 5; i++) {
            out[i] = communityCards[i];
        }
        out[5] = playerCard1;
        out[6] = playerCard2;
    }

    private static void log(Rank rank1, Rank rank2, Outcome suited, Outcome offSuit) {
        System.out.println(rank1 + " " + rank2);
        if (suited != null) {
            System.out.println("\tsuited: " + (suited.getWinRate() * 100) + "% | " + (suited.getSplitRate() * 100) + "%");
        }
        System.out.println("\toff-suit: " + (offSuit.getWinRate() * 100) + "% | " + (offSuit.getSplitRate() * 100) + "%");
        System.out.println();
    }

    private static void logWithTabs(Rank rank1, Rank rank2, Outcome suited, Outcome offSuit) {
        System.out.println(rank1 + " " + rank2 + "\t"  + offSuit.getWinRate() + "\t" + offSuit.getSplitRate() + "\t"  + ((suited != null) ? suited.getWinRate() + "\t" + suited.getSplitRate() : ""));
    }
}
