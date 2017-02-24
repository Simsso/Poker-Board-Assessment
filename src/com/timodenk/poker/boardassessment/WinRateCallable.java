package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

import java.util.concurrent.Callable;

class WinRateCallable implements Callable<Outcome> {
    private final Card pocketCard1, pocketCard2;
    private final Deck deck;
    private final Card[] communityCards = new Card[5],
            communityCardsInitial; // for later usage it is necessary to know which community cards were set / known

    private final int iterations, opponents;

    // only pocket cards played information
    WinRateCallable(final Deck deck, final Card pocketCard1, final Card pocketCard2, final int iterations, final int opponents) {
        this(deck, pocketCard1, pocketCard2, new Card[5], iterations, opponents);
    }

    // more card information available
    WinRateCallable(final Deck deck, final Card pocketCard1, final Card pocketCard2, final Card[] communityCards, final int iterations, final int opponents) {
        this.deck = deck;
        this.pocketCard1 = pocketCard1;
        this.pocketCard2 = pocketCard2;
        this.communityCardsInitial = communityCards;

        this.iterations = iterations;
        this.opponents = opponents;
    }

    @Override
    public Outcome call() throws Exception {
        Card[] tmp7Cards = new Card[7];

        Hand[] opponentHands = new Hand[opponents];

        Outcome outcome = new Outcome();
        for (int i = 0; i < iterations; i++) {
            deck.shuffle();
            Card card1 = pocketCard1,
                    card2 = pocketCard2;
            deck.takeCards(card1, card2);

            fillCommunityCards();

            joinCommunityAndPocketCards(tmp7Cards, communityCards, card1, card2);

            Hand playerHand = Poker.getBestHandFromCards(tmp7Cards);

            boolean bestSoFar = false, loss = false, splitSoFar = false;
            for (int j = 0; j < opponents; j++) {
                try {
                    joinCommunityAndPocketCards(tmp7Cards, communityCards, deck.getNextCard(), deck.getNextCard());
                }
                catch(DeckStateException e) {
                    throw new Exception("Can not take new cards. Probably too many opponents (" + opponents + ").");
                }
                try {
                    opponentHands[j] = Poker.getBestHandFromCards(tmp7Cards);
                }
                catch (NullPointerException e) {
                    System.out.println("Exception");
                }
                int compare = opponentHands[j].compareTo(playerHand);

                if (compare > 0) {
                    // opponent is better
                    loss = true;
                    outcome.addLoss(playerHand);
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
                outcome.addWin(playerHand);
            }

            // one split, no win, and no loss
            if (splitSoFar && !bestSoFar && !loss) {
                outcome.addSplit(playerHand);
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

    private void fillCommunityCards() throws DeckStateException {
        deck.takeCards(communityCardsInitial); // community cards can not be taken by other players
        for (int i = 0; i < 5; i++) {
            if (!(i < communityCardsInitial.length) || communityCardsInitial[i] == null) {
                this.communityCards[i] = deck.getNextCard();
            }
            else {
                this.communityCards[i] = communityCardsInitial[i];
            }
        }
    }
}
