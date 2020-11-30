package com.stairway.pinocle_android_opl.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
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
}
