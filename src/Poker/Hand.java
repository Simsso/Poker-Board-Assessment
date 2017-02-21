package Poker;

import java.util.Comparator;

/**
 * Created by Denk on 21/02/17.
 */
public class Hand implements Comparable<Hand> {
    public final HandName name;
    public final Card[] cards;

    public Hand(HandName name, Card[] cards) {
        if (cards.length != 5) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.cards = cards;
    }

    // @return -1 if h2 is better than h1
    // @return 0 if both hands have the same value
    // @return 1 if h1 is better than h2
    @Override
    public int compareTo(Hand h2) {
        Hand h1 = this;
        if (h1.name.ordinal() < h2.name.ordinal()) {
            // e.g. h1 has Royal Flush (0) vs. h2 has Straight Flush (1)
            return 1;
        }
        if (h1.name.ordinal() > h2.name.ordinal()) {
            // e.g. h1 has High Card (15) vs h2 has Four of a Kind (2)
            return -1;
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
                int comparison = quadsRank1.compareTo(quadsRank2);
                if (comparison != 0) {
                    return comparison;
                }
                Rank kickerRank1 = ((quadsRank1 == h1.cards[0].rank) ? h1.cards[4] : h1.cards[0]).rank,
                        kickerRank2 = ((quadsRank2 == h2.cards[0].rank) ? h2.cards[4] : h2.cards[0]).rank;
                return kickerRank1.compareTo(kickerRank2);
            case FULL_HOUSE:
                // for a full house the card in the middle (index = 2) is equal to the rank of the full house
                Rank fullHouseRank1 = h1.cards[2].rank;
                break;
            case THREE_OF_A_KIND:
                //TODO: implement comparison
                break;
            case TWO_PAIR:
                //TODO: implement comparison
                break;
            case PAIR:
                //TODO: implement comparison
                break;
            default:
                // STRAIGHT_FLUSH, FLUSH, STRAIGHT, HIGH_CARD
                return compareFirstHigherCard(h1.cards, h2.cards);
        }
        return 0;
    }

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

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < cards.length; i++) {
            result += cards[i].toString() + " ";
        }
        return result + name.toString();
    }
}