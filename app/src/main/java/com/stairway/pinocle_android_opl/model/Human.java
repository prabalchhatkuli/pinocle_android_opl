package com.stairway.pinocle_android_opl.model;

import java.util.ArrayList;

public class Human extends Player{
    /**
     class constructor
     */
    public Human()
    {
        playerName = "Human";
    }

    /**
     makeMove, function to make a move for the human player
     @param cardID, an integer which contains the ID of the card that was selected for the move
     @param playedCards, an integer which contains the cards played by the lead player if there was any
     @param trumpCard, a card object whicb contains the trump card for the current round
     */
    public void makeMove(Integer cardID, ArrayList<Card> playedCards, Card trumpCard)
    {
        //play the move
        play(cardID);
    }

    /**
     decideMeldInterface, a function to decide meld playable for the human, it does nothing
     @param selectedCard, an empty arraylist
     @param trumpCard, a card object which contains the trump card for the current round
     @param listOfLogs, an arraylist of string used to display logs
     */
    public void decideMeldInterface(ArrayList<Integer> selectedCard, Card trumpCard, ArrayList<String> listOfLogs)
    {
        //find melds
        return;
    }
}
