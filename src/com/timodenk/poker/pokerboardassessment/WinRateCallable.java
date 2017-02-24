package com.timodenk.poker.pokerboardassessment;

import com.timodenk.poker.*;

import java.util.concurrent.Callable;

/**
 * Created by Denk on 24/02/17.
 */
class WinRateCallable implements Callable<Outcome> {
    private final Rank rank1, rank2;
    private final Suit suit1, suit2;

    private final int iterations, opponents;

    public WinRateCallable(final Rank rank1, final Suit suit1, final Rank rank2, final Suit suit2, final int iterations, final int opponents) {
        this.rank1 = rank1;
        this.rank2 = rank2;
        this.suit1 = suit1;
        this.suit2 = suit2;
        this.iterations = iterations;
        this.opponents = opponents;
    }

    @Override
    public Outcome call() throws Exception {
        Card[] tmp7Cards = new Card[7],
                communityCards;

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
}
