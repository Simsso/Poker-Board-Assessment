package com.timodenk.poker.boardassessment;

import com.timodenk.poker.PocketCards;

/**
 * Extension of the {@link Outcome} object by the pocket cards that have achieved the outcome.
 * This is particularly useful when the task is not the assessment of just a single situation.
 */
class PocketCardsOutcome extends Outcome {
    private PocketCards pocketCards; // pocket cards that have this outcome

    /**
     * Default constructor extended by the pocket cards.
     * @param pocketCards Pocket cards.
     */
    PocketCardsOutcome(PocketCards pocketCards) {
        super();
        this.pocketCards = pocketCards;
    }

    /**
     * Constructor to initialize the outcome object with values.
     * @param pocketCards Pocket cards.
     * @param win Number of wins.
     * @param split Number of splits.
     * @param loss Number of losses.
     */
    PocketCardsOutcome(PocketCards pocketCards, long win, long split, long loss) {
        super(win, split, loss);
        this.pocketCards = pocketCards;
    }

    /**
     * Constructor to extend a given {@link Outcome} object with a pair of pocket cards.
     * @param pocketCards The pocket cards.
     * @param outcome The outcome to join into this object.
     */
    PocketCardsOutcome(PocketCards pocketCards, Outcome outcome) {
        this.merge(outcome);
        this.pocketCards = pocketCards;
    }

    /**
     * Getter method for the pocket cards attribut.
     */
    public PocketCards getPocketCards() {
        return pocketCards;
    }

    /**
     * @return A string holding information about both pocket cards and the outcome.
     */
    @Override
    public String toString() {
        return this.pocketCards.toString() + " " + super.toString();
    }
}
