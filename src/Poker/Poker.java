package Poker;

/**
 * Created by Denk on 21/02/17.
 */
public class Poker {
    public static Hand getHandFromCards(Card[] cards) {
        return new Hand(CardAssessment.getRank(cards), cards);
    }
}
