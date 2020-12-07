package com.stairway.pinocle_android_opl.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Player {

    protected String playerName;
    protected ArrayList<Card> playerHand;
    protected ArrayList<Card> capturePile;
    protected ArrayList<Card> meldPile;
    protected ArrayList<Card> playedCards;

    //player scores
    protected int playerGameScore;
    protected int playerRoundScore;

    //player maps
    private static final Map<Integer, String> MELDS;
    private static final Map<Integer, Integer> MELD_POINTS;

    //variable to store the melds and corresponding cards for those melds
    protected Map<Integer, ArrayList<ArrayList<Card>>> meldToCardMap;

    //variable to store the cards and its corresponding list of melds
    protected Map<Card, ArrayList<Integer>> cardToMeldMap;

    //vector to store the list of possible melds for a list of cards
    ArrayList<Pair<ArrayList<Card>, Integer>> listOfPossibleMelds;

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

    public int getPlayerGameScore() {
        return playerGameScore;
    }

    public int getPlayerRoundScore(){
        return playerRoundScore;
    }
    public void addToRoundScore(int score){
        playerRoundScore += score;
    }

    public ArrayList<Card> getPlayerHand()
    {
        return  playerHand;
    }

    public ArrayList<Card> getCapturePile()
    {
        return  capturePile;
    }

    public ArrayList<Card> getMeldPile()
    {
        return  meldPile;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void addToHand(Card card){
        playerHand.add(card);
    }

    public void addToCapturePile(Card card){
        capturePile.add(card);
    }

    abstract void makeMove(Integer cardID, ArrayList<Card> playedCards, Card trumpCard);

    //abstract void makeMeld();

    public void play(Integer selectedCard) {
        //determine if the card is in the meld or the player cards
        boolean isInHandOrMeld = true;
        Card foundCard = null;

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

    public ArrayList<Card> getPlayedCards() {
        return playedCards;
    }

    public void addMeldScore(int possibleMeld) {
        this.playerRoundScore+=MELD_POINTS.get(possibleMeld);
    }

    public void removeCardsFromHand(ArrayList<Card> cardsFromHand) {
        for(Card card: cardsFromHand) {
            for (int i = playerHand.size() - 1; i >= 0; i--) {
                if (playerHand.get(i).getCardID() == card.getCardID()) {
                    playerHand.remove(i);
                }
            }
        }
    }

    public void addNewMeldCards(final int possibleMeld, ArrayList<Card> cardsFromHand) {
        for(Card card : cardsFromHand)
        {
            System.out.println(card.getCardFace()+card.getCardSuit());
            System.out.println("was processed");
            //add to meld pile
            meldPile.add(card);

            //add to card to meld map
            cardToMeldMap.put(card, new ArrayList<Integer>(){{add(possibleMeld);}});

        }

        System.out.println(meldPile.size());
    }

    public void updateMeldCards(int possibleMeld, ArrayList<Card> cardsFromMeld) {
        for(Card card: cardsFromMeld)
        {
            cardToMeldMap.get(card).add(possibleMeld);
        }
    }

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

    /*Computer and hint strategies*/
    public void decideMeld(Card trumpCard)
    {
        ArrayList<Card> listOfPlayableCards =  new ArrayList<Card>();

        int possibleScore;
        
        listOfPlayableCards.addAll(playerHand);
        listOfPlayableCards.addAll(meldPile);

        listOfPossibleMelds.clear();

        possibleScore = findPossibleScores(listOfPlayableCards, trumpCard);

        //if no possible melds, return
        if (0 == listOfPossibleMelds.size())
        {
            //cout << "No Melds are possible from the list of cards for the "<< getPlayerName() << " player." << endl;
            playedCards.clear();
            System.out.println("No melds are possible");
            return;
        }


        //sort the list of possible melds
        Collections.sort(listOfPossibleMelds, new Comparator<Pair<ArrayList<Card>, Integer>>() {
            @Override public int compare( final Pair<ArrayList<Card>, Integer> p1, final Pair<ArrayList<Card>, Integer> p2) {
                return p2.second-p1.second ; // Ascending
            }
        });

        //get the first element
        ArrayList<Card> chosenMeld = listOfPossibleMelds.get(0).first;

        System.out.println("The size of the predicted list is:");
        System.out.println(listOfPossibleMelds.size());
        for(Pair<ArrayList<Card>, Integer> each: listOfPossibleMelds)
        {
            for(Card card: each.first)
            {
                System.out.println(card.getCardFace()+" "+card.getCardSuit()+',');
            }
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        }

        //place all the cards needed for the meld into playedCards.
        playedCards.clear();
        playedCards.addAll(chosenMeld);

        System.out.println("Recommendation to chose:");
        for(Card card: chosenMeld)
        {
            System.out.println(card.getCardFace()+" "+card.getCardSuit());
        }


        return;
    }

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

    private void findCombinations(ArrayList<Card> listOfCards, int sizeOfListOfCards, int sizeOfCombinations, int[] scoreForThisList, Card trumpCard) {
        
        ArrayList<Card> dataVector = new ArrayList<>();
        for(int i=0; i<sizeOfCombinations; i++)
        {
            dataVector.add(new Card());
        }
        utilityForMeldCombinations(listOfCards, sizeOfListOfCards, sizeOfCombinations, 0, dataVector, 0, scoreForThisList, trumpCard);
    }

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
                //the section of the code below is for storing the meld so that it can be used while the user want to call meld
                listOfPossibleMelds.add(new Pair<ArrayList<Card>, Integer>(mergedCards, MELD_POINTS.get(possibleMeld)));

                scoreForThisList[0] += MELD_POINTS.get(possibleMeld);
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


        utilityForMeldCombinations(listOfCards, sizeOfListOfCards, sizeOfCombinations, indexForDataVector + 1, dataVector, indexForMainList + 1, scoreForThisList, trumpCard);

        utilityForMeldCombinations(listOfCards, sizeOfListOfCards, sizeOfCombinations, indexForDataVector, dataVector, indexForMainList + 1, scoreForThisList, trumpCard);


    }


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

    public void clearPlayedCards() {
        playedCards.clear();
    }

    public void getPlayerMove() {
    }

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
        for (int i = 0; i < listOfPlayableCards.size(); i++)
        {
            //temporary variable to store the list of cards when an element at index i is removed
            ArrayList<Card> tempListOfCards = new ArrayList<>();
            tempListOfCards.addAll(listOfPlayableCards);
            tempListOfCards.remove(i);

            int possibleScore = findPossibleScores(tempListOfCards, trumpCard);

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
        for (int i = 0; i < winningCards.size(); i++)
        {
            //if found return card
            if (winningCards.get(i).getCardSuit() != trumpCard.getCardSuit())
            {
                Card tempCard = winningCards.get(0);
                winningCards.set(0, winningCards.get(i));
                winningCards.set(i, tempCard);
                break;
            }
        }

        if (winningCards.size() == (playerHand.size() + meldPile.size()))
        {
           System.out.println( winningCards.get(0).getCardFace() + winningCards.get(0).getCardSuit() +" because it has no cards that will win the move.");
        }
        else
        {
            System.out.println( winningCards.get(0).getCardFace() + winningCards.get(0).getCardSuit() +"  because it could win the move.");
        }

        //if no such card is found
        //return the cheapest card: [0] first card of the sorted list
        return winningCards.get(0);

    }

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

    public Map getMeldToCardMap() {
        return meldToCardMap;
    }

    public Map getCardToMeldMap() {
        return cardToMeldMap;
    }

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

    public void setPlayerScores(int game, int round) {
        playerGameScore = game;
        playerRoundScore = round;
    }

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
}
