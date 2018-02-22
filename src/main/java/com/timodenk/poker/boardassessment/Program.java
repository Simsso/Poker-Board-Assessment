package com.timodenk.poker.boardassessment;

import java.io.IOException;

public class Program {
    public static void main(String[] args) {
        try {
            ChainFinder.main(args);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}