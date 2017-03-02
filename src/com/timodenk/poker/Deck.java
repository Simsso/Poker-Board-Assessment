package com.timodenk.poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * A deck of cards. Used to keep track of which cards are available (e.g. for community cards).
 */
public class Deck {
    // all cards n = 52
    private final DeckCard[] cards;

    // all cards that are still available (not distributed)
    private ArrayList<Card> availableCards;

    // random object to ensure random distribution
    private Random random = new Random();

    /**
     * Default constructor initializes all attributes.
     * A {@link Deck} with all 52 card that a standard 52-card deck contains.
     */
    public Deck() {
        this.cards = new DeckCard[52];

        int i = 0;
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                this.cards[i] = new DeckCard(this, rank, suit);
                i++;
            }
        }

        this.shuffle();
    }

    /**
     * Takes several cards from the deck (marks them as distributed).
     * @param cards The cards to take from the deck.
     */
    public void takeCards(Card... cards) {
        for (Card card : cards) {
            this.availableCards.remove(card);
        }
    }

    /**
     * Shuffles the deck, that means all cards are available again.
     */
    public void shuffle() {
        this.availableCards = new ArrayList<Card>(Arrays.asList(this.cards));
    }

    /**
     * Takes a random card from the deck which is available.
     * @return A random card which is not distributed.
     * @throws DeckStateException Thrown e.g. if no more cards are avialable on the deck.
     */
    public Card getNextCard() throws DeckStateException {
        if (this.availableCards.size() == 0) {
            throw new DeckStateException("All cards have been taken already.");
        }
        int randomIndex = random.nextInt(this.availableCards.size());
        Card card = this.availableCards.get(randomIndex);
        this.availableCards.remove(randomIndex);
        return card;
    }

    /**
     * Takes a defined number of cards from the deck (marks them as not available).
     * @param n Number of cards to take.
     * @return The taken cards.
     * @throws DeckStateException Thrown e.g. if there are not enough cards available.
     */
    public Card[] getNCards(int n) throws DeckStateException {
        Card[] cards = new Card[n];
        for (int i = 0; i < n; i++) {
            cards[i] = getNextCard();
        }
        return cards;
    }

    /**
     * Returns a card from the deck that equals another card that has not been taken from this deck.
     * The card is not taken from the deck and will still be available.
     * @param sameCardFromDifferentDeck Card from another deck.
     * @return The card with equal rank and suit as the given card but from this deck.
     * @throws DeckStateException Thrown e.g. if there are not enough cards available.
     */
    public Card getCardLike(Card sameCardFromDifferentDeck) throws DeckStateException {
        for (Card card : this.cards) {
            if (card.suit == sameCardFromDifferentDeck.suit && card.rank == sameCardFromDifferentDeck.rank) {
                return card;
            }
        }
        throw new DeckStateException("Card not found: " + sameCardFromDifferentDeck.toString());
    }

    /**
     * Takes a card from the deck.
     * @param rank Rank of the card.
     * @param suit Suit of the deck.
     * @return The taken card.
     * @throws DeckStateException Thrown if the card is not available anymore.
     */
    public Card takeCard(Rank rank, Suit suit) throws DeckStateException {
        for (Card card : availableCards) {
            if (card.rank == rank && card.suit == suit) {
                this.availableCards.remove(card);
                return card;
            }
        }
        throw new DeckStateException(rank, suit);
    }

    public Card takeCardLike(Card card) throws DeckStateException {
        return takeCard(card.rank, card.suit);
    }

    public PocketCards takeCardsLike(PocketCards pocketCards) throws DeckStateException {
        return new PocketCards(this.takeCardLike(pocketCards.card1), this.takeCardLike(pocketCards.card2));
    }

    /**
     * Clones the deck. This includes the current state, meaning which cards have been distributed.
     * @return The cloned deck.
     */
    @Override
    public Deck clone() {
        Deck newDeck = new Deck();
        for (Card card : this.cards) {
            if (this.availableCards.contains(card)) {
                // card has not been taken yet
            }
            else {
                try {
                    newDeck.takeCard(card.rank, card.suit);
                } catch (DeckStateException e) {
                    e.printStackTrace();
                }
            }
        }
        return newDeck;
    }
}