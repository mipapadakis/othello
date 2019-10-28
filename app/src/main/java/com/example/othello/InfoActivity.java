package com.example.othello;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    protected static final String KEY_SCORES = "scores";
    protected static final String KEY_HELP = "help";
    protected static final String KEY_NOTE = "note";
    private TextView title;
    private TextView text;
    private Intent intent;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide(); //Hide ActionBar
        title = findViewById(R.id.info_titleTv);
        text = findViewById(R.id.info_textTv);
        text.setMovementMethod(new ScrollingMovementMethod());

        intent = getIntent();
        if(intent.hasExtra(KEY_SCORES)) {
            extras = intent.getExtras();
            assert extras != null;
            if(extras.getBoolean(KEY_SCORES)) {
                title.setText(getString(R.string.scores_title));
                text.setText(getString(R.string.scores_text));
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
}
