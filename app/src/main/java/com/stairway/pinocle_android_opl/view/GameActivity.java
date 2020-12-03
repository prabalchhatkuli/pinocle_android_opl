package com.stairway.pinocle_android_opl.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private int cardname;
    private Game game;
    private ArrayList<Integer> selectedCard;
    private int numberOfCards;
    private boolean moveOrMeld;
    private boolean isChasePlayer;
    private int playerTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent =  getIntent();
        String gameType = intent.getExtras().getString("type");
        moveOrMeld = true;
        isChasePlayer =false;

        Context context = getApplicationContext();
        CharSequence text = gameType + " game started.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        numberOfCards = 1;
        playerTurn = 0;

        game = new Game();
        game.startGame();

        selectedCard = new ArrayList<>();

        refreshView();


        //-------------------------Button onclick listeners------------------------------
        //make move button
        Button moveButton = findViewById(R.id.moveButton);

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCard.isEmpty()) {
                    makeToast("You have not selected a card yet.");
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
                if (selectedCard.isEmpty()) {
                    makeToast("You have not selected a card yet.");
                }
                else {
                    game.makeMeld(selectedCard);

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
                    game.getPlayerMove();
                }
                else
                {
                    game.decideMeld();
                }
            }
        });

        //save game button
        Button saveButton = findViewById(R.id.saveButton);


        //quit game button
        Button quitButton = findViewById(R.id.quitButton);
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

        //clear view variables
        selectedCard.clear();

        for(Player each : listOfPlayer)
        {
            if(each.getPlayerName() == "Human")
            {
                addCardsToClickableView(each.getPlayerHand(), humanHand);
                addCardsToClickableView(each.getMeldPile(), humanMeld);
                addCardsToView(each.getCapturePile(), humanCapture);
            }
            else
            {
                addCardsToClickableView(each.getPlayerHand(), computerHand);
                addCardsToClickableView(each.getMeldPile(), computerMeld);
                addCardsToView(each.getCapturePile(), computerCapture);
            }

            //for the desk
            addCardsToView(each.getPlayedCards(), deskLayout);
        }

        //updating the deck of cards
        LinearLayout deckLayout = findViewById(R.id.deckLayout);
        deckLayout.removeAllViews();
        addCardsToView( game.getDeck(), deckLayout);

        //getting the trumpCard and the nextPlayer
        LinearLayout trumpLayout = findViewById(R.id.trumpLayout);
        addCardsToView(new ArrayList<Card>(){{add(game.getTrumpCard());}}, trumpLayout);

        //next player name
        TextView nextPlayerview = findViewById(R.id.nextPlayerTextView);
        nextPlayerview.setText("Next Player: "+game.getListOfPlayers().get(game.getNextPlayer()).getPlayerName());

        //set the scores for each player
        TextView humanScore = findViewById(R.id.humanScoreView);
        TextView computerScore = findViewById(R.id.computerScoreView);

        humanScore.setText(Integer.toString(game.getListOfPlayers().get(0).getPlayerGameScore()) +"/"+Integer.toString(game.getListOfPlayers().get(0).getPlayerRoundScore()) );
        computerScore.setText(Integer.toString(game.getListOfPlayers().get(1).getPlayerGameScore()) +"/"+Integer.toString(game.getListOfPlayers().get(1).getPlayerRoundScore()) );
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
