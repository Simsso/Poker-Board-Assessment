package com.timodenk.poker.boardassessment;

import com.timodenk.poker.StartingHand;

/**
 * Extension of the {@link Outcome} object by the pocket cards that have achieved the outcome.
 * This is particularly useful when the task is not the assessment of just a single situation.
 */
class StartingHandOutcome extends Outcome {
    private StartingHand startingHand; // pocket cards that have this outcome

    /**
     * Default constructor extended by the pocket cards.
     * @param startingHand Pocket cards.
     */
    StartingHandOutcome(StartingHand startingHand) {
        super();
        this.startingHand = startingHand;
    }

    /**
     * Constructor to initialize the outcome object with values.
     * @param startingHand Pocket cards.
     * @param win Number of wins.
     * @param split Number of splits.
     * @param loss Number of losses.
     */
    StartingHandOutcome(StartingHand startingHand, long win, long split, long loss) {
        super(win, split, loss);
        this.startingHand = startingHand;
    }

    /**
     * Constructor to extend a given {@link Outcome} object with a pair of pocket cards.
     * @param startingHand The pocket cards.
     * @param outcome The outcome to join into this object.
     */
    StartingHandOutcome(StartingHand startingHand, Outcome outcome) {
        this.merge(outcome);
        this.startingHand = startingHand;
    }

    /**
     * Getter method for the pocket cards attribut.
     */
    public StartingHand getStartingHand() {
        return startingHand;
    }

    /**
     * @return A string holding information about both pocket cards and the outcome.
     */
    @Override
    public String toString() {
        return this.startingHand.toString() + " " + super.toString();
    }

    public String toOutcomeString() {
        return super.toString();
    }
}
