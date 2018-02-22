package com.timodenk.poker;

import java.security.InvalidParameterException;

/**
 * A poker hand. That is five cards which build a {@link HandName} in combination.
 * Hands can be compared with one another.
 */
public class Hand implements Comparable<Hand> {
    public final HandName name; // the hand's name (e.g. Royal Flush)
    public final Card[] cards; // five cards that the hand consists out of

    /**
     * Default constructor taking the hand's name and the cards that it consists out of.
     * @param name The hand's name (e.g. Full House).
     * @param cards The hand's five cards.
     */
    Hand(HandName name, Card[] cards) {
        if (cards.length != 5) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.cards = cards;
    }

    /**
     * Compares this hand with another hand.
     * Hand comparison is important at showdown.
     *
     * The task is especially difficult if both hands have the same hand name.
     * In this case it has to be determined e.g. who has the higher Straight Flush.
     *
     * @param h2 The hand to compare with.
     * @return -1 if h2 is better than this hand; 0 if both hands have the same value; 1 if this hand is better than h2.
     */
    @Override
    public int compareTo(Hand h2) {
        Hand h1 = this;

        int comparison; // tmp variable for some case branches and the following if

        comparison = h1.name.compareTo(h2.name) * -1;
        if (comparison != 0) {
            // return 1: e.g. h1 has Royal Flush (0) vs. h2 has Straight Flush (1)
            // return -1: e.g. h1 has High Card (15) vs h2 has Four of a Kind (2)
            return comparison;
        }

        // both hands have the same hand name (e.g. both have a pair)
        switch (h1.name) {
            case ROYAL_FLUSH:
                // two Royal Flushes are always the same rank
                return 0;

            case FOUR_OF_A_KIND:
                // for a sorted array containing four cards of a kind, the second, third, and fourth card always has the quad rank
                // example: A 5 5 5 5 --> 5
                // J J J J 4 --> J
                Rank quadsRank1 = h1.cards[1].rank,
                        quadsRank2 = h2.cards[1].rank;
                comparison = quadsRank1.compareTo(quadsRank2);
                if (comparison != 0) {
                    return comparison;
                }
                // look for kicker
                Rank kickerRank1 = ((quadsRank1 == h1.cards[0].rank) ? h1.cards[4] : h1.cards[0]).rank,
                        kickerRank2 = ((quadsRank2 == h2.cards[0].rank) ? h2.cards[4] : h2.cards[0]).rank;
                return kickerRank1.compareTo(kickerRank2);

            case FULL_HOUSE:
                // for a full house the card in the middle (index = 2) is equal to the rank of the full house
                Rank fullHouseRank1 = h1.cards[2].rank,
                        fullHouseRank2 = h2.cards[2].rank;
                comparison = fullHouseRank1.compareTo(fullHouseRank2);
                if (comparison != 0) { // one full house is higher than the other one
                    return comparison;
                }
                // pair decides
                return compareFirstHigherCard(h1.cards, h2.cards);

            case THREE_OF_A_KIND:
                // card in the middle (index = 2) is the rank of the trips
                Rank tripsRank1 = h1.cards[2].rank,
                        tripsRank2 = h2.cards[2].rank;
                comparison = tripsRank1.compareTo(tripsRank2);
                if (comparison != 0) {
                    return comparison; // trips rank is different
                }
                // look for kickers (first and second kicker)
                return compareFirstHigherCard(h1.cards, h2.cards);

            case TWO_PAIR:
                // first pair
                Rank firstPairRank1 = getPairRank(h1.cards),
                        firstPairRank2 = getPairRank(h2.cards);
                comparison = firstPairRank1.compareTo(firstPairRank2);
                if (comparison != 0) {
                    return comparison;
                }

                // second pair
                Rank secondPairRank1 = getPairRank(h1.cards, firstPairRank1),
                        secondPairRank2 = getPairRank(h2.cards, firstPairRank2);
                comparison = secondPairRank1.compareTo(secondPairRank2);
                if (comparison != 0) {
                    return comparison;
                }

                // kicker
                return compareFirstHigherCard(h1.cards, h2.cards);

            case PAIR:
                Rank pairRank1 = getPairRank(h1.cards),
                        pairRank2 = getPairRank(h2.cards);
                comparison = pairRank1.compareTo(pairRank2);
                if (comparison != 0) {
                    return comparison;
                }
                // kickers
                return compareFirstHigherCard(h1.cards, h2.cards);


            default:
                // STRAIGHT_FLUSH, FLUSH, STRAIGHT, HIGH_CARD
                return compareFirstHigherCard(h1.cards, h2.cards);
        }
    }

    /**
     * Helper method for {@code compareTo}.
     * Looks through two sorted arrays of cards and returns which has the higher kicker.
     * @param c1 The first hand's cards.
     * @param c2 The second hand's cards.
     * @return -1 if c2 is better than c1 hand; 0 if both card arrays have the same rank; 1 if c1 is better than c2.
     */
    private int compareFirstHigherCard(Card[] c1, Card[] c2) {
        for (int i = 0; i < 5; i++) {
            int comparison = (c1[i].rank.compareTo(c2[i].rank));
            if (comparison == 0) {
                continue;
            }
            return comparison;
        }
        return 0;
    }

    /**
     * Helper method for {@code compareTo}.
     * Searches for a pair in a given set of cards.
     * @param c Array of sorted cards.
     * @return The highest pair in the passed set of sorted cards.
     */
    private Rank getPairRank(Card[] c) {
        return getPairRank(c, null);
    }

    /**
     * Helper method for {@code compareTo}.
     * Searches for the highest pair in the passed array of cards.
     * @param c Array of sorted cards.
     * @param excluding {@code null} or the rank of a pair not to take into account.
     * @return The highest pair of a set of sorted cards (excluding the rank passed as the second parameter).
     */
    private Rank getPairRank(Card[] c, Rank excluding) {
        for (Card card1 : c) {
            for (Card card2 : c) {
                if (card1 != card2 &&
                        card1.rank == card2.rank &&
                        card1.rank != excluding) {
                    return card1.rank;
                }
            }
        }
        throw new InvalidParameterException("No pair found");
    }

    /**
     * Converts the hand object into a string.
     * @return A string holding information about all cards of the hand and the hand name.
     */
    @Override
    public String toString() {
        String result = "";
        for (Card card : cards) {
            result += String.format("%s ", card.toString());
        }
        return result + name.toString();
    }
}