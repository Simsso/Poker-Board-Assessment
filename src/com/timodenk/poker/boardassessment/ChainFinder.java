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
            final List<StartingHand>[] winsAgainst = getWinningAgainst(startingHands, DATA_PATH);

            int ctr = 0;

            for (int i = 0; i < winsAgainst.length; i++) { // all hands
                int a = i;
                for (int j = 0; j < winsAgainst[i].size(); j++) {
                    int b = winsAgainst[i].get(j).ID; // all hands that a beats

                    if (b > a) {
                        continue;
                    }

                    for (int k = 0; k < winsAgainst[b].size(); k++) {
                        int c = winsAgainst[b].get(k).ID; // all hands that b beats

                        if (c > a) {
                            continue;
                        }

                        if (winsAgainst[c].contains(startingHands[a])) { // if c beats a
                            System.out.println(startingHands[a] + " > " + startingHands[b] + " > " + startingHands[c] + " > " + startingHands[a]);
                            ctr++;
                        }
                    }
                }
            }
            System.out.println(ctr + " chains found");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static List<StartingHand>[] getWinningAgainst(String path) throws IOException, ClassNotFoundException {
        return getWinningAgainst(StartingHand.getAll(), path);
    }

    private static List<StartingHand>[] getWinningAgainst(StartingHand[] startingHands, String path) throws IOException, ClassNotFoundException {
        Outcome[][] outcome = Outcome.loadFromFile(path);
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
