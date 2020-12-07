package com.stairway.pinocle_android_opl.model;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Game {
    private ArrayList<Player> listOfPlayers;
    private int numRounds;
    private int winnerLastRound;
    private Round currentRound;

    public Game(int i)
    {
        listOfPlayers = new ArrayList<Player>();
        listOfPlayers.add(new Human());
        listOfPlayers.add(new Computer());

        numRounds =1;
        winnerLastRound = i;
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
        currentRound = new Round(winnerLastRound);
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

    public void drawRoundCards() {
        currentRound.drawCards();
    }

    public void decideMeld() {
        currentRound.letPlayerMakeMeld();
    }

    public void getPlayerMove() {
        currentRound.getPlayerMove();
    }

    public void saveState() {
        String info = currentRound.serialize();
        try {
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                String fileName = "testsavefile.txt";
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName);
                OutputStream output = new FileOutputStream(file);
                output.write(info.getBytes());
                output.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public void loadGame(String filename) {
        try {
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pinochlesave/"+filename;
                InputStream input = new FileInputStream(path);

                this.currentRound = new Round(winnerLastRound);
                this.currentRound.startRound(listOfPlayers, winnerLastRound);

                deserialize(input);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deserialize(InputStream input) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        int playerNumber = 0;
        int totalNumberOfPlayedCards = 0;

        ArrayList<ArrayList<ArrayList<String>>>meldsForPlayers = new ArrayList<>();

        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                line = line.trim();
                if (0 == line.length())
                    continue;


                //split the line based on spaces
                String[] temp_vector = line.split(" ");
                System.out.println(temp_vector);

                if (temp_vector[0].equals("Round:"))
                {
                    //the last element is the round number
                    numRounds = Integer.parseInt(temp_vector[temp_vector.length-1]);

                    continue;
                }

                if (temp_vector[0].equals("Human:") || temp_vector[0].equals("Computer:"))
                {
                    //evaluate the player
                    if (temp_vector[0].equals("Human:"))
                    { playerNumber = 0;}
                    else{ playerNumber = 1;}

                    continue;
                }

                if (temp_vector[0].equals("Score:"))
                {
                    //for the score line vector
                    //1  is Game Score, 3 is Round Score
                    listOfPlayers.get(playerNumber).setPlayerScores(Integer.parseInt(temp_vector[1]), Integer.parseInt(temp_vector[3]));
                    continue;
                }

                if (temp_vector[0].equals("Hand:"))
                {
                    //break the vector and send it to the player to create a hand
                    if (temp_vector.length > 1)
                    {
                        String[] slicedVectorOfCards = new String[(temp_vector.length-1)];
                        for(int i=1; i<temp_vector.length; i++)
                        {
                            slicedVectorOfCards[i-1] = temp_vector[i];
                        }

                        listOfPlayers.get(playerNumber).setPlayerHand(slicedVectorOfCards);
                    }
                    continue;
                }

                if (temp_vector[0].equals("Capture"))
                {
                    //1 is "pile:"
                    //break the remaining and send it to the player to create capture pile
                    if (temp_vector.length > 2)
                    {
                        String[] slicedVectorOfCards = new String[(temp_vector.length-2)];
                        for(int i=2; i<temp_vector.length; i++)
                        {
                            slicedVectorOfCards[i-2] = temp_vector[i];
                        }

                        totalNumberOfPlayedCards += slicedVectorOfCards.length;
                        listOfPlayers.get(playerNumber).setCapturePile(slicedVectorOfCards);
                    }
                    continue;
                }

                if (temp_vector[0].equals("Melds:"))
                {
                    //break the remaining and send it to the player to create capture pile
                    if (temp_vector.length > 1)
                    {
                        String[] slicedVectorOfCards = new String[(temp_vector.length-1)];
                        for(int i=1; i<temp_vector.length; i++)
                        {
                            slicedVectorOfCards[i-1] = temp_vector[i];
                        }

                        ArrayList<ArrayList<String>> vectorSingleMelds = new ArrayList<>();
                        ArrayList<String> singleMeld = new ArrayList<>();

                        //find each meld by searching for comma
                        for (int i = 0; i < slicedVectorOfCards.length; i++)
                        {
                            //check for the comma to separate melds, or the end of list of cards
                            if ((3 == slicedVectorOfCards[i].length() && ',' == slicedVectorOfCards[i].charAt(2)) || (i == (slicedVectorOfCards.length-1)))
                            {
                                if((3 == slicedVectorOfCards[i].length() && ',' == slicedVectorOfCards[i].charAt(2))) {
                                    slicedVectorOfCards[i] = slicedVectorOfCards[i].substring(0,2);
                                }
                                singleMeld.add(slicedVectorOfCards[i]);
                                vectorSingleMelds.add(singleMeld);
                            }
                            else
                            {
                                Collections.addAll(singleMeld,slicedVectorOfCards[i]);
                            }
                        }

                        meldsForPlayers.add(vectorSingleMelds);
                    }
                    else
                    {
                        meldsForPlayers.add(new ArrayList<ArrayList<String>>());
                    }
                    continue;
                }

                if (temp_vector[0].equals("Trump"))
                {
                    //the card is in index 2, beacuse index 1 is "card:"
                    currentRound.setTrumpCard(temp_vector[2]);
                    continue;
                }

                if (temp_vector[0].equals("Stock:"))
                {
                    //send the card to the round to create the deck
                    if (temp_vector.length > 1)
                    {
                        String[] slicedVectorOfCards = new String[(temp_vector.length-1)];
                        for(int i=1; i<temp_vector.length; i++)
                        {
                            slicedVectorOfCards[i-1] = temp_vector[i];
                        }

                        currentRound.setRoundDeck(slicedVectorOfCards);
                    }
                    else
                    {
                        // if the deck is empty, send an empty vector
                        currentRound.setRoundDeck(new String[]{});
                    }
                    continue;
                }

                if (temp_vector[0].equals("Next"))
                {
                    //the player with next turn is in 2
                    //send it to round to set it to the nextTurn variable
                    winnerLastRound = (temp_vector[2].equals("Human")) ? 0 : 1;
                    currentRound.setNextTurn(temp_vector[2]);
                    continue;
                }
            }

            //trimming the line to see if it has any content other than whitespace
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //evaluate player melds
        int counter = 1;
        while (counter >= 0)
        {
            listOfPlayers.get(counter).setMeldPile(meldsForPlayers.get(counter), currentRound.getTrumpCard());
            counter--;
        }

    }

    public Boolean getIfTurnOfLead() {
        int playedCardsCount = 0;
        for(Player player: listOfPlayers)
        {
            playedCardsCount+=player.getPlayedCards().size();
        }

        if(playedCardsCount==0) {
            return true;
        } else {
            return false;
        }
    }

    public int getRoundNumber() {
        return numRounds;
    }
}
