package com.stairway.pinocle_android_opl.model;

import com.stairway.pinocle_android_opl.model.Card;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    //class variables
    final char[] FACES = new char[]{ '9', 'J', 'Q', 'K', 'X', 'A' };
    final char[] SUITS = new char[]{'H','S','C','D'};

    private ArrayList<Card> listOfCards;

    public Deck()
    {
        //counter for card ID
        int cardID = 0;

        listOfCards = new ArrayList<Card>();
        //initializing the list of cards
        for (int i=0; i<2; i++)
        {
            //loop for faces
            for (int j = 0; j < FACES.length; j++)
            {
                //loop for suits
                for (int k = 0; k < SUITS.length; k++)
                {
                    listOfCards.add(new Card(FACES[j], SUITS[k], cardID));
                    cardID+=1;
                }
            }
        }
    }

    public int numberOfCards() { return listOfCards.size(); }

    public void shuffleDeck()
    {
        //shuffle the list of cards
        Collections.shuffle(listOfCards);
    }

    public void displayDeck()
    {
        for (int i=0; i<listOfCards.size(); i++)
        {
            System.out.print( listOfCards.get(i).getCardFace());
            System.out.print( listOfCards.get(i).getCardSuit());
            System.out.print( " | ");
        }
    }

    public Card dealCard()
    {
        if (0 == listOfCards.size())
        {
            System.out.println( "No more cards in stock to deal");
        }

        //the topmost card in the deck is in the index 0
        Card topCard = listOfCards.get(0);

        listOfCards.remove(0);

        //return pointer to the card
        return topCard;
    }

    public  int getDeckSize()
    {
        return listOfCards.size();
    }


    public ArrayList<Card> getCards() {
        return listOfCards;
    }
}
