package Poker;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Denk on 21/02/17.
 */
class CardAssessment {
    public static HandName getRank(Card[] c) {
        validateParameter(c);
        Arrays.sort(c, Collections.reverseOrder()); // sort descending (e.g. A, J, 5, 4, 2)

        if (isRoyalFlush(c))
            return HandName.ROYAL_FLUSH;

        if (isStraightFlush(c))
            return HandName.STRAIGHT_FLUSH;

        if (isFourOfAKind(c))
            return HandName.FOUR_OF_A_KIND;

        if (isFullHouse(c))
            return HandName.FULL_HOUSE;

        if (isFlush(c))
            return HandName.FLUSH;

        if (isStraight(c))
            return HandName.STRAIGHT;

        if (isThreeOfAKind(c))
            return HandName.THREE_OF_A_KIND;

        if (isTwoPair(c))
            return HandName.TWO_PAIR;

        if (isPair(c))
            return HandName.PAIR;

        return HandName.HIGH_CARD;
    }

    private static void validateParameter(Card[] c) {
        if (c.length != 5) {
            throw new IllegalArgumentException("Numer of cards must be 5");
        }
    }

    private static boolean isRoyalFlush(Card[] c) {
        return (isStraightFlush(c) && c[0].rank == Rank.ACE);
    }

    private static boolean isStraightFlush(Card[] c) {
        return isFlush(c) && isStraight(c);
    }

    private static boolean isFourOfAKind(Card[] c) {
        return (sameRank(c[0], c[1], c[2], c[3]) || sameRank(c[1], c[2], c[3], c[4]));
    }

    private static boolean isFullHouse(Card[] c) {
        return (sameRank(c[0], c[1]) && sameRank(c[2], c[3], c[4]) ||
                sameRank(c[0], c[1], c[2]) && sameRank(c[3], c[4]));
    }

    private static boolean isFlush(Card[] c) {
        return (sameSuit(c[0], c[1], c[2], c[3], c[4]));
    }

    private static boolean isStraight(Card[] c) {
        Rank prevRank = c[0].rank;
        for (int i = 1; i < c.length; i++) {
            Rank rank = c[i].rank;
            if (Math.abs(prevRank.ordinal() - rank.ordinal()) != 1) {
                return false;
            }
            prevRank = rank;
        }
        return true;
    }

    private static boolean isThreeOfAKind(Card[] c) {
        return (sameRank(c[0], c[1], c[2]) ||
                sameRank(c[1], c[2], c[3]) ||
                sameRank(c[2], c[3], c[4]));
    }

    private static boolean isTwoPair(Card[] c) {
        return (!isThreeOfAKind(c) && (
                sameRank(c[0], c[1]) && (sameRank(c[2], c[3]) || sameRank(c[3], c[4])) ||
                sameRank(c[1], c[2]) && sameRank(c[3], c[4])));
    }

    private static boolean isPair(Card[] c) {
        return (sameRank(c[0], c[1]) ||
                sameRank(c[1], c[2]) ||
                sameRank(c[2], c[3]) ||
                sameRank(c[3], c[4]));
    }

    private static boolean sameRank(Card... c) {
        if (c.length == 0) {
            return true;
        }

        Rank rank = c[0].rank;
        for (Card card : c) {
            if (card.rank != rank) {
                return false;
            }
        }
        return true;
    }

    private static boolean sameSuit(Card... c) {
        if (c.length == 0) {
            return true;
        }

        Suit suit = c[0].suit;
        for (Card card : c) {
            if (card.suit != suit) {
                return false;
            }
        }
        return true;
    }
}