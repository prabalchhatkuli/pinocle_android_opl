package com.stairway.pinocle_android_opl.model;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> listOfPlayers;
    private int numRounds;
    private int winnerLastRound;
    private Round currentRound;

    public Game()
    {
        listOfPlayers = new ArrayList<Player>();
        listOfPlayers.add(new Human());
        listOfPlayers.add(new Computer());

        numRounds =1;
        winnerLastRound =0;
    }

    public ArrayList<Player> getListOfPlayers()
    {
        return listOfPlayers;
    }

    public ArrayList<Card> getDeck()
    {
        return currentRound.getDeck();
    }

    public void startGame()
    {
        currentRound = new Round();
        currentRound.startRound(listOfPlayers, winnerLastRound);
    }

    public Card getTrumpCard() {
        return currentRound.getTrumpCard();
    }

    public int getNextPlayer() {
        return currentRound.getNextPlayer();
    }

    public boolean getMoveOrMeld(){
        return currentRound.getMoveOrMeld();
    }

    public void processMove() {
        currentRound.processMoves();
    }

    public void play(Integer cardID) {
        currentRound.play(cardID);
    }

    public void makeMeld(ArrayList<Integer> selectedCard) {
        currentRound.makeMeld(selectedCard);
    }
}
