package com.timodenk.poker;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link StartingHand} class contains two {@link Card}s.
 * For some applications both cards should be taken from the same {@link Deck}.
 */
public class StartingHand {
    public Card card1, card2; // both cards

    public static final int ALL_COUNT = 1326;

    /**
     * Two poker cards (no specific order).
     *
     * @param card1 A card.
     * @param card2 Another card.
     */
    public StartingHand(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    /**
     * @return True if both pocker cards have the same suit.
     */
    public boolean isSuited() {
        return card1.suit == card2.suit;
    }

    /**
     * @return True if both cards have the same rank.
     */
    public boolean isPair() {
        return card1.rank == card2.rank;
    }

    public StartingHand getPermutation(Suit[] permutation) {
        return new StartingHand(this.card1.getPermutation(permutation), this.card2.getPermutation(permutation));
    }

    /**
     * @return Rank and suit of both cards separated by a whitespace.
     */
    @Override
    public String toString() {
        return String.format("%s %s", card1, card2);
    }

    public static StartingHand[] getSignificant() {
        return getSignificant(Suit.DIAMONDS, Suit.SPADES);
    }

    public static StartingHand[] getSignificant(Suit suit1, Suit suit2) {
        if (suit1 == suit2) {
            throw new IllegalArgumentException("Suits must be different");
        }

        StartingHand[] pocketCards = new StartingHand[169];
        int i = 0;
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                if (rank2.ordinal() > rank1.ordinal()) {
                    continue; // combination occurs the other way around
                }

                pocketCards[i++] = new StartingHand(new Card(rank1, suit1), new Card(rank2, suit2));

                if (rank1 != rank2) {
                    // suited combinations possible
                    pocketCards[i++] = new StartingHand(new Card(rank1, suit1), new Card(rank2, suit1));
                }
            }
        }
        return pocketCards;
    }

    public static StartingHand[] getAll() {
        StartingHand[] pocketCards = new StartingHand[ALL_COUNT];
        int i = 0;
        for (Rank rank1 : Rank.values()) {
            for (Rank rank2 : Rank.values()) {
                if (rank2.ordinal() > rank1.ordinal()) {
                    continue; // combination occurs the other way around
                }

                for (Suit suit1 : Suit.values()) {
                    for (Suit suit2 : Suit.values()) {
                        if (rank1.ordinal() == rank2.ordinal() && suit1.ordinal() == suit2.ordinal()) {
                            continue; // no suited pairs
                        }
                        if (rank1.ordinal() == rank2.ordinal() && suit2.ordinal() > suit1.ordinal()) {
                            continue; // no flippable combination for pairs (e.g. not both, (2D,2H) and (2H,2D)
                        }

                        pocketCards[i++] = new StartingHand(new Card(rank1, suit1), new Card(rank2, suit2));
                    }
                }
            }
        }
        return pocketCards;
    }

    static boolean playable(StartingHand... startingHands) {
        Deck deck = new Deck();
        try {
            for (StartingHand startingHand : startingHands) {
                deck.takeCard(startingHand.card1);
                deck.takeCard(startingHand.card2);
            }
        } catch (DeckStateException e) {
            return false;
        }
        return true;
    }

    public static boolean playable(StartingHand startingHand, CommunityCards communityCards) {
        return playable(new StartingHand[] { startingHand }, communityCards);
    }

    public static boolean playable(StartingHand[] startingHands, CommunityCards communityCards) {
        // quick way (assumes that the board is valid and the starting hand is valid)
        if (startingHands.length == 1) {
            Card[] communityArray = communityCards.getAll();
            for (int i = 0; i < communityArray.length; i++) {
                if (communityArray[i].equals(startingHands[0].card1) || communityArray[i].equals(startingHands[0].card2)) {
                    return false;
                }
            }
            return !(startingHands[0].card1.equals(startingHands[0].card2));
        }


        Deck deck = new Deck();
        try {
            for (StartingHand startingHand : startingHands) {
                deck.takeCard(startingHand.card1);
                deck.takeCard(startingHand.card2);
            }
            deck.takeCards(communityCards.getAll());
        } catch (DeckStateException e) {
            return false;
        }
        return true;
    }

    // checks whether two valid starting hands (not the same card contained) are playable against each other
    public static boolean validHandsPlayable(StartingHand hand1, StartingHand hand2) {
        return !(hand1.card1.equals(hand2.card1) || hand1.card1.equals(hand2.card2) || hand1.card2.equals(hand2.card1) || hand1.card2.equals(hand2.card2));
    }

    public static StartingHand[][] getPermutations(StartingHand... startingHands) {
        List<StartingHand[]> permutations = new ArrayList<StartingHand[]>();
        Suit[][] suitPermutations = Suit.getPermutations();
        for (Suit[] suitPermutation : suitPermutations) {
            StartingHand[] permutation = new StartingHand[startingHands.length];
            for (int i = 0; i < startingHands.length; i++) {
                permutation[i] = startingHands[i].getPermutation(suitPermutation);
            }
            permutations.add(permutation);
        }

        // remove duplicates and non playable cards
        for (int i = 0; i < permutations.size(); i++) {
            if (!StartingHand.playable(permutations.get(i))) {
                permutations.remove(i);
                i--;
                continue;
            }

            // search for duplicates in following entries
            for (int j = i + 1; j < permutations.size(); j++) {
                boolean allContained = true;

                // check all duplicate starting hands
                for (int k = 0; k < permutations.get(i).length; k++) {
                    boolean matchFound = false;
                    // compare with all potential duplicate starting hands
                    for (int l = 0; l < permutations.get(i).length; l++) {
                        if (permutations.get(i)[k].equals(permutations.get(j)[l])) {
                            matchFound = true;
                        }
                    }
                    if (!matchFound) {
                        allContained = false;
                    }
                }
                if (allContained) {
                    permutations.remove(j);
                    j--;
                }
            }
        }
        StartingHand[][] output = new StartingHand[permutations.size()][];
        for (int i = 0; i < permutations.size(); i++) {
            output[i] = permutations.get(i);
        }
        return output;
    }

    public boolean equals(StartingHand h) {
        boolean sameOrder = this.card1.equals(h.card1) && this.card2.equals(h.card2),
            mixed = this.card1.equals(h.card2) && this.card2.equals(h.card1);
        return sameOrder || mixed;
    }
}
