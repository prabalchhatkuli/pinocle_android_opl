package com.stairway.pinocle_android_opl.model;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

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
}
