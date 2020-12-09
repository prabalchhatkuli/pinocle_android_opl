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
    //member variables
    //list of players for the game
    private ArrayList<Player> listOfPlayers;
    //number of rounds played for this game
    private int numRounds;
    //winner of last round
    private int winnerLastRound;
    //the current round object
    private Round currentRound;

    /**
     Game class constructor
     @param i, an integer which contains who will start the round
     */
    public Game(int i)
    {
        listOfPlayers = new ArrayList<Player>();
        listOfPlayers.add(new Human());
        listOfPlayers.add(new Computer());

        numRounds =1;
        winnerLastRound = i;
    }

    /**
     getListOfPlayers, gets the list of players
     @return an arraylist of player objects
     */
    public ArrayList<Player> getListOfPlayers()
    {
        return listOfPlayers;
    }

    /**
     getDeck, get the remaining cards in the deck
     @return an arraylist of card objects
     */
    public ArrayList<Card> getDeck()
    {
        return currentRound.getDeck();
    }

    /**
     startGame, start a new round in the game
     */
    public void startGame()
    {
        //create a new round
        currentRound = new Round(winnerLastRound);
        //set it to current round
        currentRound.startRound(listOfPlayers, winnerLastRound);
    }

    /**
     getTrumpCard, get the trump card for the current round
     @return a card object, which contains the trump Card
     */
    public Card getTrumpCard() {
        return currentRound.getTrumpCard();
    }

    /**
     getNextPlayer, get next player turn in the game
     @return an integer, which is the index in the listOfPlayers
     */
    public int getNextPlayer() {
        return currentRound.getNextPlayer();
    }

    /**
     getMoveOrMeld, get whether the current player is to make a move or a meld
     @return a boolean, which contains true if move turn, false otherwise
     */
    public boolean getMoveOrMeld(){
        return currentRound.getMoveOrMeld();
    }

    /**
     processMove, processes a move made by the human player
     */
    public void processMove() {
        currentRound.processMoves();
    }

    /**
     play, function to make a move for a player
     @param cardID, an integer which contains the cardID for the card that was chosen
     @param listOfLogs, an arraylist of string used to display logs
     */
    public void play(Integer cardID, ArrayList<String> listOfLogs) {
        currentRound.play(cardID, listOfLogs);
    }

    /**
     makeMeld, function to make a meld for a player
     @param selectedCard, an arraylist of integers which contains the cardID for the cards that were chosen
     @param listOfLogs, an arraylist of string used to display logs
     */
    public void makeMeld(ArrayList<Integer> selectedCard, ArrayList<String> listOfLogs) {
        currentRound.makeMeld(selectedCard,listOfLogs);
    }

    /**
     drawRoundCards, draw one card for each player
     */
    public void drawRoundCards() {
        currentRound.drawCards();
    }

    /**
     getMeldHelp, function to get help for a meld
     @param listOfLogs, an ArrayList of string used to display logs
     */
    public void getMeldHelp(ArrayList<String> listOfLogs) {
        currentRound.getPlayerMeldHelp(listOfLogs);
    }

    /**
     getMeldHelp, function to get the move made by the player
     @param listOfLogs, an ArrayList of string used to display logs
     */
    public void getPlayerMove(ArrayList<String> listOfLogs) {
        currentRound.getPlayerMove(listOfLogs);
    }

    /**
     saveState, function to save the game to a file in the external storgae directory
     */
    public void saveState() {
        try {
            //check if media is mounted for use
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                //variable declaration and initialization
                String filename = new String();
                Integer saveFileCount = 0;
                File saveFile;

                //find filename which is not already taken
                while(true)
                {
                    //filename will be in the form of saveSlot**.txt
                    filename = "saveSlot"+Integer.toString(saveFileCount)+".txt";
                    String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pinochlesave/"+filename;
                    saveFile = new File(fileDir);
                    if(!saveFile.exists()){
                        break;
                    }
                    else
                    {
                        saveFileCount+=1;
                    }
                }

                //output object
                OutputStream output = new FileOutputStream(saveFile);

                //write output using the stream object
                StringBuilder saveFileContent = new StringBuilder();

                //building the string outpout
                saveFileContent.append("Round").append(Integer.toString(numRounds)).append("\n");

                //loopinf the list of player to get each player's information
                for(int i=0; i<listOfPlayers.size(); i++)
                {
                    if(1==i)
                    {
                        saveFileContent.append("Computer:").append("\n");
                    }
                    else
                    {
                        saveFileContent.append("Human:").append("\n");
                    }

                    //player scores
                    StringBuilder playerScoreString = new StringBuilder();
                    playerScoreString.append("\t").append("Score:").append(listOfPlayers.get(i).getPlayerGameScore()).append(" / ")
                            .append(listOfPlayers.get(i).getPlayerGameScore());

                    saveFileContent.append(playerScoreString).append("\n");

                    //player hand cards
                    StringBuilder handCardsString = new StringBuilder();
                    handCardsString.append("\t").append("Hand: ");
                    for(Card card: listOfPlayers.get(i).getPlayerHand())
                    {
                        handCardsString.append(card.getCardFace()).append(card.getCardSuit()).append(" ");
                    }

                    saveFileContent.append(handCardsString).append("\n");

                    //player capture cards
                    StringBuilder playedCardsString = new StringBuilder();
                    playedCardsString.append("\t").append("Capture Pile: ");
                    for(Card card: listOfPlayers.get(i).getCapturePile())
                    {
                        playedCardsString.append(card.getCardFace()).append(card.getCardSuit()).append(" ");
                    }

                    saveFileContent.append(playedCardsString).append("\n");

                    //player meld cards
                    //get meld string

                    StringBuilder MeldCardsString = new StringBuilder();
                    MeldCardsString.append("\t").append("Melds: ");
                    MeldCardsString.append(listOfPlayers.get(i).getMeldString());

                    saveFileContent.append(MeldCardsString).append("\n");
                }

                //trump card
                StringBuilder trumpCardString = new StringBuilder();
                trumpCardString.append("Trump Card: ");
                trumpCardString.append(currentRound.getTrumpCard().getCardFace()).append(currentRound.getTrumpCard().getCardSuit()).append(" ");

                saveFileContent.append(trumpCardString).append("\n");

                //stock cards
                StringBuilder deckCardsString = new StringBuilder();
                deckCardsString.append("Stock: ");
                for(Card card: currentRound.getDeck())
                {
                    deckCardsString.append(card.getCardFace()).append(card.getCardSuit()).append(" ");
                }

                saveFileContent.append(deckCardsString).append("\n");

                //next turn
                output.write(saveFileContent.toString().getBytes());

                output.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    /**
     loadGame, function to load state from a file
     @param filename, a string which contains the name of the file to be loaded
     */
    public void loadGame(String filename) {
        try {
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pinochlesave/"+filename;
                InputStream input = new FileInputStream(path);

                //creating a new round object
                this.currentRound = new Round(winnerLastRound);
                this.currentRound.startRound(listOfPlayers, winnerLastRound);

                //update round and player informations after deserializing
                deserialize(input);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     deserialize, function to set various states in the game after reading from a file
     @param input, a inputStream object which contains the stream object for file input
     */
    private void deserialize(InputStream input) {

        //variable declarations
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        int playerNumber = 0;
        int totalNumberOfPlayedCards = 0;

        ArrayList<ArrayList<ArrayList<String>>>meldsForPlayers = new ArrayList<>();

        //reading one line at a time
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                line = line.trim();
                if (0 == line.length())
                    continue;

                //split the line based on spaces
                String[] temp_vector = line.split(" ");
                System.out.println(temp_vector);

                //round number
                if (temp_vector[0].equals("Round:"))
                {
                    //the last element is the round number
                    numRounds = Integer.parseInt(temp_vector[temp_vector.length-1]);

                    continue;
                }

                //card collection
                if (temp_vector[0].equals("Human:") || temp_vector[0].equals("Computer:"))
                {
                    //evaluate the player
                    if (temp_vector[0].equals("Human:"))
                    { playerNumber = 0;}
                    else{ playerNumber = 1;}

                    continue;
                }

                //score information
                if (temp_vector[0].equals("Score:"))
                {
                    //for the score line vector
                    //1  is Game Score, 3 is Round Score
                    listOfPlayers.get(playerNumber).setPlayerScores(Integer.parseInt(temp_vector[1]), Integer.parseInt(temp_vector[3]));
                    continue;
                }

                //hand cards for the player
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

                //capture pile for the player
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


                //meld pile for the player
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

                        //temporary variables for meld determinations
                        ArrayList<ArrayList<String>> vectorSingleMelds = new ArrayList<>();
                        ArrayList<String> singleMeld = new ArrayList<>();

                        //find each meld by searching for comma
                        for (int i = 0; i < slicedVectorOfCards.length; i++)
                        {
                            //check for the comma to separate melds, or the end of list of cards
                            if ((3 == slicedVectorOfCards[i].length() && ',' == slicedVectorOfCards[i].charAt(2)) || (i == (slicedVectorOfCards.length-1)))
                            {
                                //if there is a comma
                                if((3 == slicedVectorOfCards[i].length() && ',' == slicedVectorOfCards[i].charAt(2))) {
                                    slicedVectorOfCards[i] = slicedVectorOfCards[i].substring(0,2);
                                }
                                //otherwise
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

                //setting trump card
                if (temp_vector[0].equals("Trump"))
                {
                    //the card is in index 2, beacuse index 1 is "card:"
                    currentRound.setTrumpCard(temp_vector[2]);
                    continue;
                }

                //settting stock cards
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

                //setting the net player
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
            listOfPlayers.get((counter==0)?1:0).setMeldPile(meldsForPlayers.get(counter), currentRound.getTrumpCard());
            counter--;
        }

    }

    /**
     getIfTurnOfLead,  find whether it is the turn of the lead player
     @return a boolean, true if it is a leadplayer, false otherwise
     */
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

    /**
     getRoundNumber,  get the number of rounds that has been played for the game
     @return an integer, which is the value of numRounds member variable
     */
    public int getRoundNumber() {
        return numRounds;
    }

    /**
     resetGame,  reset all of the games information for a new round
     */
    public void resetGame() {
        //get next player who will start the next round
        winnerLastRound = getNextPlayer();

        //increment numbe rof rounds
        numRounds++;

        //clear info for users
        int counter = 0;

        while(counter<=1)
        {
            listOfPlayers.get(counter).clearAllInfo();
            counter++;
        }

        //create a new round
        currentRound = new Round(winnerLastRound);
        currentRound.startRound(listOfPlayers, winnerLastRound);

    }
}
