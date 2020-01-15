package com.example.othello;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    protected static final String KEY_SCORES = "scores";
    protected static final String KEY_HELP = "help";
    protected static final String KEY_NOTE = "note";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide(); //Hide ActionBar
        TextView title = findViewById(R.id.info_titleTv);
        TextView text = findViewById(R.id.info_textTv);
        text.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        Bundle extras;
        if(intent.hasExtra(KEY_SCORES)) {
            extras = intent.getExtras();
            assert extras != null;
            if(extras.getBoolean(KEY_SCORES)) {
                title.setText(getString(R.string.scores_title));
                text.setText(getScores());
                addClearScoreHistoryListener();
            }else{
                title.setText("");
                text.setText("");
            }
        }
        else if(intent.hasExtra(KEY_HELP)){
            extras = intent.getExtras();
            assert extras != null;
            if(extras.getBoolean(KEY_HELP)) {
                title.setText(getString(R.string.help_title));
                text.setText(getString(R.string.help_text));
            }else{
                title.setText("");
                text.setText("");
            }
        }
        else if(intent.hasExtra(KEY_NOTE)){
            extras = intent.getExtras();
            assert extras != null;
            title.setText(extras.getStringArrayList(KEY_NOTE).get(0));
            text.setText(extras.getStringArrayList(KEY_NOTE).get(1));
        }
    }

    private void addClearScoreHistoryListener(){
        findViewById(R.id.info_textTv).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(InfoActivity.this)
                        .setTitle(getString(R.string.clear_history_title))
                        .setMessage(getString(R.string.clear_history_text))
                        .setNegativeButton("Yes, delete them!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSharedPreferences(BoardActivity.TOP_TEN_SCORES, MODE_PRIVATE).edit().clear().apply();
                                TextView text = findViewById(R.id.info_textTv);
                                text.setText(getString(R.string.no_scores));
                            }
                        })
                        .setPositiveButton("Nope!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create().show();
                return false;
            }
        });
    }

    private String getScores(){
        StringBuilder scoreTable = new StringBuilder();
        SharedPreferences scores = getSharedPreferences(BoardActivity.TOP_TEN_SCORES, MODE_PRIVATE);

        //Insertion Sort
        if(scores==null || scores.getInt("0 score", -1)==-1){
            return getString(R.string.no_scores);
        }

        for(int i=0; i<10; i++) {
            if(scores.getInt(i+" score", -1)==-1)
                scoreTable.append("");
            else
                scoreTable.append(scores.getInt(i+" score", 0));
            scoreTable.append( "  \t");
            scoreTable.append(scores.getString(i+" date", " "));
            scoreTable.append("\n");
        }
        return scoreTable.toString();///
    }
}
