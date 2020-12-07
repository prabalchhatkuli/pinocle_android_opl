package com.stairway.pinocle_android_opl.model;

import java.util.ArrayList;

public class Computer extends Player{

    public Computer()
    {
        playerName = "Computer";
    }

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

        //play
        play(chosenCardID);
    }

    public void makeMeld()
    {
        //find melds

        //declare melds
    }

}
