package Poker;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Denk on 21/02/17.
 */
public class Poker {
    private static int HAND_SIZE = 5;

    public static Hand getBestHandFromCards(Card[] cards) {
        Card[][] combinations = possibleFiveCardCombinations(cards);
        Hand[] hands = new Hand[combinations.length];
        for (int i = 0; i < combinations.length; i++) {
            hands[i] = new Hand(CardAssessment.getRank(combinations[i]), combinations[i]);
        }
        Arrays.sort(hands, Collections.reverseOrder()); // descending (best hand first)
        return hands[0]; // best Hand
    }

    private static Card[][] possibleFiveCardCombinations(Card[] cards) {
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