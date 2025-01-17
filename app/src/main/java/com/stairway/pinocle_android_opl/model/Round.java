package com.stairway.pinocle_android_opl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Round {
    //member variables
    private int remainingTurns, nextTurn;
    private Deck roundDeck;
    private Card trumpCard;
    private ArrayList<Player> listOfPlayers;
    private boolean isTurnComplete;
    private boolean moveOrMeld;


    private static final Map<Integer, String> MELDS;

    //static initializations
    static {
        HashMap<Integer, String> aMap =  new HashMap<Integer, String>();
        aMap.put(1,"flush");
        aMap.put(2,"royal marriage");
        aMap.put(3,"marriage");
        aMap.put(4,"dix");
        aMap.put(5,"four Aces");
        aMap.put(6,"four Kings");
        aMap.put(7,"four Queens");
        aMap.put(8,"four Jacks");
        aMap.put(9,"Pinochle");
        MELDS = Collections.unmodifiableMap(aMap);
    }

    /**
     overloaded constructor for the class
     @param winnerLastRound, an integer, index to the listOfPlayers
     */
    public Round(int winnerLastRound)
    {
        nextTurn = winnerLastRound;
        remainingTurns = 0;
        roundDeck = new Deck();
        isTurnComplete = true;
        moveOrMeld =true;
    }

    /**
     startRound, function to initialize all information for starting the round
     @param listOfPlayers, a list of player objects, players for the game
     @param winnerLastRound, an integer for the winner of the last Round, who will start the game
     */
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

    /**
     dealCardsFromDeck, function to deal card to a player from the deck
     @param player, a player object, that will receive the cards
     @param numberOfDraws, an integer for the number of cards to be dealt
     */
    public void dealCardsFromDeck(Player player, int numberOfDraws)
    {
        //temporary variable declarations
        Card tempCard;

        for ( int i = 0; i < numberOfDraws && roundDeck.getDeckSize() > 0; i++)
        {
            tempCard = roundDeck.dealCard();
            player.addToHand(tempCard);
        }

        if(roundDeck.getDeckSize() == 0 && trumpCard.getCardFace()!='0' && numberOfDraws == 1)
        {
            player.addToHand(trumpCard);
            trumpCard = new Card('0', trumpCard.getCardSuit(), 0);
        }
    }

    /*returns the deck of cards*/
    public ArrayList<Card> getDeck() {
        return roundDeck.getCards();
    }

    /*returns the trump card*/
    public Card getTrumpCard() {
        return trumpCard;
    }

    /**
     processMoves, function to deal with the moves made by each player after a turn
     @return an integer that contains the index of the winning player for the turn
     */
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

    /*returns the index of the next player*/
    public int getNextPlayer() {
        return nextTurn;
    }


    /**
     play, function that processes a move made by a player
     @param cardID, an integer that contains the ID of the card chosen by the player
     @param listOfLogs, an arraylist of string used to display logs
     */
    public void play(Integer cardID, ArrayList<String> listOfLogs) {

        //make move for a player
        listOfPlayers.get(nextTurn).makeMove(cardID, listOfPlayers.get((nextTurn == 0)?1:0).getPlayedCards(),trumpCard);

        //only move is allowed, no meld
        moveOrMeld =true;

        String tempString = "the player chose : ";
        tempString+=listOfPlayers.get(nextTurn).getPlayedCards().size()+"---";
        tempString+=Character.toString(listOfPlayers.get(nextTurn).getPlayedCards().get(0).getCardFace());
        tempString+=Character.toString(listOfPlayers.get(nextTurn).getPlayedCards().get(0).getCardSuit());

        listOfLogs.add(tempString);

        //process played cards, in meld maps, and collections
        listOfPlayers.get(nextTurn).processPlayedCards();


        isTurnComplete = !isTurnComplete;
        nextTurn = (nextTurn == 0)?1:0;

        //if turn is complete
        if(isTurnComplete)
        {
            //evaluate the winning player
            nextTurn = processMoves();
            listOfLogs.set((listOfLogs.size()-1), listOfLogs.get(listOfLogs.size()-1)+"\n"+
                    listOfPlayers.get(nextTurn).getPlayerName()+ " won the move");

            //process the win
            processTurnWin();

            return;
        }
    }

    /*
    processTurnWin, process the played cards, scores, capture pile for the winning player of the turn
     */
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

    /*return if the next activity is a move or a meld*/
    public boolean getMoveOrMeld() {
        return moveOrMeld;
    }

    /**
     makeMeld, function that processes a meld made by a user
     @param selectedCard, a list of integer, which contains the ID of the cards chosen by the player.
     @param listOfLogs, an arraylist of string used to display logs
     */
    public void makeMeld(ArrayList<Integer> selectedCard, ArrayList<String> listOfLogs)
    {
        if(selectedCard.size()==0)
            listOfPlayers.get(nextTurn).decideMeldInterface(selectedCard, trumpCard, listOfLogs);


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
                if(listOfPlayers.get(nextTurn).getPlayerName().equals("Human"))
                {
                    StringBuilder tempString = new StringBuilder();
                    tempString.append("Human should choose the meld with cards");
                    for(Card card: mergedCards)
                    {
                        tempString.append(card.getCardFace()+card.getCardSuit());
                    }
                    tempString.append("to make a ");
                    tempString.append(MELDS.get(possibleMeld));
                }
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

    /**
     evaluateMeld, function that evaluates if and what meld a player has made
     @param mergedCards, list of cards chosen by a player
     @return an integer with the possible meld for a player
     */
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

        if(cardsInMeldPile == mergedCards.size())
            return 0;
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

    /*end of turn and draw cards*/
    public void drawCards() {
        moveOrMeld =true;
        dealCardsFromDeck(listOfPlayers.get(nextTurn), 1);
        dealCardsFromDeck(listOfPlayers.get((nextTurn==0)?1:0), 1);
    }

    /**
     getPlayerMeldHelp, function to give recommendation of a meld to the human player
     @param listOfLogs, a list of string used to display logs
     */
    public void getPlayerMeldHelp(ArrayList<String> listOfLogs) {
        ArrayList<Integer> selectedCardID= new ArrayList<Integer>();
        listOfPlayers.get(nextTurn).decideMeld(selectedCardID, trumpCard, listOfLogs);

    }

    /**
     getPlayerMove, function to give recommendation of a move to the human player
     @param listOfLogs, a list of string used to display logs
     */
    public void getPlayerMove(ArrayList<String> listOfLogs) {
        //lead
        if (listOfPlayers.get((nextTurn==0?1:0)).playedCards.size() == 0) {
            Card x = listOfPlayers.get(nextTurn).getTacticalCard(trumpCard);
            listOfLogs.add("Recommended to chose the card: "+ x.getCardFace()+" "+x.getCardSuit()+" after saving possible melds");
        }
        //chase
        else
        {
            Card x = listOfPlayers.get(nextTurn).getCheapestCard(listOfPlayers.get((nextTurn==0?1:0)).playedCards.get(0),trumpCard);
            listOfLogs.add("Recommended to chose the card: "+ x.getCardFace()+" "+x.getCardSuit()+" for possible win.");
        }

    }


    public String serialize() {
        return "hello save file here";
    }

    public void setTrumpCard(String card) {
        if(card.length() == 2 )trumpCard = new Card(card.charAt(0), card.charAt(1));
        else trumpCard = new Card('0', card.charAt(0));
    }

    public void setRoundDeck(String[] cards) {
        //vector to store the card objects
        ArrayList<Card> vectorOfCards = new ArrayList<>();

        //for each card in the string create a card object and insert in the player's hand
        for (int i = 0; i < cards.length; i++)
        {
            vectorOfCards.add(new Card(cards[i].charAt(0), cards[i].charAt(1)));
        }

        //set the deck with vectorOfCards
        roundDeck.setDeck(vectorOfCards);

    }

    public void setNextTurn(String player) {
        if (player.equals("Computer")) nextTurn = 1;
        else nextTurn = 0;
    }
}
