package com.timodenk.poker.boardassessment;

import com.timodenk.poker.CommunityCards;
import com.timodenk.poker.Hand;
import com.timodenk.poker.Poker;
import com.timodenk.poker.StartingHand;

public class StartingHandAnalysis {
    static void start() {
        CommunityCards[] allCommunityCardCombinations = CommunityCards.getAllCombinations();
        StartingHand[] startingHands = StartingHand.getAll();

        StartingHandOutcome[] outcomes = new StartingHandOutcome[startingHands.length];
        for (int i = 0; i < outcomes.length; i++) {
            outcomes[i] = new StartingHandOutcome(startingHands[i]);
        }

        // loop over all community card combinations
        for (CommunityCards communityCards : allCommunityCardCombinations) {

            // array of hands that the starting hands build with the current community cards
            Hand[] hands = new Hand[StartingHand.ALL_COUNT];

            // loop over all starting hands
            for (int i = 0; i < StartingHand.ALL_COUNT; i++) {

                StartingHand startingHand = startingHands[i];
                if (StartingHand.playable(startingHand, communityCards)) {
                    hands[i] = Poker.getBestHand(communityCards, startingHands[i]);
                }
                else {
                    hands[i] = null;
                }
            }

            // analyze and update outcomes
            for (int i = 0; i < StartingHand.ALL_COUNT; i++) {
                Hand handI = hands[i]; // hand that starting hand i builds with the community cards
                if (handI == null) {
                    continue;
                }
                for (int j = i + 1; j < StartingHand.ALL_COUNT; j++) {
                    Hand handJ = hands[j]; // hand that starting hand i builds with the community cards
                    if (handJ == null) {
                        continue;
                    }

                    // check if i can play vs j on the board
                    if (!StartingHand.playable(new StartingHand[] { startingHands[i], startingHands[j] }, communityCards)) {
                        continue;
                    }

                    // check the outcome for i vs j

                    int comparison = handI.compareTo(handJ);
                    if (comparison > 0) {
                        outcomes[i].addWin(handI);
                        outcomes[j].addLoss(handJ);
                    }
                    else if (comparison < 0) {
                        outcomes[i].addLoss(handI);
                        outcomes[j].addWin(handJ);
                    }
                    else {
                        outcomes[i].addSplit(handI);
                        outcomes[j].addSplit(handJ);
                    }
                }
            }
            System.out.println(communityCards);
        }
    }
}
