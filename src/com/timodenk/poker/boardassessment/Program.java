package com.timodenk.poker.boardassessment;

import com.timodenk.poker.*;

import java.io.*;

public class Program {
    public static void main(String[] args) {
        try {
            Outcome[][] outcome = Outcome.loadFromFile("/Users/Denk/Documents/Development/PokerBoardAssessment/out.dat");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}