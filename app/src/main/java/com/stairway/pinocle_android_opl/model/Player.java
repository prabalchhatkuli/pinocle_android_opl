package com.stairway.pinocle_android_opl.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Player {

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
    protected Map<Integer, ArrayList<Card>> meldToCardMap;

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
        meldToCardMap = new HashMap<Integer, ArrayList<Card>>();
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
                playerHand.remove(card);
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
                meldPile.remove(card);
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

    public void addToMeldToCardMap(int possibleMeld, ArrayList<Card> mergedCards) {
        meldToCardMap.put(possibleMeld, mergedCards);
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
}
