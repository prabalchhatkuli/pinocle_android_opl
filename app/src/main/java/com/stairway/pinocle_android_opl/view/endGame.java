package com.stairway.pinocle_android_opl.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stairway.pinocle_android_opl.R;

public class endGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Intent intent =  getIntent();
        String humanScoreString = intent.getExtras().getString("Human");
        String computerScoreString = intent.getExtras().getString("Computer");

        Integer humanScore = Integer.parseInt(humanScoreString);
        Integer computerScore = Integer.parseInt(computerScoreString);

        TextView mainText = findViewById(R.id.resultView);
        TextView compScore = findViewById(R.id.computerScoreView);
        TextView humScore = findViewById(R.id.humanScoreView);
        Button restartButton = findViewById(R.id.restartButton);

        if(humanScore>computerScore)
        {
            mainText.setText("YOU WON");
        }
        else if(humanScore==computerScore)
        {
            mainText.setText("Its a DRAW");
        }
        else {
            mainText.setText("YOU LOST");
        }

        compScore.setText("Computer: "+ computerScore);
        humScore.setText("Human: "+humanScore);

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(endGame.this, MainActivity.class);
                startActivity(it);
            }
        });
    }
}
