package com.stairway.pinocle_android_opl.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stairway.pinocle_android_opl.R;
import com.stairway.pinocle_android_opl.model.Card;
import com.stairway.pinocle_android_opl.model.Game;
import com.stairway.pinocle_android_opl.model.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class GameActivity extends AppCompatActivity {

    private int cardname;
    private Game game;
    private ArrayList<Integer> selectedCard;
    private int numberOfCards;
    private boolean moveOrMeld;
    private boolean isChasePlayer;
    private int playerTurn;
    ArrayList<String> listOfLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent =  getIntent();
        String gameType = intent.getExtras().getString("type");
        String startPlayer;
        String filename;
        listOfLogs = new ArrayList<>();



        moveOrMeld = true;
        isChasePlayer =false;

        numberOfCards = 1;
        playerTurn = 0;


        if(gameType.equals("new")) {
            startPlayer = intent.getExtras().getString("turn");
            game = new Game((startPlayer.equals("human"))?0:1);
            if(startPlayer.equals("human"))
            {
                listOfLogs.add("Yes, You won the toss.");
            }
            else
            {
                listOfLogs.add("No, You lost the toss.");
            }
            game.startGame();
        }
        else
        {
            filename = intent.getExtras().getString("file");
            game = new Game(0);
            game.loadGame(filename);
        }


        selectedCard = new ArrayList<>();

        refreshView();


        //-------------------------Button onclick listeners------------------------------
        //make move button
        Button moveButton = findViewById(R.id.moveButton);

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game.getListOfPlayers().get(game.getNextPlayer()).getPlayerName().equals("Human") && selectedCard.isEmpty()) {
                    makeToast("You have not selected a card yet.");
                }
                else if(game.getListOfPlayers().get(game.getNextPlayer()).getPlayerName().equals("Computer"))
                {
                    game.play(1111);
                    refreshView();
                }
                else {
                    game.play(selectedCard.get(0));
                    //refresh
                    refreshView();
                }
            }
        });

        //make meld Button
        Button meldButton = findViewById(R.id.meldButton);
        meldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game.getListOfPlayers().get(game.getNextPlayer()).getPlayerName().equals("Human") && selectedCard.isEmpty()) {
                    makeToast("You have not selected a card yet.");
                }
                else if(game.getListOfPlayers().get(game.getNextPlayer()).getPlayerName().equals("Computer"))
                {
                    game.makeMeld(new ArrayList<Integer>(), listOfLogs);
                    refreshView();
                }
                else {
                    game.makeMeld(selectedCard, listOfLogs);

                    //refresh
                    refreshView();
                }
            }
        });

        //draw button
        Button drawButton = findViewById(R.id.drawButton);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.drawRoundCards();
                refreshView();
            }
        });

        //Get help Button
        Button helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(moveOrMeld)
                {
                    game.getPlayerMove(listOfLogs);
                }
                else
                {
                    game.getMeldHelp(listOfLogs);
                }
                refreshView();
            }
        });

        //save game button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.saveState();
            }
        });


        //quit game button
        Button quitButton = findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.saveState();
            }
        });
    }

    public void refreshView()
    {
        //updating the hand of player
        LinearLayout humanHand = findViewById(R.id.humanHand);
        LinearLayout computerHand = findViewById(R.id.computerHand);
        LinearLayout humanMeld = findViewById(R.id.humanMeld);
        LinearLayout computerMeld = findViewById(R.id.computerMeld);
        LinearLayout humanCapture = findViewById(R.id.humanCapture);
        LinearLayout computerCapture = findViewById(R.id.computerCapture);
        LinearLayout deskLayout = findViewById(R.id.deskLayout);
        Button meldButton = findViewById(R.id.meldButton);
        Button drawButton = findViewById(R.id.drawButton);
        Button moveButton = findViewById(R.id.moveButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button helpButton = findViewById(R.id.helpButton);
        TextView commentView = findViewById(R.id.commentTextView);

        //clear layouts
        humanHand.removeAllViews();
        humanCapture.removeAllViews();
        humanMeld.removeAllViews();
        computerHand.removeAllViews();
        computerMeld.removeAllViews();
        computerCapture.removeAllViews();
        deskLayout.removeAllViews();

        ArrayList<Player> listOfPlayer = game.getListOfPlayers();

        //get the next player and the player info
        playerTurn = game.getNextPlayer();
        moveOrMeld = game.getMoveOrMeld();

        //move and meld display buttons.
        if(moveOrMeld)
        {
            numberOfCards=1;
            meldButton.setVisibility(View.GONE);
            drawButton.setVisibility(View.GONE);
            moveButton.setVisibility(View.VISIBLE);
        }
        else
        {
            numberOfCards=5;
            meldButton.setVisibility(View.VISIBLE);
            drawButton.setVisibility(View.VISIBLE);
            moveButton.setVisibility(View.GONE);
        }

        //for lead player display the save button
        Boolean isTurnOfLead = game.getIfTurnOfLead();
        if(isTurnOfLead)
        {
            saveButton.setVisibility(View.VISIBLE);
        }
        else
        {
            saveButton.setVisibility(View.GONE);
        }

        //clear view variables
        selectedCard.clear();

        for(Player each : listOfPlayer)
        {
            if(each.getPlayerName() == "Human")
            {
                addCardsToClickableView(each.getPlayerHand(), humanHand);
                //update meld cards
                addCardsToMeldView(each, humanMeld);
                System.out.println("Size of meld of human is");
                System.out.println(each.getMeldPile().size());
                addCardsToView(each.getCapturePile(), humanCapture);

            }
            else
            {
                addCardsToClickableView(each.getPlayerHand(), computerHand);
                addCardsToMeldView(each, computerMeld);
                System.out.println("Size of meld of computer is");
                System.out.println(each.getMeldPile().size());
                addCardsToView(each.getCapturePile(), computerCapture);
            }

            //for the desk
            addCardsToView(each.getPlayedCards(), deskLayout);
            if(each.getPlayedCards().size()!=0)
            {
                listOfLogs.add(each.getPlayerName()+" chose "+ each.getPlayedCards().get(0).getCardFace()+each.getPlayedCards().get(0).getCardSuit());
            }
        }

        //updating the deck of cards
        LinearLayout deckLayout = findViewById(R.id.deckLayout);
        deckLayout.removeAllViews();
        addCardsToView( game.getDeck(), deckLayout);

        //getting the trumpCard and the nextPlayer
        LinearLayout trumpLayout = findViewById(R.id.trumpLayout);
        trumpLayout.removeAllViews();
        addCardsToView(new ArrayList<Card>(){{add(game.getTrumpCard());}}, trumpLayout);

        //next player name
        TextView nextPlayerview = findViewById(R.id.nextPlayerTextView);
        nextPlayerview.setText("Next Player: "+game.getListOfPlayers().get(game.getNextPlayer()).getPlayerName());

        if(game.getListOfPlayers().get(game.getNextPlayer()).getPlayerName().equals("Computer"))
        {
            helpButton.setVisibility(View.GONE);
        }
        else
        {
            helpButton.setVisibility(View.VISIBLE);
        }

        //set the scores for each player
        TextView humanScore = findViewById(R.id.humanScoreView);
        TextView computerScore = findViewById(R.id.computerScoreView);
        TextView roundNum = findViewById(R.id.roundView);

        if(listOfLogs.size()>0)
            commentView.setText("Comment: " +listOfLogs.get(listOfLogs.size()-1));
        roundNum.setText("Round:"+Integer.toString(game.getRoundNumber()));
        humanScore.setText(Integer.toString(game.getListOfPlayers().get(0).getPlayerGameScore()) +"/"+Integer.toString(game.getListOfPlayers().get(0).getPlayerRoundScore()) );
        computerScore.setText(Integer.toString(game.getListOfPlayers().get(1).getPlayerGameScore()) +"/"+Integer.toString(game.getListOfPlayers().get(1).getPlayerRoundScore()) );

        //end of round prompt
        if((listOfPlayer.get(0).getCapturePile().size()+listOfPlayer.get(1).getCapturePile().size() == 48))
        {
            String[] options = {"Yes, start another.", "No, quit the game."};

            //display prompt
            final AlertDialog.Builder prompt = new AlertDialog.Builder(GameActivity.this);

            prompt.setTitle("End of Round: want to start another round?");

            prompt.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0)
                    {
                        //reset game and update player game/round scores
                        game.resetGame();
                        listOfLogs.clear();
                        dialog.dismiss();
                        refreshView();
                    }
                }
            });

            prompt.show();
        }

    }

    @SuppressLint("ResourceType")
    private void addCardsToMeldView(Player each, LinearLayout meldView) {
        Map<Card, ArrayList<Integer>> mp = each.getMeldToCardMap();
        System.out.println("Size of meld to card map of human is");
        System.out.println(each.getMeldToCardMap().size());
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry im = (Map.Entry)it.next();

            ArrayList<ArrayList<Card>> tempVectorOfAMeld= (ArrayList<ArrayList<Card>>) im.getValue();

            if (0 == tempVectorOfAMeld.size()) {
                continue;
            }

            Integer currentMeld = (Integer) im.getKey();

            for ( int i = 0; i < tempVectorOfAMeld.size(); i++)
            {
                final ArrayList<Card> vectorWithinVector = tempVectorOfAMeld.get(i);

                for ( int j = 0; j < vectorWithinVector.size(); j++)
                {
                    //append the card to the output string
                    //vectorWithinVector[j] is the card
                    String meldString = new String();
                    meldString += vectorWithinVector.get(j).getCardFace();
                    meldString += vectorWithinVector.get(j).getCardSuit();

                    final int index = j;
                    addCardsToClickableView(new ArrayList<Card>(){{add(vectorWithinVector.get(index));}}, meldView);
                    //check if an asterisk is required
                    //flag to see if the card was used in another active meld
                    Boolean isFound = false;

                    //go to the card index in card to meld map
                    ArrayList<Integer> meldsForACard = (ArrayList<Integer>) each.getCardToMeldMap().get(vectorWithinVector.get(j));

                    //for each meld mentioned for the card
                    for ( int k = 0; k < meldsForACard.size(); k++)
                    {
                        //go to the meld to see if another meld with the card is still active
                        if (currentMeld == meldsForACard.get(k)) {
                            continue;
                        } else
                        {
                            //collection of vectors for this meld
                            ArrayList<ArrayList<Card>> tempCheckMeldCollection = (ArrayList<ArrayList<Card>>) each.getMeldToCardMap().get(meldsForACard.get(k));

                            //for the vector of melds hence received
                            for ( int l = 0; l < tempCheckMeldCollection.size(); l++)
                            {
                                //if the card is found an asterisk is required, break off the program, go to the next card
                                if (tempCheckMeldCollection.get(l).contains(vectorWithinVector.get(j)))
                                {
                                    System.out.println("Star found");
                                    meldString += '*';
                                    final ImageView starImage = new ImageView(this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
                                    params.setMargins(15, 0, -30, 0);
                                    starImage.setLayoutParams(params);
                                    Context context = meldView.getContext();
                                    int id = context.getResources().getIdentifier("star", "drawable", context.getPackageName());

                                    starImage.setImageResource(id);

                                    starImage.setId(i);
                                    starImage.setClickable(true);

                                    meldView.addView(starImage);

                                    isFound = true;
                                    break;
                                }
                            }
                            if (isFound)
                                break;
                        }
                    }
                }
                //line separating melds
                final ImageView lineImage = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 190);
                params.setMargins(15, 0, -30, 0);
                lineImage.setLayoutParams(params);
                Context context = meldView.getContext();
                int id = context.getResources().getIdentifier("line", "drawable", context.getPackageName());

                lineImage.setImageResource(id);

                lineImage.setId(22);
                lineImage.setClickable(true);

                meldView.addView(lineImage);
            }



            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("After processing Size of meld to card map of human is");
        System.out.println(each.getMeldToCardMap().size());
    }

    public void makeToast(String tst) {
        Toast.makeText(getApplicationContext(), tst, Toast.LENGTH_SHORT).show();
    }

    public void addCardsToView( ArrayList<Card> cardsToAdd, LinearLayout viewToAdd)
    {
        for(final Card singleCard: cardsToAdd)
        {
            final ImageView cardImage = new ImageView(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 190);
            params.setMargins(15, 0, -30, 0);
            cardImage.setLayoutParams(params);

            Context context = viewToAdd.getContext();
            String cardName = Character.toString(singleCard.getCardSuit()).toLowerCase() +
                    Character.toString(singleCard.getCardFace()).toLowerCase();
            int id = context.getResources().getIdentifier(cardName, "drawable", context.getPackageName());
            cardImage.setImageResource(id);

            cardImage.setId(singleCard.getCardID());
            cardImage.setClickable(true);

            viewToAdd.addView(cardImage);
        }
    }

    public void addCardsToClickableView( ArrayList<Card> cardsToAdd, LinearLayout viewToAdd)
    {

       for(final Card singleCard: cardsToAdd)
       {
           final ImageView cardImage = new ImageView(this);

           LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 190);
           params.setMargins(15, 0, -30, 0);
           cardImage.setLayoutParams(params);

           Context context = viewToAdd.getContext();
           String cardName = Character.toString(singleCard.getCardSuit()).toLowerCase() +
                   Character.toString(singleCard.getCardFace()).toLowerCase();
           int id = context.getResources().getIdentifier(cardName, "drawable", context.getPackageName());
           cardImage.setImageResource(id);

           cardImage.setId(singleCard.getCardID());
           cardImage.setClickable(true);
           cardImage.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   //clearCardsBackground();

                   if(selectedCard.contains(cardImage.getId())) {
                       cardImage.setBackgroundColor(Color.TRANSPARENT);
                       selectedCard.remove(new Integer(Integer.valueOf(cardImage.getId())));
                   }
                   else if(selectedCard.size() == numberOfCards)
                   {
                       //enough cards have been selected
                   }
                   else {
                       cardImage.setBackgroundColor(Color.GREEN);
                       selectedCard.add(singleCard.getCardID());
                   }
                   int selectedHandCard = cardImage.getId();
               }
           });

           viewToAdd.addView(cardImage);
       }
    }
}
