package com.timodenk.poker.boardassessment;

import com.timodenk.poker.StartingHand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * For starting hands playing heads-up against each other there is no transitivity with respect to whether one hand wins statistically against another one.
 * This means that there exist rings like A, B, C; with A winning against B heads-up, B winning against C, and C winning against A. Such chains can be even longer.
 * This static class searches and observes such chains. Primary goal is to find the longest possible chain (the one involving most starting hands).
 */
public class ChainFinder {
    private static final String DATA_PATH = "/Users/Denk/Documents/Development/PokerBoardAssessment/out.dat";

    public static void main(String[] args) {
        try {
            final StartingHand[] startingHands = StartingHand.getAll();
            final List<StartingHand>[] winsAgainst = getWinningAgainst(DATA_PATH);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static List<StartingHand>[] getWinningAgainst(String path) throws IOException, ClassNotFoundException {
        Outcome[][] outcome = Outcome.loadFromFile(path);
        StartingHand[] startingHands = StartingHand.getAll();
        List<StartingHand>[] winsAgainst = new List[startingHands.length]; // length = 1326

        for (int i = 0; i < startingHands.length; i++) {
            winsAgainst[i] = new ArrayList<>();

            // check for all starting hands that i beats
            for (int j = 0; j < startingHands.length; j++) {
                if (outcome[i][j].getWinCount() > outcome[j][i].getWinCount()) {
                    winsAgainst[i].add(startingHands[j]);
                }
            }
        }

        return winsAgainst;
    }
}
