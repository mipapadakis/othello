package com.example.othello;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;

public class ModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_mode_layout);
        setTitle(getString(R.string.choose_mode)); //ActionBar title
        initializeModeButtons();

        //////////////Online PvP not implemented yet => don't show the button///////////////////////
        findViewById(R.id.onlineBtn).setVisibility(View.GONE);
    }


    private void initializeModeButtons(){
        findViewById(R.id.aiBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent startIntent = new Intent(getApplicationContext(),BoardActivity.class);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ModeActivity.this);
                mBuilder.setTitle(getString(R.string.choose_difficulty));
                mBuilder.setSingleChoiceItems(getResources().getStringArray(R.array.difficulty), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            startIntent.putExtra(BoardActivity.KEY_MODE, BoardActivity.EASY_AI);
                            startActivity(startIntent);
                            dialogInterface.dismiss();
                        }
                        else if(i==1){
                            startIntent.putExtra(BoardActivity.KEY_MODE, BoardActivity.MEDIUM_AI);
                            startActivity(startIntent);
                            dialogInterface.dismiss();
                        }
                        else if(i==2){
                            startIntent.putExtra(BoardActivity.KEY_MODE, BoardActivity.HARD_AI);
                            startActivity(startIntent);
                            dialogInterface.dismiss();
                        }
                        else{
                            startIntent.putExtra(BoardActivity.KEY_MODE, BoardActivity.EXPERT_AI);
                            startActivity(startIntent);
                            dialogInterface.dismiss();
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        findViewById(R.id.onlineBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<>();
                list.add("PvP Online");
                list.add("Not implemented yet");
                Intent startIntent = new Intent(getApplicationContext(),InfoActivity.class);
                startIntent.putExtra(InfoActivity.KEY_NOTE, list);
                startActivity(startIntent);
            }
        });

        findViewById(R.id.two_usersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),BoardActivity.class);
                startIntent.putExtra(BoardActivity.KEY_MODE, BoardActivity.MODE_TWO_USERS);
                startActivity(startIntent);
            }
        });
    }
}
