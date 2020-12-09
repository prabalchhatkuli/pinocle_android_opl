package com.stairway.pinocle_android_opl.model;

import com.stairway.pinocle_android_opl.model.Card;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    //class constants for the deck initalizations
    final char[] FACES = new char[]{ '9', 'J', 'Q', 'K', 'X', 'A' };
    final char[] SUITS = new char[]{'H','S','C','D'};

    //list to store all current cards in the deck
    private ArrayList<Card> listOfCards;

    /**
     default constructor for a deck
     */
    public Deck()
    {
        //counter for card ID
        int cardID = 1;

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

    /**
     * numberOfCards function
     * @return an integer, which contains the number of remaining cards in the list
     */
    public int numberOfCards() { return listOfCards.size(); }

    /**
     shuffleDeck, randomly shuffles the list of cards
     */
    public void shuffleDeck()
    {
        //shuffle the list of cards
        Collections.shuffle(listOfCards);
    }

    /**
     displayDeck, displays an ascii outout in the system log, used for debugging purposes
     */
    public void displayDeck()
    {
        for (int i=0; i<listOfCards.size(); i++)
        {
            System.out.print( listOfCards.get(i).getCardFace());
            System.out.print( listOfCards.get(i).getCardSuit());
            System.out.print( " | ");
        }
    }

    /**
     dealCard, deals a card to a user
     @return a card object, the topmost card in the deck.
     */
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

    /**
     * getDeckSize function
     * @return an integer, which contains the number of remaining cards in the list
     */
    public  int getDeckSize()
    {
        return listOfCards.size();
    }


    /**
     getCards, get the entire deck
     @return an arraylist of cards, which is the entire list of cards.
     */
    public ArrayList<Card> getCards() {
        return listOfCards;
    }

    /**
     setDeck, sets a deck based on a list
     @param vectorOfCards, an arraylist contains a list of cards to set to the deck.
     */
    public void setDeck(ArrayList<Card> vectorOfCards) {
        //clear the existing list
        listOfCards.clear();
        //add to the empty list
        listOfCards.addAll(vectorOfCards);
    }
}
