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
    protected static final String KEY_HELP = "help";
    protected static final String KEY_NOTE = "note";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);
        //getSupportActionBar().hide(); //Hide ActionBar
        TextView text = findViewById(R.id.info_textTv);
        text.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        Bundle extras;
        if(intent.hasExtra(KEY_HELP)){
            extras = intent.getExtras();
            assert extras != null;
            if(extras.getBoolean(KEY_HELP)) {
                if(getSupportActionBar()!=null)
                    getSupportActionBar().setTitle(getString(R.string.help_title));
                text.setText(getString(R.string.help_text));
            }else{
                if(getSupportActionBar()!=null)
                    getSupportActionBar().setTitle("");
                text.setText("");
            }
        }
        else if(intent.hasExtra(KEY_NOTE)){
            extras = intent.getExtras();
            assert extras != null;
            if(getSupportActionBar()!=null)
                getSupportActionBar().setTitle(extras.getStringArrayList(KEY_NOTE).get(0));
            text.setText(extras.getStringArrayList(KEY_NOTE).get(1));
        }
    }
}
