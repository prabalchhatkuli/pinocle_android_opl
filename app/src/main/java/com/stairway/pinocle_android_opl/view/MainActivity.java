package com.stairway.pinocle_android_opl.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.stairway.pinocle_android_opl.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newGameButton =  findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder guess = new AlertDialog.Builder(MainActivity.this);
                guess.setTitle("A Coin has been tossed");

                final String[] choice = {"Heads", "Tails"};
                final String result;

                // display the toss dialogue and start the main activity
                guess.setItems(choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ListView select = ((AlertDialog)dialogInterface).getListView();
                        String selected = (String) select.getAdapter().getItem(i);
                        final Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        intent.putExtra("state",1);

                        // generate a random integer
                        Random rand = new Random();
                        int temp = rand.nextInt(10000);
                        int tossResult = temp % 2;
                        Intent it = new Intent(MainActivity.this, GameActivity.class);
                        System.out.println("Toss result is "+tossResult);
                        if ((selected.equals("Heads") && tossResult == 0) || (selected.equals("Tails") && tossResult == 1)) {
                            intent.putExtra("turn", "human");
                            it.putExtra("turn", "human");
                        }
                        else {
                            intent.putExtra("turn","computer");
                            it.putExtra("turn", "computer");
                            System.out.println("Chosen is computer");
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        it.putExtra("type", "new");
                        //startActivity(intent);
                        startActivity(it);
                    }
                });
                guess.show();

            }
        });

        Button loadButton = findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder prompt = new AlertDialog.Builder(MainActivity.this);
                prompt.setTitle("Select Load Game file");


                String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pinochlesave";
                File directory = new File(fileDir);
                System.out.println(Environment.getExternalStorageState());
                System.out.println(directory);
                final File[] files = directory.listFiles();

                List<String> nameList = new ArrayList<>(files.length);

                for (File file : files ){
                    String name = file.getName();
                    //if (name.endsWith(".txt")){
                        nameList.add(name);
                    //}
                }

                String [] items = new String[nameList.size()];
                items = nameList.toArray(items);

                // show the load file list and start the main activity
                prompt.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int these) {
                        ListView select = ((AlertDialog)dialog).getListView();
                        String fileName = (String) select.getAdapter().getItem(these);
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        intent.putExtra("type", "load");
                        intent.putExtra("file", fileName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                prompt.show();
            }
        });

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
