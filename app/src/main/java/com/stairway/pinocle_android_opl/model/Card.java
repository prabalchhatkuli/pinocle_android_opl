package com.stairway.pinocle_android_opl.model;

public class Card {
    //for card face, suit, ID, points and static counter for ID
    private char cardFace;
    private char cardSuit;
    private int cardPoints;
    private int cardID;
    private static int cardIDCounter=0;

    /**
     static function that increments the ID counter
     */
    private static void incrementCardCounter() {
        cardIDCounter+=1;
    }

    /**
     default constructor for a card
     */
    public Card()
    {
        cardFace = '0';
        cardSuit = '0';
        cardPoints = 0;
        cardID = 0;
    }

    /**
     Overloaded constructor with 3 parameters
     @param cardFace, a character , that contains the face of the card
     @param cardSuit, a character, that contains the suit of the card
     @param cardID, an integer, that contains the unique ID for a card
     */
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

    /**
     Overloaded constructor with 3 parameters
     @param cardFace, a character , that contains the face of the card
     @param cardSuit, a character, that contains the suit of the card
     */
    public Card(char cardFace, char cardSuit)
    {
        //initialize member variables
        this.cardFace = cardFace;
        this.cardSuit = cardSuit;
        this.cardID = cardIDCounter;
        incrementCardCounter();

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


    //public getters for private class variables
    public char getCardFace() { return this.cardFace; }
	public char getCardSuit() { return this.cardSuit; }
    public int getCardPoints() { return this.cardPoints; }
    public int getCardID(){return this.cardID;}
}