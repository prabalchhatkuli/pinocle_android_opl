package com.stairway.pinocle_android_opl.model;

import java.util.ArrayList;

public class Computer extends Player{

    /**
     class constructor
     */
    public Computer()
    {
        playerName = "Computer";
    }

    /**
     makeMove, function to make a move for the computer player
     @param cardID, an integer which contains a random value
     @param playedCards, an integer which contains the cards played by the lead player if there was any
     @param trumpCard, a card object whicb contains the trump card for the current round
     */
    public void makeMove(Integer cardID, ArrayList<Card> playedCards, Card trumpCard)
    {
        Integer chosenCardID = 999;
        //find if lead or chase player
        if(playedCards.size()!=0)
        {
            //chase player
            chosenCardID = (getCheapestCard(playedCards.get(0), trumpCard)).getCardID();
        }
        else
        {
            //lead player
            chosenCardID = (getTacticalCard(trumpCard)).getCardID();
        }

        //play the chosen card
        play(chosenCardID);
    }

    /**
     decideMeldInterface, a function to decide meld playable for the computer
     @param selectedCard, an empty arraylist which will be populated in decideMeld function
     @param trumpCard, a card object which contains the trump card for the current round
     @param listOfLogs, an arraylist of string used to display logs
     */
    public void decideMeldInterface(ArrayList<Integer> selectedCard, Card trumpCard, ArrayList<String> listOfLogs)
    {
        //find melds and decide on one, for the computer
        decideMeld(selectedCard, trumpCard, listOfLogs);
    }

}
