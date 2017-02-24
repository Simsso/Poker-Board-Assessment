package com.timodenk.poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Deck {
    private final Card[] cards;
    private ArrayList<Card> availableCards;
    private Random random = new Random();

    public Deck() {
        this.cards = getDeckCards();
        this.shuffle();
    }

    public void takeCards(Card... cards) {
        for (Card card : cards) {
            this.availableCards.remove(card);
        }
    }

    public void shuffle() {
        this.availableCards = new ArrayList<Card>(Arrays.asList(this.cards));
    }

    public Card getNextCard() throws DeckStateException {
        if (this.availableCards.size() == 0) {
            throw new DeckStateException("All cards have been taken already.");
        }
        int randomIndex = random.nextInt(this.availableCards.size());
        Card card = this.availableCards.get(randomIndex);
        this.availableCards.remove(randomIndex);
        return card;
    }

    public Card[] getNCards(int n) throws DeckStateException {
        Card[] cards = new Card[n];
        for (int i = 0; i < n; i++) {
            cards[i] = getNextCard();
        }
        return cards;
    }

    public Card getCardLike(Card sameCardFromDifferentDeck) throws DeckStateException {
        for (Card card : this.cards) {
            if (card.suit == sameCardFromDifferentDeck.suit && card.rank == sameCardFromDifferentDeck.rank) {
                return card;
            }
        }
        throw new DeckStateException("Card not found: " + sameCardFromDifferentDeck.toString());
    }

    // takes a card from the deck
    // returns null if the card is not available
    public Card takeCard(Rank rank, Suit suit) throws DeckStateException {
        for (Card card : availableCards) {
            if (card.rank == rank && card.suit == suit) {
                this.availableCards.remove(card);
                return card;
            }
        }
        throw new DeckStateException(rank, suit);
    }

    private static Card[] getDeckCards() {
        Card[] deck = new Card[52];

        int i = 0;
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck[i] = new Card(rank, suit);
                i++;
            }
        }

        return deck;
    }

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