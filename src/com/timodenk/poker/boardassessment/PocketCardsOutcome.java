package com.timodenk.poker.boardassessment;

import com.timodenk.poker.PocketCards;

public class PocketCardsOutcome extends Outcome {
    private PocketCards pocketCards;

    PocketCardsOutcome(PocketCards pocketCards) {
        super();
        this.pocketCards = pocketCards;
    }

    PocketCardsOutcome(PocketCards pocketCards, long win, long split, long loss) {
        super(win, split, loss);
        this.pocketCards = pocketCards;
    }

    PocketCardsOutcome(PocketCards pocketCards, Outcome outcome) {
        this.merge(outcome);
        this.pocketCards = pocketCards;
    }

    public PocketCards getPocketCards() {
        return pocketCards;
    }

    @Override
    public String toString() {
        return this.pocketCards.toString() + " " + super.toString();
    }
}
