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

        /*
        onclick listeners for the main activity
         */
        //new game button

        Button newGameButton =  findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder prompt = new AlertDialog.Builder(MainActivity.this);
                //title
                prompt.setTitle("A Coin has been tossed");

                final String[] choice = {"Heads", "Tails"};


                // display the toss dialogue and start the main activity
                prompt.setItems(choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ListView select = ((AlertDialog)dialogInterface).getListView();
                        String selected = (String) select.getAdapter().getItem(i);

                        // generate a random integer for the coin toss
                        Random rand = new Random();
                        int temp = rand.nextInt(10000);
                        int coinToss = temp % 2;

                        //intent for a new activity
                        Intent it = new Intent(MainActivity.this, GameActivity.class);

                        if ((selected.equals("Heads") && coinToss == 0) || (selected.equals("Tails") && coinToss == 1)) {
                            it.putExtra("turn", "human");
                        }
                        else {
                            it.putExtra("turn", "computer");
                        }
                        it.putExtra("type", "new");
                        //start the activity
                        startActivity(it);
                    }
                });
                prompt.show();

            }
        });


        //load game button

        Button loadButton = findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder prompt = new AlertDialog.Builder(MainActivity.this);
                //title
                prompt.setTitle("Select Load Game file:");

                //find all files in the external directory
                String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pinochlesave";
                File directory = new File(fileDir);
                System.out.println(Environment.getExternalStorageState());
                System.out.println(directory);
                //copy the file objects in an array
                final File[] files = directory.listFiles();

                List<String> fileNameList = new ArrayList<>(files.length);

                //find the names of the file and add them to the list of strings
                for (File file : files ){
                    String name = file.getName();
                    fileNameList.add(name);
                }

                String [] actualFileNames = new String[fileNameList.size()];
                actualFileNames = fileNameList.toArray(actualFileNames);

                // show the load file list and start the main activity
                prompt.setItems(actualFileNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int these) {
                        ListView select = ((AlertDialog)dialog).getListView();
                        String fileName = (String) select.getAdapter().getItem(these);
                        Intent it = new Intent(MainActivity.this, GameActivity.class);
                        it.putExtra("type", "load");
                        it.putExtra("file", fileName);

                        startActivity(it);
                    }
                });
                prompt.show();
            }
        });

    }


}
