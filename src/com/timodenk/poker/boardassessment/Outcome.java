package com.timodenk.poker.boardassessment;

import com.timodenk.poker.Hand;
import com.timodenk.poker.HandName;

public class Outcome {
    private long win, split, loss;

    private long[] handWinCount = new long[HandName.values().length],
            handSplitCount = new long[HandName.values().length],
            handLossCount = new long[HandName.values().length];


    public Outcome() { }

    public void addWin(Hand hand) {
        this.win++;
        handWinCount[hand.name.ordinal()]++;
    }

    public void addSplit(Hand hand) {
        this.split++;
        handSplitCount[hand.name.ordinal()]++;
    }

    public void addLoss(Hand hand) {
        this.loss++;
        handLossCount[hand.name.ordinal()]++;
    }

    public double getWinRate() {
        return (double)win / getCount();
    }
    public double getWinRate(HandName handName) {
        return (double)handWinCount[handName.ordinal()] / win;
    }

    public double getSplitRate() {
        return (double)split / getCount();
    }
    public double getSplitRate(HandName handName) {
        return (double)handSplitCount[handName.ordinal()] / split;
    }

    public double getLossRate() {
        return (double)loss / getCount();
    }
    public double getLossRate(HandName handName) {
        return (double)handLossCount[handName.ordinal()] / loss;
    }

    public long getCount() {
        return win + split + loss;
    }
    public long getCount(HandName handName) {
        return handWinCount[handName.ordinal()] + handSplitCount[handName.ordinal()] + handLossCount[handName.ordinal()];
    }

    public double getHandRate(HandName handName) {
        return (double)getCount(handName) / getCount();
    }

    public Outcome merge(Outcome toMerge) {
        this.win += toMerge.win;
        this.split += toMerge.split;
        this.loss += toMerge.loss;

        for (int i = 0; i < HandName.values().length; i++) {
            this.handWinCount[i] += toMerge.handWinCount[i];
            this.handSplitCount[i] += toMerge.handSplitCount[i];
            this.handLossCount[i] += toMerge.handLossCount[i];
        }

        return this; // for chaining
    }

    @Override
    public String toString() {
        return "Win " + String.format("%6f", getWinRate()) + " \tSplit " + String.format("%6f", getSplitRate());
    }

    public String toTable() {
        String out = this.toString();
        for (int i = 0; i < HandName.values().length; i++) {
            HandName hand = HandName.values()[i];
            out += "\n" + String.format("%6f", getHandRate(hand)) + " \t" + hand.toString();
        }
        return out;
    }
}
