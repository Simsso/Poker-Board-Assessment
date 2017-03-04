# Poker Board Assessment
This Java console application assesses given poker situations. The analysis work in a statistical manner, meaning the given situation is played out randomly many times. The statistical average of wins, splits, and losses is considered the usual outcome for a given situation.

A poker situation can be very complex, e.g. many players, known folded cards, and known communities, or very simple with just one player and the goal of observing how often a hand occurs (e.g. how often does the player get a RoyalFlush with their pocket cards).

## Assessment
Example code for two known pocket hands (Ace of Spades and Eight of Spades, vs. King of Spades and Five of Spades). Four community cards are known and several cards have been folded.

The output would be 
```
Win 0.795896 	Split 0.000000
Win 0.204104 	Split 0.000000
```
with the first row telling the win rate of Ace of Spades and Eight of Spades. The numer of itations is defined in the last function parameter (here set to `1000000`).
```Java
Deck deck = new Deck();

Outcome[] playerOutcomes;

try {
    playerOutcomes = Assessment.assess(
            deck,
            new DeckStartingHand[] {
                    new DeckStartingHand(
                            deck.takeCard(Rank.ACE, Suit.SPADES),
                            deck.takeCard(Rank.EIGHT, Suit.SPADES)
                    ),
                    new DeckStartingHand(
                            deck.takeCard(Rank.KING, Suit.SPADES),
                            deck.takeCard(Rank.FIVE, Suit.SPADES)
                    )
            },
            new DeckCard[] {
                    deck.takeCard(Rank.SIX, Suit.SPADES),
                    deck.takeCard(Rank.SEVEN, Suit.HEARTS),
                    deck.takeCard(Rank.FOUR, Suit.DIAMONDS),
                    deck.takeCard(Rank.JACK, Suit.SPADES),
            },
            new DeckCard[] {
                    deck.takeCard(Rank.QUEEN, Suit.HEARTS),
                    deck.takeCard(Rank.JACK, Suit.CLUBS),
                    deck.takeCard(Rank.SIX, Suit.HEARTS),
                    deck.takeCard(Rank.QUEEN, Suit.CLUBS),
                    deck.takeCard(Rank.JACK, Suit.DIAMONDS),
                    deck.takeCard(Rank.THREE, Suit.DIAMONDS),
                    deck.takeCard(Rank.ACE, Suit.CLUBS),
                    deck.takeCard(Rank.EIGHT, Suit.DIAMONDS),
                    deck.takeCard(Rank.KING, Suit.CLUBS),
                    deck.takeCard(Rank.THREE, Suit.CLUBS),
            },
            1000000
    );

    for (Outcome outcome : playerOutcomes) {
        System.out.println(outcome);
    }
} catch (DeckStateException e) {
    e.printStackTrace();
}
```

## Analysis examples
### Win, split, and loss probability
For distinct pocket cards, heads-up, random opponent and community cards.
![image](https://cloud.githubusercontent.com/assets/6556307/23580646/e5e43660-0105-11e7-9264-f62cdd56bc4d.png)

For all possible pocket cards, heads-up, random opponent and community cards.
![image](https://cloud.githubusercontent.com/assets/6556307/23580632/b24ae9ca-0105-11e7-8b5e-ab2f2e81ed64.png)

## Compile and run on Linux
```
git clone https://github.com/Simsso/Poker-Board-Assessment
```

```
cd Poker-Board-Assessment/src
javac com/timodenk/poker/*.java com/timodenk/poker/boardassessment/*.java
```

Run with 
```
java com.timodenk.poker.boardassessment.Program 'out.txt' 1000
```
for console output or 
```
nohup java com.timodenk.poker.boardassessment.Program out.txt 25000 > log.txt &
```
to run in the background and redirect output into a log file.
