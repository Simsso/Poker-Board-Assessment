package com.timodenk.poker.boardassessment;

public class Program {
    public static void main(String[] args) {
        StartingHandOutcome[][] matrix = Assessment.getStartingHandsHeadsUp((args.length != 0) ? Integer.valueOf(args[0]) : 100);
        for (StartingHandOutcome[] row : matrix) {
            for (StartingHandOutcome outcome : row) {
                if (outcome == null) {
                    System.out.println("null");
                }
                else {
                    System.out.println(outcome.getStartingHand() + ": " + outcome.getWinRate());
                }
            }
        }
    }
}