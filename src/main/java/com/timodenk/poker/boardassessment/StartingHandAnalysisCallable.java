package com.timodenk.poker.boardassessment;

import com.timodenk.poker.CommunityCards;
import com.timodenk.poker.Hand;
import com.timodenk.poker.Poker;
import com.timodenk.poker.StartingHand;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

class StartingHandAnalysisCallable implements Callable<Outcome[][]> {
    private final CommunityCards[] communityCardsCombinations;
    private final StartingHand[] startingHands;
    private final int internalThreadID;
    private final OutputStream log;

    StartingHandAnalysisCallable(int internalThreadID, CommunityCards[] communityCardsCombinations, StartingHand[] startingHands, OutputStream log) {
        this.communityCardsCombinations = communityCardsCombinations;
        this.startingHands = startingHands;
        this.internalThreadID = internalThreadID;
        this.log = log;
    }

    @Override
    public Outcome[][] call() throws Exception {
        final long startTime = System.nanoTime();
        Outcome[][] outcomes = new Outcome[startingHands.length][startingHands.length];
        for (int i = 0; i < outcomes.length; i++) { // init outcome array
            for (int j = 0; j < outcomes.length; j++) {
                // matrix contains results
                // row plays against column
                outcomes[i][j] = new Outcome(false);
            }
        }

        int ctr = 0;
        // loop over all community card combinations
        for (CommunityCards communityCards : communityCardsCombinations) {
            if (++ctr % 1000 == 0 || ctr < 10) {
                log.write((String.format("Thread %2d: %7f (%4f hours remaining)",
                    internalThreadID,
                    (ctr) / (float) communityCardsCombinations.length,
                    (double) (System.nanoTime() - startTime) / 1e9 / 3600 / ctr * (communityCardsCombinations.length - ctr)) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }

            // array of hands that the starting hands build with the current community cards
            Hand[] hands = new Hand[StartingHand.ALL_COUNT];

            // loop over all starting hands
            // check whether they can be played on the current board
            for (int i = 0; i < StartingHand.ALL_COUNT; i++) {
                StartingHand startingHand = startingHands[i];
                if (StartingHand.playable(startingHand, communityCards)) {
                    hands[i] = Poker.getBestHand(communityCards, startingHands[i]);
                } else {
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
                    if (!StartingHand.validHandsPlayable(startingHands[i], startingHands[j])) {
                        continue;
                    }

                    // check the outcome for i vs j
                    int comparison = handI.compareTo(handJ);
                    if (comparison > 0) { // i won
                        outcomes[i][j].addWin(handI); // matrix entry i vs j
                        outcomes[j][i].addLoss(handJ); // matrix entry j vs i
                    } else if (comparison < 0) { // j won
                        outcomes[i][j].addLoss(handI);
                        outcomes[j][i].addWin(handJ);
                    } else { // split
                        outcomes[i][j].addSplit(handI);
                        outcomes[j][i].addSplit(handJ);
                    }
                }
            }
        }
        return outcomes;
    }
}
