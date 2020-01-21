package com.example.othello;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {
    protected static final String KEY_HELP = "help";
    protected static final String KEY_NOTE = "note";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

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
            ArrayList<String> list = extras.getStringArrayList(KEY_NOTE);
            if(list!=null) {
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(list.get(0));
                text.setText(list.get(1));
            }
        }
    }
}
