package com.timodenk.poker.boardassessment;

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

            Assessment.getStartingHandsHeadsUp((args.length > 1) ? Integer.valueOf(args[1]) : 1000, fileOutputStream, System.out);

            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}