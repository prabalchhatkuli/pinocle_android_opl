package com.stairway.pinocle_android_opl.model;

public class Card {
    private char cardFace;
    private char cardSuit;
    private int cardPoints;
    private int cardID;

    public Card()
    {
        cardFace = 0;
        cardSuit = '0';
        cardPoints = 0;
        cardID = 0;
    }

    public Card(char cardFace, char cardSuit, int cardID)
    {
        //initialize member variables
        this.cardFace = cardFace;
        this.cardSuit = cardSuit;
        this.cardID = cardID;

        //note that:: the cards can only be of the faces mentioned in the cases below
        switch (cardFace)
        {
            case 'J': cardPoints = 2;
                break;
            case 'Q': cardPoints = 3;
                break;
            case 'K': cardPoints = 4;
                break;
            case 'X': cardPoints = 10;
                break;
            case 'A': cardPoints = 11;
                break;
            default:
                    cardPoints =0;
                break;
        }
    }

    public char getCardFace() { return this.cardFace; }
	public char getCardSuit() { return this.cardSuit; }
    public int getCardPoints() { return this.cardPoints; }
    public int getCardID(){return this.cardID;}

    public static class Game {
    }
}