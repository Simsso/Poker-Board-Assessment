package PokerBoardAssessment;

import Poker.*;

class PocketCardsWinRate {
    static void analyseAllWinRates(int iterations, int opponents) {
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                for (Suit suit1 : Suit.values()) {
                    for (Suit suit2 : Suit.values()) {
                        if (rank1 == rank2 && suit1 == suit2) {
                            continue; // not possible
                        }

                        Outcome outcome = winRateFor(rank1, suit1, rank2, suit2, iterations, opponents);
                        logWithTabs(rank1, rank2, (suit1 == suit2) ? outcome : null, (suit1 == suit2) ? null : outcome);
                    }
                }
            }
        }
    }

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
            return null;
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

            boolean bestSoFar = false, loss = false, splitSoFar = false;
            for (int j = 0; j < opponents; j++) {
                joinCommunityAndPocketCards(tmp7Cards, communityCards, deck.getNextCard(), deck.getNextCard());
                opponentHands[j] = Poker.getBestHandFromCards(tmp7Cards);
                int compare = opponentHands[j].compareTo(playerHand);

                if (compare > 0) {
                    // opponent is better
                    loss = true;
                    outcome.addLoss();
                    break; // break for performance reason: no further comparison necessary
                }

                if (!splitSoFar && compare < 0) {
                    // won at least against one opponent
                    // if there has already been a split with another opponent (splitSoFar) the superiority does not matter
                    bestSoFar = true;
                }

                if (compare == 0) {
                    // split pot against that opponent
                    splitSoFar = true;

                    // previous wins do not matter anymore (pot will be either split or lost)
                    bestSoFar = false;
                }
            }

            // no split, at least one win, and no loss
            if (!splitSoFar && bestSoFar && !loss) {
                outcome.addWin();
            }

            // one split, no win, and no loss
            if (splitSoFar && !bestSoFar && !loss) {
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
        if (offSuit != null) {
            System.out.println(rank1 + " " + rank2 + " (off-suit)\t" + offSuit.getWinRate() + "\t" + offSuit.getSplitRate());
        }
        if (suited != null) {
            System.out.println(rank1 + " " + rank2 + " (suited)\t" + suited.getWinRate() + "\t" + suited.getSplitRate());
        }
    }
}
