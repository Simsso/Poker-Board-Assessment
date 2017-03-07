package com.timodenk.poker.boardassessment;

import com.timodenk.poker.Card;
import com.timodenk.poker.Rank;
import com.timodenk.poker.StartingHand;
import com.timodenk.poker.Suit;

import java.io.*;

public class Program {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("First argument must be output file path.");
            return;
        }
        File file = new File(args[0]);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }

            // clear file
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

            Assessment.getStartingHandsHeadsUp((args.length > 1) ? Integer.valueOf(args[1]) : 100, fileOutputStream, System.out);

            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (true) return;

        StartingHand a = new StartingHand(new Card(Rank.TWO, Suit.SPADES), new Card(Rank.TWO, Suit.DIAMONDS)),
                b = new StartingHand(new Card(Rank.TWO, Suit.CLUBS), new Card(Rank.TWO, Suit.HEARTS));

        StartingHand[][] permutations = StartingHand.getPermutations(a, b);
        for (StartingHand[] permutation : permutations) {
            for (StartingHand hand : permutation) {
                System.out.print(hand + "  ");
            }
            System.out.println();
        }
    }
}