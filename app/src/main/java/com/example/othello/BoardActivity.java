package com.example.othello;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BoardActivity extends AppCompatActivity {
    public static final String TOP_TEN_SCORES = "top10scores";
    protected static final String KEY_MODE = "mode";
    private ImageView nowPlaysIV; //Shows who is currently playing
    private TextView[] countTV; //Shows how many tiles of each color exist on board
    protected int evaluation; //evaluation = #white_tiles - #black_tiles
    protected Toast toast;
    protected Tile[][] board;
    protected boolean turnBlack;
    protected static int gameMode;
    protected static boolean AI_MODE;

    //Mode: player vs. AI
    protected static final int EASY_AI =100;
    protected static final int MEDIUM_AI =110;
    protected static final int HARD_AI =120;
    protected static final int EXPERT_AI =130;
    protected static int playerColor;
    private double score;

    // Mode: online
    protected static final int MODE_ONLINE=200;

    // Mode: two users
    protected static final int MODE_TWO_USERS=300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide(); //Hide ActionBar
        evaluation=0;
        turnBlack = true; //First turn plays black
        nowPlaysIV = findViewById(R.id.nowPlaysIV);

        //Player vs. AI
        playerColor = Tile.BLACK;

        //What mode?
        gameMode = MODE_TWO_USERS; //By default: two users
        Intent intent = getIntent();
        if(intent.hasExtra(KEY_MODE)) {
            Bundle extras = intent.getExtras();
            assert extras != null;
            if(extras.getInt(KEY_MODE)==MODE_ONLINE)
                gameMode = MODE_ONLINE;
            else if(extras.getInt(KEY_MODE) == EASY_AI || extras.getInt(KEY_MODE) == MEDIUM_AI || extras.getInt(KEY_MODE) == HARD_AI || extras.getInt(KEY_MODE) == EXPERT_AI) {
                gameMode = extras.getInt(KEY_MODE);
            }
        }
        AI_MODE = gameMode == EASY_AI || gameMode == MEDIUM_AI ||gameMode == HARD_AI || gameMode == EXPERT_AI;

        //Initialize the TextViews that show how many tiles of each color exist on board
        countTV = new TextView[2];
        countTV[0] = findViewById(R.id.countBlackTV);
        countTV[1] = findViewById(R.id.countWhiteTV);

        board = new Tile[8][8];
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                board[i][j] = new Tile();
            }
        }
        initialiseButtons();
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                board[i][j].setNeighbors(board, i, j);
            }
        }
        //Initial board has 2 whites and 2 blacks on the center.
        board[3][3].setColor(Tile.WHITE);
        board[3][4].setColor(Tile.BLACK);
        board[4][3].setColor(Tile.BLACK);
        board[4][4].setColor(Tile.WHITE);

        updateCounts();
        addClickListeners();

        if(AI_MODE)
            chooseColorDialog();
    }

    protected void onPause() {
        if(toast!=null){
            toast.cancel();
        }
        super.onPause();
    }

    protected boolean playsBlack(){return turnBlack;}
    protected boolean playsWhite(){return !turnBlack;}
    protected boolean playsPlayer(){return (playsBlack() && playerColor==Tile.BLACK) || (playsWhite() && playerColor==Tile.WHITE);}
    private int getCurrentColor(){
        if(playsBlack())
            return Tile.BLACK;
        else
            return Tile.WHITE;
    }
    private int getPlayerColor(){return playerColor;}
    private int getAIColor(){
        if(playerColor==Tile.WHITE)
            return Tile.BLACK;
        else
            return Tile.WHITE;
    }
    private int evaluateBoard(Tile[][] board){
        int[] count = countTiles(board);
        return count[Tile.WHITE] - count[Tile.BLACK];
    }
    //Calculate and show many tiles of each color exist on the board.
    private void updateCounts(){
        int[] c = countTiles(board);
        countTV[Tile.BLACK].setText(String.format(getResources().getString(R.string.count),c[Tile.BLACK]));
        countTV[Tile.WHITE].setText(String.format(getResources().getString(R.string.count),c[Tile.WHITE]));
    }

    protected void playAI(){
        if(playsPlayer())
            return;
        /*if(unableToMove(board)) {
            gameOverDialog();
            return;
        }*/

        //Insert the code into a timer in order to simulate the "thinking time" of the AI
        new CountDownTimer(800, 1) {
            @Override
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                if (gameMode == EASY_AI) {
                    AI.play(board, getAIColor(), 0);
                } else if (gameMode == MEDIUM_AI) {
                    AI.play(board, getAIColor(), 1);
                } else if (gameMode == HARD_AI){
                    AI.play(board, getAIColor(), 2);
                } else if (gameMode == EXPERT_AI){
                    AI.play(board, getAIColor(), 50);
                }
                if(AI_MODE){
                    nextTurn();
                    updateCounts();
                    evaluation = evaluateBoard(board);
                    if (unableToMove(board))
                        gameOverDialog();
                }
            }
        }.start();
    }

    private void nextTurn(){
        int[] count = countTiles(board);
        score += count[playerColor]*(60-count[Tile.GREEN]);

        turnBlack=!turnBlack;
        if(playsBlack())
            nowPlaysIV.setImageResource(R.drawable.black);
        else
            nowPlaysIV.setImageResource(R.drawable.white);

        if(AI_MODE && !playsPlayer())
            playAI();
    }
    // int[0] = int[Tile.BLACK] = countTV of black tiles
    // int[1] = int[Tile.WHITE] = countTV of white tiles
    // int[2] = int[Tile.GREEN] = countTV of green tiles
    private int[] countTiles(Tile[][] board){
        int[] count = new int[3];
        for(Tile[] row : board){
            for(final Tile tile: row){
                if(tile.isBlack())
                    count[Tile.BLACK]++;
                else if(tile.isWhite())
                    count[Tile.WHITE]++;
                else
                    count[Tile.GREEN]++;
            }
        }
        return count;
    }

    private void addClickListeners(){
        for(Tile[] row : board){
            for(final Tile tile: row){
                if(gameMode==MODE_TWO_USERS){
                    tile.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        boolean valid;
                        if (playsBlack())
                            valid = Tile.flipTiles(tile, Tile.BLACK);
                        else
                            valid = Tile.flipTiles(tile, Tile.WHITE);

                        if (valid) {
                            nextTurn();
                            updateCounts();
                            evaluation = evaluateBoard(board);
                            //showToast("evaluation = "+evaluation);
                            if(unableToMove(board))
                                gameOverDialog();

                        } else
                            showToast(getResources().getString(R.string.invalid));
                        }
                    });
                }
                else if(gameMode==MODE_ONLINE){
                    showToast("Online PvP not Implemented yet");
                }
                else{
                    tile.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        boolean valid = false;
                        if (playsPlayer())
                            valid = Tile.flipTiles(tile, getPlayerColor());

                        if (valid) {
                            nextTurn();
                            updateCounts();
                            evaluation = evaluateBoard(board);

                        } else
                            showToast(getResources().getString(R.string.invalid));
                        }
                    });
                }
            }
        }
    }

    private boolean unableToMove(Tile[][] board){
        int ableToMove = Tile.ableToMove(board, getCurrentColor());

        if(ableToMove==Tile.CAN_PLAY) {
            return false;
        }
        else if(ableToMove==Tile.CANT_PLAY){
            if(countTiles(board)[Tile.GREEN]==0){
                return true;
            }
            if(getCurrentColor()==Tile.BLACK) {
                if(Tile.ableToMove(board, Tile.WHITE) == Tile.CANT_PLAY){
                    showToast(getResources().getString(R.string.no_moves));
                    return true;
                }
                //Black has no available move. White plays again.
                showToastLong(String.format(getResources().getString(R.string.cant_play), getResources().getString(R.string.black), getResources().getString(R.string.white)));
            }
            else{
                if(Tile.ableToMove(board, Tile.BLACK) == Tile.CANT_PLAY ){
                    showToastLong(getResources().getString(R.string.no_moves));
                    return true;
                }
                //White has no available move. Black plays again.
                showToastLong(String.format(getResources().getString(R.string.cant_play), getResources().getString(R.string.black), getResources().getString(R.string.white)));
            }
            nextTurn();
            return false;
        }
        return true;
    }

    protected void showToast(String str){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(BoardActivity.this, str, Toast.LENGTH_SHORT);
        toast.show();
    }
    protected void showToastLong(String str){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(BoardActivity.this, str, Toast.LENGTH_LONG);
        toast.show();
    }

    private void gameOverDialog(){
        String title = getResources().getString(R.string.game_over), msg;
        if(evaluation==0) {
            msg = getResources().getString(R.string.tie) + " Score: "+(int) fixScore(score);
            if(gameMode!=MODE_TWO_USERS)
                commitScore(score);
        }
        else if(gameMode == MODE_TWO_USERS){
            if( evaluation>0 )
                msg = getResources().getString(R.string.win_white);
            else
                msg = getResources().getString(R.string.win_black);
        }
        else{
            if( evaluation>0 && playerColor==Tile.WHITE || evaluation<0 && playerColor==Tile.BLACK){
                msg = getResources().getString(R.string.you_won) + " Score: "+(int) fixScore(score);
            }
            else
                msg = getResources().getString(R.string.you_lost);
            commitScore(score);
        }

        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(msg)
            .setNegativeButton(getResources().getString(R.string.main_menu), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            })
            .setPositiveButton(getResources().getString(R.string.play_again), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gameReset();
                }
            }).create().show();
    }

    //Top ten scores will be stored locally on device, as well as the date & time of the score.
    //The score depends on how many tiles of your color the final board has, and how well you played overall.
    private void commitScore(double score){
        int currentScore, place = -1;
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        ArrayList<Integer> scoreList = new ArrayList();
        ArrayList<String> dateList = new ArrayList();
        SharedPreferences scores = getSharedPreferences(TOP_TEN_SCORES, MODE_PRIVATE);

        score = fixScore(score);

        //Insertion Sort
        if(scores!=null){
            for(int i=0; i<10; i++) {
                currentScore = scores.getInt(i+" score", 0);
                if(score>=currentScore && place == -1){
                    scoreList.add((int)score);
                    dateList.add(date + ",  \t" + time);
                    place = i;
                }
                if(currentScore>0){
                    scoreList.add(currentScore);
                    dateList.add(scores.getString(i+" date", " "));
                }
            }

            if(place==-1){ // <score> will NOT enter the top ten scores => nothing changes
                return;
            }
        }
        else{ //SharedPreferences == empty, so <score> enters the top ten list, at first place!
            scoreList.add((int)score);
            dateList.add(date + ", " + time);
            place = 0;
        }

        if(scoreList.isEmpty()){return;}

        //Commit changes
        SharedPreferences.Editor editor = getSharedPreferences(TOP_TEN_SCORES, MODE_PRIVATE).edit();
        for(int i=0; i<scoreList.size(); i++) {
            if(i>9){ break; }
            editor.putInt(i+" score", scoreList.get(i));
            editor.putString(i+" date", dateList.get(i));
        }
        showToastLong("You made it to top " + (place+1) + "! Well done!");
        editor.apply();
    }

    //<score> is updated every turn, using the following formula:
    //    score += count[playerColor]*(60-count[Tile.GREEN]);
    //This formula creates certain minimum and maximum values for the <score>, depending on color.
    //For example, I calculated that if you have BLACK tiles, in the worst case scenario your score
    //is 3570, and in the best case scenario its 77620. For WHITE, the interval is [3510, 77560].
    //So, I choose to scale the score for both colors, using a mutual interval: [0,1000].
    private double fixScore(double score){
        int min,max;
        if(playerColor==Tile.BLACK){
            min=3570;
            max=77620;
        }
        else{
            min=3510;
            max=77560;
        }
        return ((score-min)/(max-min))*1000; //Scale the score to the interval [0,1000]
    }

    private void chooseColorDialog(){
        String title = getResources().getString(R.string.choose_color), msg = getResources().getString(R.string.choose_color_txt);

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton(getResources().getString(R.string.white), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playerColor = Tile.WHITE;
                        playAI();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.black), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        playerColor = Tile.BLACK;
                    }
                }).create().show();
    }

    /*
    private void errorOccurredDialog(){
        String title = getResources().getString(R.string.error), msg = getResources().getString(R.string.error_txt);
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(msg)
            .setNegativeButton(getResources().getString(R.string.main_menu), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            })
            .setPositiveButton(getResources().getString(R.string.play_again), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gameReset();
                }
            }).create().show();
    }*/

    private void gameReset(){
        evaluation=0;
        turnBlack = true; // First turn plays black
        score = 0;

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                board[i][j].setColor(Tile.GREEN);
            }
        }
        board[3][3].setColor(Tile.WHITE);
        board[3][4].setColor(Tile.BLACK);
        board[4][3].setColor(Tile.BLACK);
        board[4][4].setColor(Tile.WHITE);
        updateCounts();

        if(AI_MODE)
            chooseColorDialog();
    }

    private void initialiseButtons(){
        board[0][0].setButton( findViewById(R.id.tile00) );
        board[0][1].setButton( findViewById(R.id.tile01) );
        board[0][2].setButton( findViewById(R.id.tile02) );
        board[0][3].setButton( findViewById(R.id.tile03) );
        board[0][4].setButton( findViewById(R.id.tile04) );
        board[0][5].setButton( findViewById(R.id.tile05) );
        board[0][6].setButton( findViewById(R.id.tile06) );
        board[0][7].setButton( findViewById(R.id.tile07) );

        board[1][0].setButton( findViewById(R.id.tile10) );
        board[1][1].setButton( findViewById(R.id.tile11) );
        board[1][2].setButton( findViewById(R.id.tile12) );
        board[1][3].setButton( findViewById(R.id.tile13) );
        board[1][4].setButton( findViewById(R.id.tile14) );
        board[1][5].setButton( findViewById(R.id.tile15) );
        board[1][6].setButton( findViewById(R.id.tile16) );
        board[1][7].setButton( findViewById(R.id.tile17) );

        board[2][0].setButton( findViewById(R.id.tile20) );
        board[2][1].setButton( findViewById(R.id.tile21) );
        board[2][2].setButton( findViewById(R.id.tile22) );
        board[2][3].setButton( findViewById(R.id.tile23) );
        board[2][4].setButton( findViewById(R.id.tile24) );
        board[2][5].setButton( findViewById(R.id.tile25) );
        board[2][6].setButton( findViewById(R.id.tile26) );
        board[2][7].setButton( findViewById(R.id.tile27) );

        board[3][0].setButton( findViewById(R.id.tile30) );
        board[3][1].setButton( findViewById(R.id.tile31) );
        board[3][2].setButton( findViewById(R.id.tile32) );
        board[3][3].setButton( findViewById(R.id.tile33) );
        board[3][4].setButton( findViewById(R.id.tile34) );
        board[3][5].setButton( findViewById(R.id.tile35) );
        board[3][6].setButton( findViewById(R.id.tile36) );
        board[3][7].setButton( findViewById(R.id.tile37) );

        board[4][0].setButton( findViewById(R.id.tile40) );
        board[4][1].setButton( findViewById(R.id.tile41) );
        board[4][2].setButton( findViewById(R.id.tile42) );
        board[4][3].setButton( findViewById(R.id.tile43) );
        board[4][4].setButton( findViewById(R.id.tile44) );
        board[4][5].setButton( findViewById(R.id.tile45) );
        board[4][6].setButton( findViewById(R.id.tile46) );
        board[4][7].setButton( findViewById(R.id.tile47) );

        board[5][0].setButton( findViewById(R.id.tile50) );
        board[5][1].setButton( findViewById(R.id.tile51) );
        board[5][2].setButton( findViewById(R.id.tile52) );
        board[5][3].setButton( findViewById(R.id.tile53) );
        board[5][4].setButton( findViewById(R.id.tile54) );
        board[5][5].setButton( findViewById(R.id.tile55) );
        board[5][6].setButton( findViewById(R.id.tile56) );
        board[5][7].setButton( findViewById(R.id.tile57) );

        board[6][0].setButton( findViewById(R.id.tile60) );
        board[6][1].setButton( findViewById(R.id.tile61) );
        board[6][2].setButton( findViewById(R.id.tile62) );
        board[6][3].setButton( findViewById(R.id.tile63) );
        board[6][4].setButton( findViewById(R.id.tile64) );
        board[6][5].setButton( findViewById(R.id.tile65) );
        board[6][6].setButton( findViewById(R.id.tile66) );
        board[6][7].setButton( findViewById(R.id.tile67) );

        board[7][0].setButton( findViewById(R.id.tile70) );
        board[7][1].setButton( findViewById(R.id.tile71) );
        board[7][2].setButton( findViewById(R.id.tile72) );
        board[7][3].setButton( findViewById(R.id.tile73) );
        board[7][4].setButton( findViewById(R.id.tile74) );
        board[7][5].setButton( findViewById(R.id.tile75) );
        board[7][6].setButton( findViewById(R.id.tile76) );
        board[7][7].setButton( findViewById(R.id.tile77) );
    }
    /*private void initialiseButtons(){
        button[0][0] = findViewById(R.id.tile00);
        button[0][1] = findViewById(R.id.tile01);
        button[0][2] = findViewById(R.id.tile02);
        button[0][3] = findViewById(R.id.tile03);
        button[0][4] = findViewById(R.id.tile04);
        button[0][5] = findViewById(R.id.tile05);
        button[0][6] = findViewById(R.id.tile06);
        button[0][7] = findViewById(R.id.tile07);

        button[1][0] = findViewById(R.id.tile10);
        button[1][1] = findViewById(R.id.tile11);
        button[1][2] = findViewById(R.id.tile12);
        button[1][3] = findViewById(R.id.tile13);
        button[1][4] = findViewById(R.id.tile14);
        button[1][5] = findViewById(R.id.tile15);
        button[1][6] = findViewById(R.id.tile16);
        button[1][7] = findViewById(R.id.tile17);

        button[2][0] = findViewById(R.id.tile20);
        button[2][1] = findViewById(R.id.tile21);
        button[2][2] = findViewById(R.id.tile22);
        button[2][3] = findViewById(R.id.tile23);
        button[2][4] = findViewById(R.id.tile24);
        button[2][5] = findViewById(R.id.tile25);
        button[2][6] = findViewById(R.id.tile26);
        button[2][7] = findViewById(R.id.tile27);

        button[3][0] = findViewById(R.id.tile30);
        button[3][1] = findViewById(R.id.tile31);
        button[3][2] = findViewById(R.id.tile32);
        button[3][3] = findViewById(R.id.tile33);
        button[3][4] = findViewById(R.id.tile34);
        button[3][5] = findViewById(R.id.tile35);
        button[3][6] = findViewById(R.id.tile36);
        button[3][7] = findViewById(R.id.tile37);

        button[4][0] = findViewById(R.id.tile40);
        button[4][1] = findViewById(R.id.tile41);
        button[4][2] = findViewById(R.id.tile42);
        button[4][3] = findViewById(R.id.tile43);
        button[4][4] = findViewById(R.id.tile44);
        button[4][5] = findViewById(R.id.tile45);
        button[4][6] = findViewById(R.id.tile46);
        button[4][7] = findViewById(R.id.tile47);

        button[5][0] = findViewById(R.id.tile50);
        button[5][1] = findViewById(R.id.tile51);
        button[5][2] = findViewById(R.id.tile52);
        button[5][3] = findViewById(R.id.tile53);
        button[5][4] = findViewById(R.id.tile54);
        button[5][5] = findViewById(R.id.tile55);
        button[5][6] = findViewById(R.id.tile56);
        button[5][7] = findViewById(R.id.tile57);

        button[6][0] = findViewById(R.id.tile60);
        button[6][1] = findViewById(R.id.tile61);
        button[6][2] = findViewById(R.id.tile62);
        button[6][3] = findViewById(R.id.tile63);
        button[6][4] = findViewById(R.id.tile64);
        button[6][5] = findViewById(R.id.tile65);
        button[6][6] = findViewById(R.id.tile66);
        button[6][7] = findViewById(R.id.tile67);

        button[7][0] = findViewById(R.id.tile70);
        button[7][1] = findViewById(R.id.tile71);
        button[7][2] = findViewById(R.id.tile72);
        button[7][3] = findViewById(R.id.tile73);
        button[7][4] = findViewById(R.id.tile74);
        button[7][5] = findViewById(R.id.tile75);
        button[7][6] = findViewById(R.id.tile76);
        button[7][7] = findViewById(R.id.tile77);
    }*/
}

//TODO: Online Mode
//TODO: default actions when an alert dialog is closed prematurely
//TODO: score sharedPreferences separate for each difficulty
//TODO: Bug - when AI has no legal moves, player gets their turn as normal, BUT toast doesn't show up