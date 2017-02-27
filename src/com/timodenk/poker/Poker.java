package com.timodenk.poker;

import java.util.Arrays;
import java.util.Collections;

/**
 * Static poker class containing parts of the Hold'em game logic.
 */
public class Poker {
    private static final int HAND_SIZE = 5; // size of a hand at showdown

    /**
     * @param cards Array of five or more cards. At showdown usually 7 cards (5 community cards and 2 pocket cards).
     * @return The best hand that can be formed with the given cards.
     */
    public static Hand getBestHand(Card[] cards) {
        Card[][] combinations = possibleCombinations(cards);
        Hand[] hands = new Hand[combinations.length];
        for (int i = 0; i < combinations.length; i++) {
            hands[i] = new Hand(CardAssessment.getRank(combinations[i]), combinations[i]);
        }
        Arrays.sort(hands, Collections.reverseOrder()); // descending (best hand first)
        return hands[0]; // best Hand
    }

    /**
     * Generates all possible combinations of length 5, that can be formed with the given cards.
     * Order does not matter so e.g. [A 2 3 4 5] and [A 3 2 4 5] will not both be returned for the example input [.
     * @param cards The cards.
     * @return All possible sets of five cards build from the parameter.
     */
    private static Card[][] possibleCombinations(Card[] cards) {
        Card[][] combinations;
        if (cards.length <= HAND_SIZE) {
            combinations = new Card[1][];
            combinations[0] = cards;
            return combinations;
        }

        combinations = new Card[(int)Util.binomial(cards.length, HAND_SIZE)][HAND_SIZE];

        for (int i = 0, addedCount = 0; i < Math.pow(2, cards.length); i++) {
            if (Util.binaryDigitSum(i) == HAND_SIZE) {
                combinations[addedCount] = new Card[HAND_SIZE];

                // pick cards based on binary digits
                for (int j = 0, cardsAddedCount = 0; j < cards.length; j++) {
                    if ((i >> j & 1) == 1) {
                        combinations[addedCount][cardsAddedCount] = cards[j];
                        cardsAddedCount++;
                    }
                }

                addedCount++;
            }
        }
        return combinations;
    }
}