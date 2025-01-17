package com.stairway.pinocle_android_opl.model;

import android.content.Context;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Player {

    //player maps
    private static final Map<Integer, String> MELDS;
    private static final Map<Integer, Integer> MELD_POINTS;

    //member variables
    protected String playerName;
    protected ArrayList<Card> playerHand;
    protected ArrayList<Card> capturePile;
    protected ArrayList<Card> meldPile;
    protected ArrayList<Card> playedCards;

    //player scores
    protected int playerGameScore;
    protected int playerRoundScore;

    //variable to store the melds and corresponding cards for those melds
    protected Map<Integer, ArrayList<ArrayList<Card>>> meldToCardMap;

    //variable to store the cards and its corresponding list of melds
    protected Map<Card, ArrayList<Integer>> cardToMeldMap;

    //vector to store the list of possible melds for a list of cards
    ArrayList<Pair<ArrayList<Card>, Integer>> listOfPossibleMelds;

    //static initializations for the final variables
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

    static {
        HashMap<Integer, Integer> aMap =  new HashMap<Integer, Integer>();
        aMap.put(1, 150);
        aMap.put(2, 40);
        aMap.put(3, 20);
        aMap.put(4, 10);
        aMap.put(5, 100);
        aMap.put(6,80);
        aMap.put(7, 60);
        aMap.put(8, 40);
        aMap.put(9, 40);
        MELD_POINTS = Collections.unmodifiableMap(aMap);
    }

    /**
     Player class default constructor
     */
    public Player()
    {
        playerGameScore = 0;
        playerRoundScore = 0;
        playerHand = new ArrayList<Card>();
        capturePile =new ArrayList<Card>();
        meldPile=new ArrayList<Card>();
        playedCards = new ArrayList<Card>();
        meldToCardMap = new HashMap<Integer, ArrayList<ArrayList<Card>>>();
        cardToMeldMap = new HashMap<Card, ArrayList<Integer>>();
        listOfPossibleMelds = new ArrayList<>();
    }

    /**
     getPlayerGameScore, get the score for the player
     @return an integer, with the player's game score
     */
    public int getPlayerGameScore() {
        return playerGameScore;
    }

    /**
     getPlayerRoundScore, get the round score for the player
     @return an integer, with the player's round score
     */
    public int getPlayerRoundScore(){
        return playerRoundScore;
    }

    /**
     addToRoundScore, add a score to the round score
     @param score, an integer which contains the score to be added
     */
    public void addToRoundScore(int score){
        playerRoundScore += score;
    }

    /**
     getPlayerHand, get the hand pile
     @return an Arraylist of card objects, which contains the player's hand cards
     */
    public ArrayList<Card> getPlayerHand()
    {
        return  playerHand;
    }

    /**
     getCapturePile, get the capture pile
     @return an Arraylist of card objects, which contains the player's capture cards
     */
    public ArrayList<Card> getCapturePile()
    {
        return  capturePile;
    }

    /**
     getMeldPile, get the meld pile
     @return an Arraylist of card objects, which contains the player's meld pile
     */
    public ArrayList<Card> getMeldPile()
    {
        return  meldPile;
    }

    /**
     * return the name of the player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     addToHand, add a card to the hand pile
     @param card, a Card object to be added to the hand
     */
    public void addToHand(Card card){
        playerHand.add(card);
    }

    /**
     addToCapturePile, add a card to the capture pile
     @param card, a Card object to be added to the pile
     */
    public void addToCapturePile(Card card){
        capturePile.add(card);
    }

    /**
     makeMove, function to make a move for the a player
     @param cardID, an integer which contains the ID of the card that was selected for the move
     @param playedCards, an integer which contains the cards played by the lead player if there was any
     @param trumpCard, a card object which contains the trump card for the current round
     */
    abstract void makeMove(Integer cardID, ArrayList<Card> playedCards, Card trumpCard);

    /**
     decideMeldInterface, a function to decide meld playable
     @param selectedCard, an empty arraylist which will be populated in decideMeld function
     @param trumpCard, a card object which contains the trump card for the current round
     @param listOfLogs, an arraylist of string used to display logs
     */
    abstract void decideMeldInterface(ArrayList<Integer> selectedCard, Card trumpCard, ArrayList<String> listOfLogs);

    /**
     play, a function to execute a card move made by a player
     @param selectedCard, an Integer which contains the ID of the card that was chosen
     */
    public void play(Integer selectedCard) {
        //determine if the card is in the meld or the player cards
        boolean isInHandOrMeld = true;
        Card foundCard = null;

        //find in the hand pile
        for(Card card: playerHand)
        {
            if(selectedCard == card.getCardID())
            {
                //card is in playerHand
                isInHandOrMeld = true;
                foundCard = card;
                //remove the card from the collection
                break;
            }
        }

        //find in the meld pile
        for(Card card: meldPile)
        {
            if(selectedCard == card.getCardID())
            {
                //card is in meldPile
                isInHandOrMeld = false;
                foundCard = card;
                //remove the card from the collection
                break;
            }
        }

        //add the card to playedCards
        playedCards.add(foundCard);

    }

    /*returns the list of cards played by the user for the turn*/
    public ArrayList<Card> getPlayedCards() {
        return playedCards;
    }

    /**
     addMeldScore, add the scores of a meld to player's round score
     @param possibleMeld, an integer which is a key to a particular meld
     */
    public void addMeldScore(int possibleMeld) {
        this.playerRoundScore+=MELD_POINTS.get(possibleMeld);
    }

    /**
     removeCardsFromHand, remove a list of cards from the player's hand
     @param cardsFromHand, an ArrayList of Card objects from the user's hand played for the move or meld
     */
    public void removeCardsFromHand(ArrayList<Card> cardsFromHand) {
        for(Card card: cardsFromHand) {
            for (int i = playerHand.size() - 1; i >= 0; i--) {
                if (playerHand.get(i).getCardID() == card.getCardID()) {
                    playerHand.remove(i);
                }
            }
        }
    }

    /**
     addNewMeldCards, add hand cards to meld pile
     @param possibleMeld, an integer which contains the meld that was made
     @param cardsFromHand, cards from hand that need to be processed into meld cards
     */
    public void addNewMeldCards(final int possibleMeld, ArrayList<Card> cardsFromHand) {
        for(Card card : cardsFromHand)
        {
            //add to meld pile
            meldPile.add(card);

            //add to card to meld map
            cardToMeldMap.put(card, new ArrayList<Integer>(){{add(possibleMeld);}});

        }

        System.out.println(meldPile.size());
    }

    /**
     updateMeldCards, update the existing meld collection
     @param possibleMeld, an integer which contains the meld that was made
     @param cardsFromMeld, cards from meld that need to be reevaluated for the maps
     */
    public void updateMeldCards(int possibleMeld, ArrayList<Card> cardsFromMeld) {
        for(Card card: cardsFromMeld)
        {
            cardToMeldMap.get(card).add(possibleMeld);

            if(!meldPile.contains(card))
            {
                meldPile.add(card);
            }
        }
    }

    /**
     addToMeldToCardMap, add a record in the cardToMeldMap
     @param possibleMeld, an integer which contains the meld that was made
     @param mergedCards, cards to be added into the meldToCard map
     */
    public void addToMeldToCardMap(int possibleMeld, final ArrayList<Card> mergedCards) {

        //search for the possible meld in the meldToCardMap
        //if exists add to the arrayList
        if(meldToCardMap.containsKey(possibleMeld))
        {
            meldToCardMap.get(possibleMeld).add(mergedCards);
        }
        //else, create a new entry
        else
        {
            meldToCardMap.put(possibleMeld, new ArrayList<ArrayList<Card>>(){{add(mergedCards);}});
        }
    }

    /**
     addToMeldToCardMap, add a record in the cardToMeldMap
     @param selectedCard, the cards that need to be populated for the computer
     @param trumpCard, a card object which contains the trump card for the current round
     @param listOfLogs, an arraylist of string used to display logs
     */
    public void decideMeld(ArrayList<Integer> selectedCard, Card trumpCard, ArrayList<String> listOfLogs)
    {
        //variable declarations
        ArrayList<Card> listOfPlayableCards =  new ArrayList<Card>();

        int possibleScore;

        //find list of playable cards
        listOfPlayableCards.addAll(playerHand);
        listOfPlayableCards.addAll(meldPile);

        listOfPossibleMelds.clear();

        possibleScore = findPossibleScores(listOfPlayableCards, trumpCard);

        //if no possible melds, return
        if (0 == listOfPossibleMelds.size())
        {
            //cout << "No Melds are possible from the list of cards for the "<< getPlayerName() << " player." << endl;
            playedCards.clear();
            listOfLogs.add("No melds are possible");
            return;
        }


        //sort the list of possible melds
        Collections.sort(listOfPossibleMelds, new Comparator<Pair<ArrayList<Card>, Integer>>() {
            @Override public int compare( final Pair<ArrayList<Card>, Integer> p1, final Pair<ArrayList<Card>, Integer> p2) {
                return (MELD_POINTS.get(p2.second)-MELD_POINTS.get(p1.second)) ; // Ascending
            }
        });

        //get the first element
        ArrayList<Card> chosenMeld = listOfPossibleMelds.get(0).first;


        //place all the cards needed for the meld into playedCards.
        playedCards.clear();
        //playedCards.addAll(chosenMeld);

        String displayString = new String();

        if(playerName.equals("Human"))
            displayString+="Human should choose the cards:";
        else
            displayString+="Computer played the cards:";

        for(Card card: chosenMeld)
        {
            selectedCard.add(card.getCardID());
            displayString+=(card.getCardFace()+" "+card.getCardSuit());

        }

        displayString+=" to make a ";
        displayString+=MELDS.get(listOfPossibleMelds.get(0).second);

        listOfLogs.add(displayString);

        return;
    }

    /**
     findPossibleScores, find possible meld scores that can be made from a set of cards
     @param listOfCards, an arraylist of cards that are to be evaluated
     @param trumpCard, a card object which contains the trump card for the current round
     */
    private int findPossibleScores(ArrayList<Card> listOfCards, Card trumpCard) {
        //else:: we need possible scores for max::listOfCards.size() cards and min 1
        int[] scoreForThisList=new int[]{0};
        int size = listOfCards.size();

        //for 1-5 cards
        //we only need to create combinations of 1 to 5 cards because,
        //melds can only be of maximum size 5
        for (int i = 1; i < 6; i++)
        {
            findCombinations(listOfCards, size, i, scoreForThisList, trumpCard);
        }

        return scoreForThisList[0];
    }
    /**
     findPossibleScores, find possible meld scores that can be made from a set of cards
     @param listOfCards, an arraylist of cards that are to be evaluated
     @param sizeOfListOfCards, an integer which contains the size of the list
     @param sizeOfCombinations, an integer that specifies the size of combinations for cards
     @param scoreForThisList, an integer array, which stores the score in the first element
     @param trumpCard, a card object which contains the trump card for the current round
     */
    private void findCombinations(ArrayList<Card> listOfCards, int sizeOfListOfCards, int sizeOfCombinations, int[] scoreForThisList, Card trumpCard) {
        
        ArrayList<Card> dataVector = new ArrayList<>();
        for(int i=0; i<sizeOfCombinations; i++)
        {
            dataVector.add(new Card());
        }
        utilityForMeldCombinations(listOfCards, sizeOfListOfCards, sizeOfCombinations, 0, dataVector, 0, scoreForThisList, trumpCard);
    }

    /**
     findPossibleScores, find possible meld scores that can be made from a set of cards
     @param listOfCards, an arraylist of cards that are to be evaluated
     @param sizeOfListOfCards, an integer which contains the size of the list
     @param sizeOfCombinations, an integer that specifies the size of combinations for cards
     @param indexForDataVector, an integer that has the index in the datavector
     @param dataVector, a list of cards of a specific combination
     @param indexForMainList, an integer for creating combinations from the main list
     @param scoreForThisList, an integer array, which stores the score in the first element
     @param trumpCard, a card object which contains the trump card for the current round
     */
    private void utilityForMeldCombinations(ArrayList<Card> listOfCards, int sizeOfListOfCards, int sizeOfCombinations, int indexForDataVector, ArrayList<Card> dataVector, int indexForMainList, int[] scoreForThisList, Card trumpCard) {
        // if combination has reached the require size
        if (indexForDataVector == sizeOfCombinations)
        {
            //at this point dataVector has a combination of "sizeOfCombinations" elements
            // set the "playedCards" variable of Player class to the dataVector: this is done because the evaluateMeld() function uses data from playedCards
            ArrayList<Card> mergedCards = new ArrayList<>();
            mergedCards.addAll(dataVector);

            //evaluate possible meld
            int possibleMeld = evaluateMeld(mergedCards, trumpCard);

            //if any meld possible: add to the score for this list
            if (0 != possibleMeld)
            {

                // find all entries with possible meld in listofpossiblemeld
                Iterator<Pair<ArrayList<Card>, Integer>> myIter = listOfPossibleMelds.iterator();

                Boolean cardFound = false;

                //for all entries
                outerLoop:
                for(Pair<ArrayList<Card>, Integer> singleMeld: listOfPossibleMelds)
                {
                    if(singleMeld.second.equals(possibleMeld)) {
                        for (Card card : mergedCards) {
                            for(Card setCard: singleMeld.first)
                            {
                                if(card.getCardID()==setCard.getCardID())
                                {
                                    cardFound =true;
                                    break outerLoop;
                                }
                            }
                        }
                    }
                }

                //if none found do nothing
                if(!cardFound)
                {
                    //the section of the code below is for storing the meld so that it can be used while the user want to call meld
                    listOfPossibleMelds.add(new Pair<ArrayList<Card>, Integer>(mergedCards, possibleMeld));

                    scoreForThisList[0] += MELD_POINTS.get(possibleMeld);
                    System.out.println(MELDS.get(possibleMeld));
                }
            }

            //clear "playedCards" variable
            playedCards.clear();

            //scoreForThisList++;

            return;
        }

        //  if all the size of cards are chosen
        if (indexForMainList >= sizeOfListOfCards) {
            return;
        }

        // current is included, put next at next location
        dataVector.set(indexForDataVector, listOfCards.get(indexForMainList));


        //dfs--search recursions
        utilityForMeldCombinations(listOfCards, sizeOfListOfCards, sizeOfCombinations, indexForDataVector + 1, dataVector, indexForMainList + 1, scoreForThisList, trumpCard);

        utilityForMeldCombinations(listOfCards, sizeOfListOfCards, sizeOfCombinations, indexForDataVector, dataVector, indexForMainList + 1, scoreForThisList, trumpCard);

    }


    /**
     evaluateMeld, function to evaluate what and if a meld is possible
     @param mergedCards, an ArrayList of card objects to be evaluated
     @param trumpCard, a card object which contains the trump card for the current round
     */
    private int evaluateMeld(ArrayList<Card> mergedCards,Card trumpCard) {
        //variable to store the index of the possible meld
        int possibleMeld = 0;

        //variable to store the number of cards used in previous melds
        int cardsInMeldPile = 0;

        for(Card card: meldPile)
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
                            return possibleMeld;
                        }

                        //if same suit, return false
                        if (mergedCards.get(i).getCardSuit() == mergedCards.get(j).getCardSuit())
                        {
                            possibleMeld = 0;
                            return possibleMeld;
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
                ArrayList<Integer> meldsOfCard = cardToMeldMap.get(card);

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

    /*clear played cards*/
    public void clearPlayedCards() {
        playedCards.clear();
    }

    public void getPlayerMove() {
    }

    /**
     getTacticalCard, find the best card for a lead player
     @param trumpCard, a card object which contains the trump card for the current round
     @return  a card object evaluated to be the best move
     */
    public Card getTacticalCard(Card trumpCard){
        //vector to store the overall meldscore if we remove a card from the deck
        ArrayList<Integer> meldScoreEachCard = new ArrayList<>();

        //vector to store all playable cards
        ArrayList<Card> listOfPlayableCards = new ArrayList<>();

        //temporary vector to store cards with maximum points
        ArrayList<Card> cardsWithMaxPoints = new ArrayList<>();

        //create a vector with all playable cards: combine hand and meld pile
        listOfPlayableCards.addAll(playerHand);
        listOfPlayableCards.addAll(meldPile);

        //iterate the vector
        //for each element(Card*): create another vector by removing that element from the overall vector
        ArrayList<Pair<ArrayList<Card>, Integer>> tempListOfAllPossibleMelds = new ArrayList<>();
        for (int i = 0; i < listOfPlayableCards.size(); i++)
        {
            //temporary variable to store the list of cards when an element at index i is removed
            ArrayList<Card> tempListOfCards = new ArrayList<>();
            tempListOfCards.addAll(listOfPlayableCards);
            tempListOfCards.remove(i);

            int possibleScore = findPossibleScores(tempListOfCards, trumpCard);
            //tempListOfAllPossibleMelds.addAll(listOfPossibleMelds);
            listOfPossibleMelds.clear();
            //add the score of removing this card
            meldScoreEachCard.add(possibleScore);
        }

        int maxPoints = 0;

        //find the indexes with the highest points for the melds in overalllMeldScoreEachCard
        maxPoints = Collections.max(meldScoreEachCard);

        for (int i = 0; i < meldScoreEachCard.size(); i++)
        {
            //this means that the if this card is removed, we will have the highest possibility of scoring in meld, if we win
            if (maxPoints == meldScoreEachCard.get(i))
            {
                cardsWithMaxPoints.add(listOfPlayableCards.get(i));
            }
        }

        //find the card with the highest value
        //sort them based on points in descending order
        Collections.sort(cardsWithMaxPoints, new Comparator<Card>() {
            @Override public int compare(  Card p1, Card p2) {
                return p2.getCardPoints()-p1.getCardPoints() ;
            }
        });


        //from the cards with high meld points choose the first card/ if there is a trump card suit with equal points

        for (int i = 0; i < cardsWithMaxPoints.size(); i++)
        {
            if (cardsWithMaxPoints.get(i).getCardSuit() == trumpCard.getCardSuit())
            {
                return cardsWithMaxPoints.get(i);
            }
        }

        return cardsWithMaxPoints.get(0);

    }

    /**
     getCheapestCard, find the best card for a chase player
     @param trumpCard, a card object which contains the trump card for the current round
     @return  a card object evaluated to be the best move
     */
    public Card getCheapestCard(Card leadCard, Card trumpCard){
        //variable to store the winning cards
        ArrayList<Card> winningCards;

        //find all playable cards which will win the turn
        winningCards = findPlayableCards(leadCard, trumpCard);

        //sort them based on points on ascending order
        Collections.sort(winningCards, new Comparator<Card>() {
            @Override public int compare(  Card p1, Card p2) {
                return p1.getCardPoints()-p2.getCardPoints() ;
            }
        });

        //find the first card on the vector that is not of trump suit


        //from the winning cards, evaluate all melds for each card
        //find the most tactical card
        return getCheapestMeldAccountedCard(winningCards, trumpCard);

        //if no such card is found
        //return the cheapest card: [0] first card of the sorted list
        //return winningCards.get(0);

    }

    /**
     getCheapestMeldAccountedCard, find the best card for a chase player from a list of winning cards by accounting the meld
     @param winningCards, a list of cards that will win the move for the chase player
     @param trumpCard, a card object which contains the trump card for the current round
     @return  a card object evaluated to be the best move
     */
    private Card getCheapestMeldAccountedCard(ArrayList<Card> winningCards, Card trumpCard) {

        //vector to store the overall meldscore if we remove a card from the deck
        ArrayList<Integer> meldScoreEachCard = new ArrayList<>();

        //vector to store all playable cards
        ArrayList<Card> listOfPlayableCards = new ArrayList<>();

        //temporary vector to store cards with maximum points
        ArrayList<Card> cardsWithMaxPoints = new ArrayList<>();

        //
        //create a vector with all playable cards: combine hand and meld pile
        listOfPlayableCards.addAll(playerHand);
        listOfPlayableCards.addAll(meldPile);

        //for each element(Card*): create another vector by removing that element from the overall vector
        for (int i = 0; i < winningCards.size(); i++)
        {
            //temporary variable to store the list of cards when an element at index i is removed
            ArrayList<Card> tempListOfCards = new ArrayList<>();
            tempListOfCards.addAll(listOfPlayableCards);
            tempListOfCards.remove(winningCards.get(i));

            int possibleScore = findPossibleScores(tempListOfCards, trumpCard);

            listOfPossibleMelds.clear();
            //add the score of removing this card
            meldScoreEachCard.add(possibleScore);
        }


        int maxPoints = 0;

        //find the indexes with the highest points for the melds in overalllMeldScoreEachCard
        maxPoints = Collections.max(meldScoreEachCard);


        for (int i = 0; i < meldScoreEachCard.size(); i++)
        {
            //this means that the if this card is removed, we will have the highest possibility of scoring in meld, if we win
            if (maxPoints == meldScoreEachCard.get(i))
            {
                cardsWithMaxPoints.add(winningCards.get(i));
            }
        }

        Collections.sort(cardsWithMaxPoints, new Comparator<Card>() {
            @Override public int compare(  Card p1, Card p2) {
                return p2.getCardPoints()-p1.getCardPoints() ;
            }
        });

        //from the cards with high meld points choose the first card/ if there is a trump card suit with equal points
        for (int i = (cardsWithMaxPoints.size()-1); i >=0; i--)
        {
            //if found return card
            if (cardsWithMaxPoints.get(i).getCardSuit() != trumpCard.getCardSuit())
            {
                //Card tempCard = cardsWithMaxPoints.get(0);
                return cardsWithMaxPoints.get(i);
                //cardsWithMaxPoints.set(0, cardsWithMaxPoints.get(i));
                //cardsWithMaxPoints.set(i, tempCard);
                //break;
            }
        }

        return cardsWithMaxPoints.get(cardsWithMaxPoints.size()-1);
    }

    /**
     findPlayableCards, finds the winning cards if possible, otherwise returns all playable cards
     @param leadCard, a card object, the card played by the lead player for the turn
     @param trumpCard, a card object which contains the trump card for the current round
     @return  an either an ArrayList of card objects which is playable by the player to win, or all playable cards
     */
    private ArrayList<Card> findPlayableCards(Card leadCard, Card trumpCard) {
        //vector to store cards that should be used on the move
        ArrayList<Card> listOfPlayableCards =  new ArrayList<>();

        //find all cards from the hand pile which will win the turn
        for (int i = 0; i < playerHand.size(); i++)
        {
            if (compareTwoCards(leadCard, playerHand.get(i), trumpCard))
            {
                listOfPlayableCards.add(playerHand.get(i));
            }
        }
        //same for meldPile
        for (int i = 0; i < meldPile.size(); i++)
        {
            if (compareTwoCards(leadCard, meldPile.get(i), trumpCard))
            {
                listOfPlayableCards.add(meldPile.get(i));
            }
        }

        //if no card is found that can win the turn
        //get all cards
        if (0 == listOfPlayableCards.size())
        {
            listOfPlayableCards.addAll(playerHand);
            listOfPlayableCards.addAll(meldPile);
        }

        return listOfPlayableCards;
    }

    /**
     compareTwoCards, determines if a chase card can win the lead card
     @param leadCard, a card object, the card played by the lead player for the turn
     @param chaseCard, a card object, the card played by the chase player for the turn
     @param trumpCard, a card object which contains the trump card for the current round
     @return a boolean , true if win is possible false otherwise
     */
    private boolean compareTwoCards(Card leadCard, Card chaseCard, Card trumpCard) {
        //if both cards are same, lead player wins
        if (leadCard.getCardSuit() == chaseCard.getCardSuit() && leadCard.getCardFace() == chaseCard.getCardFace())
        {
            return false;
        }

        //if lead card is of trump suit
        else if (trumpCard.getCardSuit() == leadCard.getCardSuit())
        {
            //if chase is also of trump suit and has more point than lead then the chase player wins the turn
            if (trumpCard.getCardSuit() == chaseCard.getCardSuit() && chaseCard.getCardPoints() > leadCard.getCardPoints())
            {
                return true;
            }
            //else the lead player wins
            else
            {
                return false;
            }
        }
        //if  lead card is not of trump suit,
        else if (trumpCard.getCardSuit() != leadCard.getCardSuit())
        {
            //if chase is of trump suit, chase wins
            if (trumpCard.getCardSuit() == chaseCard.getCardSuit())
            {
                return true;
            }
            //if lead and chase are same suit and chase has higher card face, chase wins
            else if (leadCard.getCardSuit() == chaseCard.getCardSuit() && chaseCard.getCardPoints() > leadCard.getCardPoints())
            {
                return true;
            }
            //otherwise lead wins
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     @return the meldToCardMap
     */
    public Map getMeldToCardMap() {
        return meldToCardMap;
    }

    /**
     @return the cardToMeldMap
     */
    public Map getCardToMeldMap() {
        return cardToMeldMap;
    }

    /**
     processPlayedCards, process played cards from the hand or meld pile
                            also updates the maps and collections
     */
    public void processPlayedCards() {
        //process played cards, hand, and meld cards,
        //if the played card is in hand pile: 1)it can either be only part of a hand, 2) it can be part of a hand and an earlier meld
        if (playerHand.contains(playedCards.get(0)))
        {
            //check if it is in the card to meld map as well
            //if true remove from meld pile also
            if (cardToMeldMap.containsKey(playedCards.get(0)))
            {
                cardToMeldMap.remove(playedCards.get(0));
            }

            //remove card from hand pile
            playerHand.remove(playedCards.get(0));
        }
        else//else: the cards are in an active meld: in the meld pile and the cardToMeldMap
        {
            //find the card entry in cardToMeldMap, im is the list of melds for the card
            ArrayList<Integer> im = cardToMeldMap.get(playedCards.get(0));

            //for each of the card's meld
            for (int i = 0; i < im.size(); i++)
            {
                //temporary variable which stores the current meld for the chosen card
                Integer tempMeld = im.get(i);

                //Among the vectors in the entry in meldToCardMap, find the one in which this card has been used
                for (int j = 0; j < meldToCardMap.get(tempMeld).size(); j++)
                {

                    //iv is the iterator to the found vector
                    if (((meldToCardMap.get(tempMeld)).get(j)).contains(playedCards.get(0)))
                    {
                        //for all other cards in this vector: we need to decide whether to send them to the hand or not
                        for (int k = 0; k < ((meldToCardMap.get(tempMeld)).get(j)).size(); k++)
                        {
                            //if same card, continue
                            if(playedCards.get(0).getCardID() == ((meldToCardMap.get(tempMeld)).get(j).get(k)).getCardID())
                            {
                                continue;
                            }
                            //find it in the cardToMeldMap
                            ArrayList<Integer> meldCheckIterator = cardToMeldMap.get((meldToCardMap.get(tempMeld).get(j).get(k)));

                            //flag to see if variable found
                            Boolean isfound = false;

                            //go to each of the mentioned meld to find the card
                            for (int l = 0; l < meldCheckIterator.size(); l++)
                            {
                                //temporary variable for checking if the meld exists in the map
                                Integer tempCheckMeld = meldCheckIterator.get(l);

                                //if the checkmeld is the same as parent meld, continue, because we are going to remove from this
                                if (tempCheckMeld.equals(tempMeld)) continue;

                                for (int m = 0; m < meldToCardMap.get(tempCheckMeld).size(); m++)
                                {
                                    //if found:
                                    if (((meldToCardMap.get(tempCheckMeld).get(m)).contains((meldToCardMap.get(tempMeld)).get(j).get(k))))
                                    {
                                        //only erase the original vector from the original meld entry in meldToCard map
                                        //no need to send the to player's hand
                                        isfound = true;
                                        break;
                                    }
                                }

                                if (isfound)
                                {
                                    break;
                                }
                            }
                            //if the card is not found in any of the listed meld, it means that the melds are no longer active
                            //send the card to the user's hand
                            if (!isfound)
                            {
                                addToHand((meldToCardMap.get(tempMeld)).get(j).get(k));
                                //find in meld pile
                                //remove from meld pile
                                if (meldPile.contains((meldToCardMap.get(tempMeld)).get(j).get(k)))
                                    meldPile.remove((meldToCardMap.get(tempMeld)).get(j).get(k));
                                else
                                    System.out.println("Error!! not found in the pile");
                            }
                            //else do nothing
                        }
                        //erase the vector containing iv from meldtoCardMap ((meldToCardMap[tempMeld])[j])
                        ((meldToCardMap.get(tempMeld))).remove(j);
                    }
                }
            }

            //erase from cardToMeld map
            cardToMeldMap.remove(playedCards.get(0));

            //erase from meldpile
            if (meldPile.contains(playedCards.get(0)))
            {
                meldPile.remove(playedCards.get(0));
            }
            else
                System.out.println("error in deleting the chosen card");
        }

    }

    /**
     setPlayerScores, function to update the scores for the player from the load file
     @param game, an integer, which contains the loaded game score
     @param round, an integer, which contains the loaded round score
     */
    public void setPlayerScores(int game, int round) {
        playerGameScore = game;
        playerRoundScore = round;
    }

    /**
     setPlayerHand, set the hand from the loaded file
     @param cards, an array of strings, which has the ascii representation of cards to be loaded
     */
    public void setPlayerHand(String[] cards) {
        //vector to store the card objects
        ArrayList<Card> vectorOfCards = new ArrayList<>();

        //for each card in the string create a card object and insert in the player's hand
        for (int i = 0; i < cards.length; i++)
        {
            vectorOfCards.add(new Card(cards[i].charAt(0), cards[i].charAt(1)));
        }

        //set the card to player hand
        playerHand.clear();
        playerHand.addAll(vectorOfCards);
    }

    /**
     setCapturePile, set the capture pile from the loaded file
     @param cards, an array of strings, which has the ascii representation of cards to be loaded
     */
    public void setCapturePile(String[] cards) {

        ArrayList<Card> vectorOfCards = new ArrayList<>();

        //for each card in the string create a card object and insert in the player's hand
        for (int i = 0; i < cards.length; i++)
        {
            vectorOfCards.add(new Card(cards[i].charAt(0), cards[i].charAt(1)));
        }

        //set the card to player hand
        capturePile.clear();
        capturePile.addAll(vectorOfCards);
    }

    /**
     setMeldPile, set the meld pile and collections from a 2-d arraylist of strings
     @param meldCards, an array of array of strings, to be loaded into the meld collections
     @param trumpCard, a card object which contains the trump card for the current round
     */
    public void setMeldPile(ArrayList<ArrayList<String>> meldCards, Card trumpCard) {

        //variable to act as a buffer for repeated meld cards (i.e. with *)
        Map<Card, ArrayList<Integer>> cardToMeldMapBuffer =  new HashMap<>();

        //variable to store the current Meld
        Integer currentMeld;

        for (int i = 0; i != meldCards.size(); i++)
        {
            ArrayList<Card> mergedCards = new ArrayList<>();
            //insert from meldCards to played cards by creating new cards
            for (int j=0; j<meldCards.get(i).size(); j++)
            {
                mergedCards.add(new Card(meldCards.get(i).get(j).charAt(0), meldCards.get(i).get(j).charAt(1)));
            }

            //evaluate the meld with these cards
            currentMeld = evaluateMeld(mergedCards, trumpCard);

            //reset the played cards because we don't need it from now
            mergedCards.clear();

            //variable to store the cards in the current Meld currently being evaluated
            final ArrayList<Card> currentMeldCards = new ArrayList<>();

            //for each of the cards in the meldsCards, evaluate the meld and insert in the respective member maps
            for (int j = 0; j < meldCards.get(i).size(); j++)
            {
                //variable to store the card
                Card chosenCard = null;

                //if the card is being shared with another meld, it has an asterisk
                if (3 == meldCards.get(i).get(j).length() && '*' == meldCards.get(i).get(j).charAt(2))
                {
                    //find a card in the cardToMeldBuffer which does not have the "currentMeld" in it
                    ArrayList<Card> possibleCard= new ArrayList<>();

                    Iterator it = cardToMeldMapBuffer.entrySet().iterator();
                    while (it.hasNext())
                    {
                        Map.Entry im = (Map.Entry)it.next();
                        if ( ((Card)im.getKey()).getCardSuit() == meldCards.get(i).get(j).charAt(1) && ((Card)im.getKey()).getCardFace() == meldCards.get(i).get(j).charAt(0))
                        {
                            if(((ArrayList<Integer>)im.getValue()).contains(currentMeld))
                            {
                                possibleCard.add((Card) im.getKey());
                            }
                        }
                    }

                    //for each of the possible cards, find out which one has only one meld
                    //if such card is found, choose this card
                    //otherwise add to the first card in the list

                    Boolean isCardFound = false;

                    for (int k = 0; k < possibleCard.size(); k++)
                    {
                        if ((cardToMeldMapBuffer.get(possibleCard.get(i)).size() == 1))
                        {
                            chosenCard = possibleCard.get(i);
                            isCardFound = true;
                            break;
                        }
                    }

                    //if a card was found
                    if (isCardFound)
                    {
                        cardToMeldMapBuffer.get(chosenCard).add(currentMeld);
                    }
                    //if a card was not found and there was no similar card in the buffer map
                    else if(!isCardFound && 0 == possibleCard.size())
                    {
                        chosenCard = new Card(meldCards.get(i).get(j).charAt(0), meldCards.get(i).get(j).charAt(1));
                        //insert into the buffer
                        final Integer tempMeld = currentMeld;
                        cardToMeldMapBuffer.put(chosenCard,new ArrayList<Integer>(){{add(tempMeld);}});
                        //insert into the meld list
                        meldPile.add(chosenCard);
                    }
                    else // possible card was found, and there were no cards with a only a single Meld
                    {
                        (cardToMeldMapBuffer.get(possibleCard.get(0))).add(currentMeld);
                    }
                }
                else// it is not being shared with another meld
                {
                    //no need to insert in the buffer, directly insert to the main map
                    chosenCard = new Card(meldCards.get(i).get(j).charAt(0), meldCards.get(i).get(j).charAt(1));

                    final Integer tempMeld = currentMeld;
                    cardToMeldMap.put(chosenCard, new ArrayList<Integer>(){{add(tempMeld);}});

                    //insert to the meld pile
                    meldPile.add(chosenCard);
                }

                //add the chosen card to the vector of cards for the current meld
                currentMeldCards.add(chosenCard);
            }

            //add to an entry for the meldToCard map
            //if the current meld already exists in the meldToCardMap, push the meld vector
            if (meldToCardMap.containsKey(currentMeld))
            {
                meldToCardMap.get(currentMeld).add(currentMeldCards);
            }
            //else, create a new entry
            else
            {
                meldToCardMap.put(currentMeld, new ArrayList<ArrayList<Card>>(){{add(currentMeldCards);}});
            }
        }

        //finally merge the buffer into the main cardToMeldMap
        cardToMeldMap.putAll(cardToMeldMapBuffer);
    }

    /*claear all info used for a round*/
    public void clearAllInfo() {
        playerHand.clear();
        meldPile.clear();
        playedCards.clear();
        capturePile.clear();

        playerGameScore+=playerRoundScore;
        playerRoundScore=0;

        cardToMeldMap.clear();
        meldToCardMap.clear();


    }

    /*returns the total score for a player*/
    public int getTotalScore() {
        return playerGameScore+playerRoundScore;
    }

    /**
     * returns a ascii representation of the player's meld information by evaluating the collections
     */
    public String getMeldString() {
        Map<Card, ArrayList<Integer>> mp = getMeldToCardMap();

        String meldString = new String();

        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry im = (Map.Entry)it.next();

            ArrayList<ArrayList<Card>> tempVectorOfAMeld= (ArrayList<ArrayList<Card>>) im.getValue();

            if (0 == tempVectorOfAMeld.size()) {
                continue;
            }

            Integer currentMeld = (Integer) im.getKey();

            for ( int i = 0; i < tempVectorOfAMeld.size(); i++)
            {
                final ArrayList<Card> vectorWithinVector = tempVectorOfAMeld.get(i);

                for ( int j = 0; j < vectorWithinVector.size(); j++)
                {
                    //append the card to the output string
                    //vectorWithinVector[j] is the card

                    meldString += vectorWithinVector.get(j).getCardFace();
                    meldString += vectorWithinVector.get(j).getCardSuit();

                    final int index = j;

                    //check if an asterisk is required
                    //flag to see if the card was used in another active meld
                    Boolean isFound = false;

                    //go to the card index in card to meld map
                    ArrayList<Integer> meldsForACard = (ArrayList<Integer>) getCardToMeldMap().get(vectorWithinVector.get(j));

                    //for each meld mentioned for the card
                    for ( int k = 0; k < meldsForACard.size(); k++)
                    {
                        //go to the meld to see if another meld with the card is still active
                        if (currentMeld == meldsForACard.get(k)) {
                            continue;
                        } else
                        {
                            //collection of vectors for this meld
                            ArrayList<ArrayList<Card>> tempCheckMeldCollection = (ArrayList<ArrayList<Card>>) getMeldToCardMap().get(meldsForACard.get(k));

                            //for the vector of melds hence received
                            for ( int l = 0; l < tempCheckMeldCollection.size(); l++)
                            {
                                //if the card is found an asterisk is required, break off the program, go to the next card
                                if (tempCheckMeldCollection.get(l).contains(vectorWithinVector.get(j)))
                                {
                                    System.out.println("Star found");
                                    meldString += "*";

                                    isFound = true;
                                    break;
                                }
                            }
                            if (isFound)
                                break;
                        }
                    }
                }
                //line separating melds
                meldString +=",";
            }

        }
        return meldString;
    }
}
