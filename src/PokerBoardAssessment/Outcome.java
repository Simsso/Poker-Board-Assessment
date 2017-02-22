package PokerBoardAssessment;

/**
 * Created by Denk on 22/02/17.
 */
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
        return (double)win / (win + split + loss);
    }

    public double getSplitRate() {
        return (double)split / (win + split + loss);
    }
}
