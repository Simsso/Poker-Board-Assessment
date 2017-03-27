package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

import java.io.*;

public class Program {
    public static void main(String[] args) {
        File file = new File(args[0]);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }

            // clear file
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

            StartingHandAnalysis.start(fileOutputStream, System.out);

            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}