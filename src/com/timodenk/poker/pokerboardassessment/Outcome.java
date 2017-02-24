package com.timodenk.poker.pokerboardassessment;

public class Outcome {
    private long win, split, loss;


    public Outcome() {

    }

    public void addWin() {
        this.win++;
    }

    public void addSplit() {
        this.split++;
    }

    public void addLoss() {
        this.loss++;
    }

    public double getWinRate() {
        return (double)win / getCount();
    }

    public double getSplitRate() {
        return (double)split / getCount();
    }

    public long getCount() {
        return win + split + loss;
    }

    public void merge(Outcome toMerge) {
        this.win += toMerge.win;
        this.split += toMerge.split;
        this.loss += toMerge.loss;
    }
}
