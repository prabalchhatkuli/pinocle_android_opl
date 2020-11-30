package com.stairway.pinocle_android_opl.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stairway.pinocle_android_opl.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //new game
    public void onNewGame(View view) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("type", "new");
        startActivity(i);
    }

    //load game
    public void onLoadGame(View view) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("type", "load");
        startActivity(i);
    }

}
