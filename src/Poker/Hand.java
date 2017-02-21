package Poker;

/**
 * Created by Denk on 21/02/17.
 */
public class Hand implements Comparable<Hand> {
    public final HandName name;
    public final Card[] cards;

    Hand(HandName name, Card[] cards) {
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

        int comparison; // tmp variable for some case branches

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

    // returns the highest pair of a set of sorted cards
    private Rank getPairRank(Card[] c) {
        return getPairRank(c, null);
    }

    // returns the highest pair of a set of sorted cards (excluding the rank passed as the second parameter)
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
        return null;
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