package Poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Denk on 21/02/17.
 */
public class Deck {
    public final Card[] cards;
    private ArrayList<Card> availableCards;
    private Random random = new Random();

    public Deck() {
        this.cards = getDeckCards();
        this.availableCards = new ArrayList<Card>(Arrays.asList(this.cards));
    }

    public Card getNextCard() {
        int randomIndex = random.nextInt(this.availableCards.size());
        Card card = this.availableCards.get(randomIndex);
        this.availableCards.remove(randomIndex);
        return card;
    }

    public Card[] getNCards(int n) {
        Card[] cards = new Card[n];
        for (int i = 0; i < n; i++) {
            cards[i] = getNextCard();
        }
        return cards;
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
}
