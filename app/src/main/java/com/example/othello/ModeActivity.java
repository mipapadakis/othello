package com.example.othello;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_mode_layout);
        setTitle(getString(R.string.choose_mode)); //ActionBar title
        initializeModeButtons();
    }


    private void initializeModeButtons(){
        findViewById(R.id.aiBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),BoardActivity.class);
                startIntent.putExtra(BoardActivity.KEY_MODE, BoardActivity.MODE_VS_AI_EASY);
                startActivity(startIntent);
            }
        });

        findViewById(R.id.onlineBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("NOTE","onlineBtn");
                createNoteIntent("PvP Online", "Not implemented yet");
            }
        });

        findViewById(R.id.two_usersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createNoteIntent("User vs. User", "Not implemented yet");
                Intent startIntent = new Intent(getApplicationContext(),BoardActivity.class);
                startIntent.putExtra(BoardActivity.KEY_MODE, BoardActivity.MODE_TWO_USERS);
                startActivity(startIntent);
            }
        });
    }

    protected void createNoteIntent(String title, String text){
        ArrayList<String> list = new ArrayList<>();
        list.add(title);
        list.add(text);
        Intent startIntent = new Intent(getApplicationContext(),InfoActivity.class);
        startIntent.putExtra(InfoActivity.KEY_NOTE, list);
        startActivity(startIntent);
    }
}
