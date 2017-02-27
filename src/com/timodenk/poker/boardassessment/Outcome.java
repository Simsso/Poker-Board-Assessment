package com.timodenk.poker.boardassessment;

import com.timodenk.poker.Hand;
import com.timodenk.poker.HandName;

/**
 * The Outcome class is mostly used to store information about how a pocket hand / a player has performed over multiple iterations at a table.
 * The information is a combination of the Hand that the player had and whether they won, lost, or participated at a split pot.
 * The class offers functions so get the aggregated and simply processed values of the stored outcomes.
 */
class Outcome {
    // counter for win, split, and loss
    private long win, split, loss;

    // counter for win, split, and loss with respect to the hand that lead to the win/split/loss.
    private long[] handWinCount = new long[HandName.values().length],
            handSplitCount = new long[HandName.values().length],
            handLossCount = new long[HandName.values().length];


    /**
     * Default constructor for blank Outcome object.
     * All count values will be equal to 0.
     */
    Outcome() {

    }


    /**
     * Constructor to initialize the outcome object with values.
     * @param win Number of wins.
     * @param split Number of splits.
     * @param loss Number of losses.
     */
    Outcome(long win, long split, long loss) {
        this.win = win;
        this.split = split;
        this.loss = loss;
    }


    /**
     * Adds a new win.
     * @param hand The hand that won.
     */
    void addWin(Hand hand) {
        this.win++;
        handWinCount[hand.name.ordinal()]++;
    }


    /**
     * Adds a new split.
     * @param hand The hand that participated in the split.
     */
    void addSplit(Hand hand) {
        this.split++;
        handSplitCount[hand.name.ordinal()]++;
    }


    /**
     * Adds a new loss.
     * @param hand The hand that lost.
     */
    void addLoss(Hand hand) {
        this.loss++;
        handLossCount[hand.name.ordinal()]++;
    }


    /**
     * Get win rate over all showdowns that have been added.
     * @return The percentage of wins [0,1].
     */
    double getWinRate() {
        return (double)win / getCount();
    }

    /**
     * Get win rate over all showdown where the player participated with a given hand.
     * @param handName The hand that participated in a showdown.
     * @return The percentage of wins with the given hand [0,1].
     */
    double getWinRate(HandName handName) {
        return (double)handWinCount[handName.ordinal()] / win;
    }


    /**
     * Get split rate over all showdowns that have been added.
     * @return The percentage of splits [0,1].
     */
    double getSplitRate() {
        return (double)split / getCount();
    }

    /**
     * Get split rate over all showdown where the player participated with a given hand.
     * @param handName The hand that participated in a showdown.
     * @return The percentage of splits with the given hand [0,1].
     */
    double getSplitRate(HandName handName) {
        return (double)handSplitCount[handName.ordinal()] / split;
    }


    /**
     * Get loss rate over all showdowns that have been added.
     * @return The percentage of losses [0,1].
     */
    double getLossRate() {
        return (double)loss / getCount();
    }

    /**
     * Get loss rate over all showdown where the player participated with a given hand.
     * @param handName The hand that participated in a showdown.
     * @return The percentage of losses with the given hand [0,1].
     */
    double getLossRate(HandName handName) {
        return (double)handLossCount[handName.ordinal()] / loss;
    }


    /**
     * @return Number of showdowns that have been added (sum of wins, splits, and losses)
     */
    private long getCount() {
        return win + split + loss;
    }

    /**
     * @param handName A hand.
     * @return Number of showdowns that have been added for a given hand (sum of wins, splits, and losses)
     */
    private long getCount(HandName handName) {
        return handWinCount[handName.ordinal()] + handSplitCount[handName.ordinal()] + handLossCount[handName.ordinal()];
    }


    /**
     * Hand rate, meaning how often did a given hand occur.
     * @param handName The hand.
     * @return Percentage telling, how often the passed hand has occured yet [0,1].
     */
    private double getHandRate(HandName handName) {
        return (double)getCount(handName) / getCount();
    }


    /**
     * Merges another {@link Outcome} object into this object.
     * All stored information will be summed up.
     * @param toMerge The other object to merge with.
     * @return This object (for chained merging with multiple other objects).
     */
    Outcome merge(Outcome toMerge) {
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


    /**
     * Converts an {@link Outcome} object into a single-line string.
     * @return Win rate and split rate (loss rate is not contained because it can be deduced).
     */
    @Override
    public String toString() {
        return "Win " + String.format("%6f", getWinRate()) + " \tSplit " + String.format("%6f", getSplitRate());
    }

    /**
     * Converts an {@link Outcome} object into a multi-line string.
     * @return Tabular string holding the win and split rate for every hand name (Royal Flush, Pair, ...).
     */
    String toTable() {
        String out = this.toString();
        for (int i = 0; i < HandName.values().length; i++) {
            HandName hand = HandName.values()[i];
            out += "\n" + String.format("%4f", getHandRate(hand)) + " \t" + hand.toString();
        }
        return out;
    }


    /**
     * Number of logged showdowns of multiple {@link Outcome} objects.
     * @param outcomes Multiple outcomes.
     * @return Total number of logged showdowns.
     */
    static long getCount(Outcome[] outcomes) {
        long count = 0;
        for (Outcome outcome : outcomes) {
            count += outcome.getCount();
        }
        return count;
    }

    /**
     * Number of logged showdowns of multiple {@link Outcome} objects for a given hand.
     * @param outcomes Multiple outcomes.
     * @param handName A hand name.
     * @return Total number of logged showdowns where the given hand was played.
     */
    static long getCount(Outcome[] outcomes, HandName handName) {
        long count = 0;
        for (Outcome outcome : outcomes) {
            count += outcome.getCount(handName);
        }
        return count;
    }
}
