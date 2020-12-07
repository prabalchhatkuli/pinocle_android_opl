package com.stairway.pinocle_android_opl.model;

import java.util.ArrayList;

public class Human extends Player{
    public Human()
    {
        playerName = "Human";
    }

    public void makeMove(Integer cardID, ArrayList<Card> playedCards, Card trumpCard)
    {
        //find if lead or chase player
        play(cardID);
    }

    public void makeMeld()
    {
        //find melds

        //declare melds
    }
}
