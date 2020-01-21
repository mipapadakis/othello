package com.example.othello;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.example.othello.ui.main.SectionsPagerAdapter;

public class ScoreActivity extends AppCompatActivity {
    protected static final String KEY_SCORES = "scores";
    private static String[] scoreTables; //Contains the score tables (for each difficulty)
    private static int deletedTabIndex = -1;
    private int tabSelected;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { tabSelected = tab.getPosition(); }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        TabLayout.Tab deletedTab = tabLayout.getTabAt(deletedTabIndex);
        if(deletedTab!=null){
            deletedTab.select();
            deletedTabIndex = -1;
            Snackbar.make(findViewById(R.id.fab), "Done!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }

        scoreTables = new String[4];
        for(int i=0; i<scoreTables.length; i++)
            scoreTables[i] = getScores(i);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(ScoreActivity.this)
                        .setTitle(getString(R.string.clear_history_title))
                        .setMessage(getString(R.string.clear_history_text))
                        .setNegativeButton("Yes, delete them!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSharedPreferences(getPref(tabSelected), MODE_PRIVATE).edit().clear().apply();
                                //scoreTables[tabSelected] = getString(R.string.no_scores);
                                deletedTabIndex = tabLayout.getSelectedTabPosition();
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        })
                        .setPositiveButton("Nope!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
            }
        });
    }

    public static String getScoreTable(int difficulty){ return scoreTables[difficulty]; }

    private String getPref(int difficulty){
        if(difficulty==0)
            return BoardActivity.TOP_TEN_EASY;
        if(difficulty==1)
            return BoardActivity.TOP_TEN_MEDIUM;
        if(difficulty==2)
            return BoardActivity.TOP_TEN_HARD;
        return BoardActivity.TOP_TEN_EXPERT;
    }

    private String getScores(int difficulty){
        StringBuilder scoreTable = new StringBuilder();//"Score:  \tDate:                \tTime:\n"
        SharedPreferences scores = getSharedPreferences(getPref(difficulty), MODE_PRIVATE);

        //Insertion Sort
        if(scores==null || scores.getInt("0 score", -1)==-1)
            return getString(R.string.no_scores);

        for(int i=0; i<10; i++) {
            if(scores.getInt(i+" score", -1)==-1)
                scoreTable.append("");
            else
                scoreTable.append(scores.getInt(i+" score", 0));
            scoreTable.append( "\t\t\t\t");
            scoreTable.append(scores.getString(i+" date", " "));
            scoreTable.append("\n");
        }
        return scoreTable.toString();
    }
}