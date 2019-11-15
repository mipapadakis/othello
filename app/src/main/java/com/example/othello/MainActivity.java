package com.example.othello;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initializeMainButtons();
    }

    private void initializeMainButtons(){
        findViewById(R.id.playBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),ModeActivity.class);
                startActivity(startIntent);
            }
        });

        findViewById(R.id.scoresBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),InfoActivity.class);
                startIntent.putExtra(InfoActivity.KEY_SCORES, true);
                startActivity(startIntent);
            }
        });

        findViewById(R.id.helpBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),InfoActivity.class);
                startIntent.putExtra(InfoActivity.KEY_HELP, true);
                startActivity(startIntent);
            }
        });
    }

    public void onBackPressed(){
        new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.exit))
            .setMessage(getResources().getString(R.string.exit_msg))
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    //TODO: save data?
                    //EXIT APPLICATION
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }).create().show();
    }
}
