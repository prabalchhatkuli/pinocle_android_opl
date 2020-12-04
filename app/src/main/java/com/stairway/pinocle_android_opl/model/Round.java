package com.stairway.pinocle_android_opl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Round {
    private int remainingTurns, nextTurn;
    private Deck roundDeck;
    private Card trumpCard;
    private ArrayList<Player> listOfPlayers;
    private boolean isTurnComplete;
    private boolean moveOrMeld;

    public Round(int winnerLastRound)
    {
        nextTurn = winnerLastRound;
        remainingTurns = 0;
        roundDeck = new Deck();
        isTurnComplete = true;
        moveOrMeld =true;
    }

    public void startRound(ArrayList<Player> listOfPlayers, int winnerLastRound) {
        this.listOfPlayers = listOfPlayers;
        this.nextTurn = winnerLastRound;

        roundDeck.shuffleDeck();

        int halfOfDeck = roundDeck.getDeckSize()/2;
        //deal initial cards to the players
        while(roundDeck.getDeckSize() > halfOfDeck ) {
            for (Player each: listOfPlayers)
            {
                //each player gets 4 cards at one deal
                dealCardsFromDeck(each, 4);
            }
        }

        //dealing the trump Card
        trumpCard = roundDeck.dealCard();
    }

    public void dealCardsFromDeck(Player player, int numberOfDraws)
    {
        //temporary variable declarations
        Card tempCard;

        for ( int i = 0; i < numberOfDraws && roundDeck.getDeckSize() > 0; i++)
        {
            tempCard = roundDeck.dealCard();
            player.addToHand(tempCard);
        }

        if(roundDeck.getDeckSize() == 0 && trumpCard.getCardID()!=0 && numberOfDraws == 1)
        {
            player.addToHand(trumpCard);
            trumpCard = new Card('0', trumpCard.getCardSuit(), 0);
        }
    }

    public ArrayList<Card> getDeck() {
        return roundDeck.getCards();
    }

    public Card getTrumpCard() {
        return trumpCard;
    }

    public int processMoves() {
        Card leadCard = listOfPlayers.get(nextTurn).playedCards.get(0);
        Card chaseCard = listOfPlayers.get((0 == nextTurn) ? 1 : 0).playedCards.get(0);

        //if both cards are same, lead player wins
        if (leadCard.getCardSuit() == chaseCard.getCardSuit() && leadCard.getCardFace() == chaseCard.getCardFace()) {
            return nextTurn;
        }

        //if lead card is of trump suit
        else if (trumpCard.getCardSuit() == leadCard.getCardSuit()) {
            //if chase is also of trump suit and has more point than lead then the chase player wins the turn
            if (trumpCard.getCardSuit() == chaseCard.getCardSuit() && chaseCard.getCardPoints() > leadCard.getCardPoints()) {
                return (0 == nextTurn) ? 1 : 0;
            }
            //else the lead player wins
            else {
                return nextTurn;
            }
        }
        //if  lead card is not of trump suit,
        else if (trumpCard.getCardSuit() != leadCard.getCardSuit()) {
            //if chase is of trump suit, chase wins
            if (trumpCard.getCardSuit() == chaseCard.getCardSuit()) {
                return (0 == nextTurn) ? 1 : 0;
            }
            //if lead and chase are same suit and chase has higher card face, chase wins
            else if (leadCard.getCardSuit() == chaseCard.getCardSuit() && chaseCard.getCardPoints() > leadCard.getCardPoints()) {
                return (0 == nextTurn) ? 1 : 0;
            }
            //otherwise lead wins
            else {
                return nextTurn;
            }
        }
        return nextTurn;
    }

    public int getNextPlayer() {
        return nextTurn;
    }

    public void play(Integer cardID) {
        listOfPlayers.get(nextTurn).play(cardID);

        //only move is allowed, no meld
        moveOrMeld =true;

        isTurnComplete = !isTurnComplete;

        nextTurn = (nextTurn == 0)?1:0;

        //process played cards, in meld maps, and collections
        listOfPlayers.get(nextTurn).processPlayedCards();

        if(isTurnComplete)
        {
            nextTurn = processMoves();
            processTurnWin();

            return;
        }
    }

    private void processTurnWin() {
        //capturePile update
        listOfPlayers.get(nextTurn).addToCapturePile(listOfPlayers.get(nextTurn).playedCards.get(0) );
        listOfPlayers.get(nextTurn).addToCapturePile(listOfPlayers.get((0 == nextTurn) ? 1 : 0).playedCards.get(0) );

        //round score update
        listOfPlayers.get(nextTurn).addToRoundScore(listOfPlayers.get(nextTurn).playedCards.get(0).getCardPoints());
        listOfPlayers.get(nextTurn).addToRoundScore(listOfPlayers.get((0 == nextTurn) ? 1 : 0).playedCards.get(0).getCardPoints());

        //process played cards, in meld maps, and collections

        //clear turn collections like played cards
        listOfPlayers.get(nextTurn).playedCards.clear();
        listOfPlayers.get((0 == nextTurn) ? 1 : 0).playedCards.clear();

        //set meld option to true for the winner
        moveOrMeld = false;

        //subtract the remaining turns
        remainingTurns -- ;

    }

    public boolean getMoveOrMeld() {
        return moveOrMeld;
    }

    public void makeMeld(ArrayList<Integer> selectedCard)
    {
        //the cards with cardID in selectedCard belongs to the player whose number is in nextTurn
        ArrayList<Card> cardsFromHand = new ArrayList<Card>();
        ArrayList<Card> cardsFromMeld = new ArrayList<Card>();

        for(int cardID : selectedCard)
        {
            first:
            for(Card card: listOfPlayers.get(nextTurn).getPlayerHand())
            {
                if(cardID == card.getCardID())
                {
                    cardsFromHand.add(card);
                    break first;
                }
            }

            second:
            for(Card card: listOfPlayers.get(nextTurn).getMeldPile())
            {
                if(cardID == card.getCardID())
                {
                    cardsFromMeld.add(card);
                    break second;
                }
            }
        }

        ArrayList<Card> mergedCards = new ArrayList<Card>();

        //if all of the chosen cards are from cardsfromMeld, then meld cannot happen
        if(cardsFromMeld.size() !=  selectedCard.size())
        {
            mergedCards.addAll(cardsFromMeld);
            mergedCards.addAll(cardsFromHand);
            int possibleMeld = evaluateMeld(mergedCards);
            //move the cards to the meld Pile;
            if(possibleMeld != 0) {
                //update score
                listOfPlayers.get(nextTurn).addMeldScore(possibleMeld);

                //for cards from hand move hand cards from hand pile to meld
                listOfPlayers.get(nextTurn).removeCardsFromHand(cardsFromHand);
                listOfPlayers.get(nextTurn).addNewMeldCards(possibleMeld, cardsFromHand);

                //update meld pile cards for the new meld
                listOfPlayers.get(nextTurn).updateMeldCards(possibleMeld, cardsFromMeld);

                //update the meldToCard map for the user
                listOfPlayers.get(nextTurn).addToMeldToCardMap(possibleMeld, mergedCards);

            }
        }
        moveOrMeld = true;
        listOfPlayers.get(nextTurn).clearPlayedCards();

        dealCardsFromDeck(listOfPlayers.get(nextTurn), 1);
        dealCardsFromDeck(listOfPlayers.get((nextTurn==0)?1:0), 1);
        return;
    }

    private int evaluateMeld(ArrayList<Card> mergedCards) {
        //variable to store the index of the possible meld
        int possibleMeld = 0;

        //variable to store the number of cards used in previous melds
        int cardsInMeldPile = 0;

        for(Card card: listOfPlayers.get(nextTurn).getMeldPile())
        {
            for (Card selectedCards: mergedCards)
            {
                if(selectedCards.getCardID() == card.getCardID())
                {
                    cardsInMeldPile++;
                }
            }
        }
        //sort the played cards so that it is easy for comparision based on the index of the cards
        //the comments describe how they are compared
        Collections.sort(mergedCards, new Comparator<Card>() {
            @Override public int compare(Card p1, Card p2) {
                return p2.getCardPoints() - p1.getCardPoints()  ; // Ascending
            }
        });

        //find which meld is possible from the available cards
        switch (mergedCards.size())
        {
            case 1:
                //Nine of Trump suit - called dix - 10 points
                if ('9' == mergedCards.get(0).getCardFace() && trumpCard.getCardSuit() == mergedCards.get(0).getCardSuit())
            {
                //add scores and evaluate the meld
                possibleMeld = 4;
            }
            break;
            case 2:
                //King and Queen of Trump suit - called royal marriage - 40 points
                if ('K' == mergedCards.get(0).getCardFace() && 'Q' == mergedCards.get(1).getCardFace() && mergedCards.get(0).getCardSuit() == mergedCards.get(1).getCardSuit())
            {
                if (trumpCard.getCardSuit() == mergedCards.get(0).getCardSuit())
                {

                    possibleMeld = 2;
                }
                //King and Queen of any other suit - called marriage - 20 points
			else
                {
                    possibleMeld = 3;
                }
            }

            //Queen of Spades and Jack of Diamonds - called Pinochle - 40 points
            if ('Q' == mergedCards.get(0).getCardFace() && 'S' == mergedCards.get(0).getCardSuit())
            {
                if ('J' == mergedCards.get(1).getCardFace() && 'D' == mergedCards.get(1).getCardSuit())
                {
                    possibleMeld = 9;
                }
            }
            break;
            case 4:
                //Aces of four different suits - called four Aces - 100 points
                //Kings of four different suits - called four Kings - 80 points
                //Queens of four different suits - called four Queens - 60 points
                //Jacks of four different suits - called four Jacks - 40 points

                //check if the cards are of same suits and different faces
                for (int i = 0; i < 4; i++)
                {
                    for (int j = i + 1; j < 4; j++)
                    {
                        //if not same face, return false
                        if (mergedCards.get(i).getCardFace() != mergedCards.get(j).getCardFace())
                        {
                            possibleMeld = 0;
                        }

                        //if same suit, return false
                        if (mergedCards.get(i).getCardSuit() == mergedCards.get(i).getCardSuit())
                        {
                            possibleMeld = 0;
                        }
                    }
                }

                switch (mergedCards.get(0).getCardFace())
            {
                case 'A':
                    possibleMeld = 5;
                    break;
                case 'K':
                    possibleMeld = 6;
                    break;
                case 'Q':
                    possibleMeld = 7;
                    break;
                case 'J':
                    possibleMeld = 8;
                    break;
            }

            break;
            case 5:
                //Ace, Ten, King, Queen and Jack of Trump suit - called flush - 150 points
                //run a loop to see if all of them have Trump suit
                for (Card card: mergedCards)
            {
                if (trumpCard.getCardSuit() != card.getCardSuit())
                {
                    return 0;
                }
            }

            //check if they are int the order: Ace, Ten, King, Queen and Jack
            if ('A' == mergedCards.get(0).getCardFace() && 'X' == mergedCards.get(1).getCardFace() && 'K' == mergedCards.get(2).getCardFace())
            {
                if ('Q'==mergedCards.get(3).getCardFace() && 'J' ==mergedCards.get(4).getCardFace())
                {
                    possibleMeld = 1;
                }
            }
            break;
            default://if no cases match return false;
                possibleMeld = 0;
                return 0;
        }

        //if meld cards are used and player is trying to make a meld they already made
        //the meld cannot be allowed
        if (cardsInMeldPile > 0 && possibleMeld != 0)
        {
            for (Card card: mergedCards)
            {
                //find the meld cards
                ArrayList<Integer> meldsOfCard = listOfPlayers.get(nextTurn).cardToMeldMap.get(card);

                //if card is found in cardToMeldMap
                if (meldsOfCard != null)
                {
                    //check if the possibleMeld is in the vector of Melds for a card
                    //if found
                    if (meldsOfCard.contains(possibleMeld))
                    {
                        return 0;
                    }
                }
            }

            //if the condition passes, the meld card/s is/are used for a new valid meld
            return possibleMeld;

        }
        //else
        //proceed for scoring
        else
        {
            return possibleMeld;
        }
    }

    public void drawCards() {
        moveOrMeld =true;
        dealCardsFromDeck(listOfPlayers.get(nextTurn), 1);
        dealCardsFromDeck(listOfPlayers.get((nextTurn==0)?1:0), 1);
    }

    public void letPlayerMakeMeld() {
        listOfPlayers.get(nextTurn).decideMeld(trumpCard);
    }

    public void getPlayerMove() {
        if (listOfPlayers.get((nextTurn==0?1:0)).playedCards.size() == 0) {
            Card x = listOfPlayers.get(nextTurn).getTacticalCard(trumpCard);
            System.out.println("The card that should be chosen is:");
            System.out.println(x.getCardFace()+" "+x.getCardSuit());
        }
        else
        {
            Card x = listOfPlayers.get(nextTurn).getCheapestCard(listOfPlayers.get((nextTurn==0?1:0)).playedCards.get(0),trumpCard);
            System.out.println("The card that should be chosen is:");
            System.out.println(x.getCardFace()+" "+x.getCardSuit());
        }

    }


    public String serialize() {
        return "hello save file here";
    }
}
