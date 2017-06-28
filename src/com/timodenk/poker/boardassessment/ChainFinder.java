package com.timodenk.poker.boardassessment;

import com.timodenk.poker.StartingHand;
import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
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
    private static List<int[]> chains = new ArrayList<>();
    private static int chainCtr = 0;

    private static double[][] m = new double[0][];
    private static double[][] adjacencyMatrix = new double[0][];

    private static final StartingHand[] startingHands = StartingHand.getAll();

    private static long startTime = 0;

    static {
        try {
            m = getWinningMatrix(DATA_PATH);
            adjacencyMatrix= getAdjacencyMatrix(m);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        startTime = System.nanoTime();
    }

    public static void main(String[] args) {
        involvedInChainsOfLength();
    }

    private static void involvedInChainsOfLength() {
        for (int i = 0; i < startingHands.length; i++) {
            System.out.print(startingHands[i] + "\t");
        }
        System.out.println();

        // copy adjacency matrix
        double[][] multipliedMatrix = new double[adjacencyMatrix.length][];
        for (int i = 0; i < m.length; i++) {
            multipliedMatrix[i] = new double[adjacencyMatrix[i].length];
            for (int j = 0; j < m[i].length; j++) {
                multipliedMatrix[i][j] = adjacencyMatrix[i][j];
            }
        }


        for (int l = 2; l < m.length; l++) {
            multipliedMatrix = matrixMultiplication(multipliedMatrix, adjacencyMatrix);
            normalizeMatrix(multipliedMatrix);
            printMatrixMainDiagonal(multipliedMatrix);
        }
    }

    private static void normalizeMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                m[i][j] = (m[i][j] == 0) ? 0 : 1;
            }
        }
    }

    private static void printMatrixMainDiagonal(double[][] m) {
        for (int i = 0; i < m.length && i < m[i].length; i++) {
            System.out.print((m[i][i] == 0) ? 0 : 1);
            System.out.print("\t");
        }
        System.out.println();
    }

    private static double[][] matrixMultiplication(double[][] A, double[][] B) {
        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }


    private static void chain(int startID, int targetLength) {
        if (targetLength < 3) {
            chainCtr = 0;
            return;
        }
        int[] emptyChain = new int[targetLength];
        emptyChain[0] = startID;
        chain(emptyChain, 1);
    }

    private static void chain(int[] elementIDs, int currentLength) {
        if (elementIDs.length == currentLength) {
            if (m[elementIDs[elementIDs.length - 1]][elementIDs[0]] > 0) {
                //chains.add(elementIDs);
                chainCtr++;
            }
            return;
        }

        int last = elementIDs[currentLength - 1];
        for (int i = elementIDs[0] + 1; i < m.length; i++) {
            for (int j = 1; j < currentLength; j++) {
                if (elementIDs[j] == i) continue; // same element can not occur in a chain twice
            }

            if (m[last][i] > 0) {
                elementIDs[currentLength] = i;
                chain(elementIDs, currentLength + 1);
            }
        }
    }

    private static void approachB() {
        long ctr = 0;

        double maxmin = 0;

        for (int a = 0; a < m.length; a++) { // a = hand A id

            for (int b = a + 1; b < m[a].length; b++) { // b = hand B id
                double ab = m[a][b]; // A wins against B by ab

                if (ab > 0) { // A wins against B

                    for (int c = a + 1; c < m[b].length; c++) {
                        double bc = m[b][c];

                        if (bc > 0) {

                            for (int d = a + 1; d < m[c].length; d++) {
                                double cd = m[c][d];
                                double da = m[d][a];
                                if (cd > 0 && da > 0) {
                                    double min = Math.min(Math.min(ab, bc), Math.min(cd, da));
                                    ctr++;
                                    if (min > maxmin) {
                                        maxmin = min;
                                        System.out.println(startingHands[a] + "\t" + startingHands[b] + "\t" + startingHands[c] + "\t" + startingHands[d] + "\t" + ab + "\t" + bc + "\t" + cd + "\t" + da + "\t" + min);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(ctr + " chains found.");
    }

    private static void approachA() {
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

    private static void saveWinningMatrix(String path, String matrixPath) throws IOException, ClassNotFoundException {
        double[][] m = getWinningMatrix(matrixPath);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < m.length; i++) {
            JSONArray row = new JSONArray();
            for (int j = 0; j < m[i].length; j++) {
                row.put(j, (Double.isNaN(m[i][j]) ? "NaN" : new java.text.DecimalFormat("#.########").format(m[i][j])));
            }
            jsonArray.put(i, row);
        }
        String json = jsonArray.toString().replaceAll("\"", "").replaceAll("0\\.", ".");

        FileWriter fileWriter = new FileWriter(new File(path));
        fileWriter.write(json);
        fileWriter.close();
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


    /**
     * Reads a winning matrix out of a serialized file.
     * The first index represents the hand A that plays against hand B (second index): matrix[A][B].
     * The matrix has the following values:
     *  - A number [-1,1] which represents the win probability of A over B minus B over A. If A wins more often against B then B against A, this value is positive.
     *  - NaN: The cards can not play against each other.
     * @param path File to load the data from.
     * @return The matrix.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static double[][] getWinningMatrix(String path) throws IOException, ClassNotFoundException {
        Outcome[][] outcome = Outcome.loadFromFile(path);
        double[][] matrix = new double[outcome.length][];
        for (int i = 0; i < outcome.length; i++) {
            matrix[i] = new double[outcome[i].length];
            for (int j = 0; j < outcome[i].length; j++) {
                if (outcome[i][j].getWinCount() + outcome[i][j].getSplitCount() + outcome[i][j].getLossCount() == 0) {
                    matrix[i][j] = Double.NaN;
                }
                else {
                    matrix[i][j] = outcome[i][j].getWinRate() - outcome[j][i].getWinRate();
                }
            }
        }
        return matrix;
    }

    private static double[][] getAdjacencyMatrix(double[][] winMatrix) {
        double[][] adjacencyMatrix = new double[winMatrix.length][];
        for (int i = 0; i < winMatrix.length; i++) {
            adjacencyMatrix[i] = new double[winMatrix[i].length];
            for (int j = 0; j < winMatrix[i].length; j++) {
                if (Double.isNaN(winMatrix[i][j]) || winMatrix[i][j] <= 0) {
                    adjacencyMatrix[i][j] = 0;
                }
                else {
                    adjacencyMatrix[i][j] = 1;
                }
            }
        }
        return adjacencyMatrix;
    }
}
