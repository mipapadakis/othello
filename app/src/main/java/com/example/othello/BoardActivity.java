package com.example.othello;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    protected static final String KEY_MODE = "mode";
    private ImageView nowPlaysIV; //Shows who is currently playing
    private TextView[] countTV; //Shows how many tiles of each color exist on board
    protected int evaluation; //evaluation = #white_tiles - #black_tiles
    protected Toast toast;
    protected Tile[][] board;
    protected static boolean turnBlack;
    protected static int gameMode;
    private int twoInARow; //Checks if both players have no available move => when (twoInARow==2), game ends.

    //Mode: player vs. AI
    protected static final int MODE_VS_AI_EASY =100;
    protected static final int MODE_VS_AI_MEDIUM =110;
    protected static final int MODE_VS_AI_HARD =120;
    protected static int playerColor;
    protected static int difficulty;

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
        twoInARow=0;
        evaluation=0;
        turnBlack = true; //First turn plays black
        nowPlaysIV = findViewById(R.id.nowPlaysIV);

        //Player vs. AI
        playerColor = Tile.BLACK;
        difficulty = MODE_VS_AI_EASY;

        //What mode?
        gameMode = MODE_TWO_USERS; //By default: two users
        Intent intent = getIntent();
        if(intent.hasExtra(KEY_MODE)) {
            Bundle extras = intent.getExtras();
            assert extras != null;
            if(extras.getInt(KEY_MODE)==MODE_ONLINE)
                gameMode = MODE_ONLINE;
            else if(extras.getInt(KEY_MODE)== MODE_VS_AI_EASY) {
                gameMode = MODE_VS_AI_EASY;
            }
        }

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

        if(gameMode==MODE_VS_AI_EASY)
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
    protected boolean playsAI(){return !playsPlayer();}
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
    private void nextTurn(){
        turnBlack=!turnBlack;
        if(playsBlack())
            nowPlaysIV.setImageResource(R.drawable.black);
        else
            nowPlaysIV.setImageResource(R.drawable.white);
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
                            if(unableToMove())
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
                            if(unableToMove())
                                gameOverDialog();
                            else
                                flipTilesAI();

                        } else
                            showToast(getResources().getString(R.string.invalid));
                        }
                    });
                }
            }
        }
    }

    private boolean unableToMove(){
        int ableToMove = Tile.ableToMove(board, getCurrentColor());

        if(ableToMove==Tile.CAN_PLAY) {
            twoInARow=0;
            return false;
        }
        else if(ableToMove==Tile.CANT_PLAY){
            twoInARow++;
            if(twoInARow==2) {
                showToast(getResources().getString(R.string.no_moves));
                return true;
            }
            if(getCurrentColor()==Tile.BLACK) {
                //Black has no available move. White plays again.
                showToast(String.format(getResources().getString(R.string.cant_play), getResources().getString(R.string.black), getResources().getString(R.string.white)));
            }
            else{
                //White has no available move. Black plays again.
                showToast(String.format(getResources().getString(R.string.cant_play), getResources().getString(R.string.black), getResources().getString(R.string.white)));
            }
            nextTurn();
            return false;
        }
        return true;
    }

    private void flipTilesAI(){
        if(!playsAI())
            return;

        //Insert the code into a timer in order to simulate the "thinking time" of the AI
        new CountDownTimer(1000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                ArrayList<int[]> availableTiles = new ArrayList<>();
                Tile examineTile;
                int[] item;
                if(unableToMove()) {
                    gameOverDialog();
                    return;
                }

                //Find all available moves:
                for(int i=0; i<8; i++){
                    for(int j=0; j<8; j++){
                        if(board[i][j].isEmpty()) {
                            examineTile = board[i][j];

                            //Check if a tile can be played on this i,j position:
                            item = new int[]{i, j, 0}; // item[2] sums up all the player's tiles that will be flipped if the AI places its tile on this (i,j) position.
                            for (int direction = 0; direction < 8; direction++) {
                                if(Tile.flipAllowed(board[i][j], getAIColor(), direction)){
                                    do{
                                        examineTile=examineTile.neighbor[direction];
                                        item[2]++;
                                    }while(examineTile.neighbor[direction].getColor()!=getAIColor());
                                    examineTile=board[i][j];
                                }
                            }
                            if(item[2]>0){ //Flipping is valid in position (i,j)
                                availableTiles.add(item);
                            }
                        }
                    }
                }

                int[] maxFlipped = availableTiles.get(0);

                twoInARow=0;
                //Find and play the move that flips the most tiles
                for(int[] it: availableTiles){
                    if(it[2]>maxFlipped[2])
                        maxFlipped=it;
                }

                if(Tile.flipTiles(board[maxFlipped[0]][maxFlipped[1]], getAIColor())) {
                    nextTurn();
                    updateCounts();
                    evaluation = evaluateBoard(board);
                    //showToast("evaluation = "+evaluation);
                    if (unableToMove())
                        gameOverDialog();
                }else
                    showToast(getResources().getString(R.string.invalid));
            }
        }.start();

    }

    protected void showToast(String str){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(BoardActivity.this, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void gameOverDialog(){
        String title = getResources().getString(R.string.game_over), msg;
        if(evaluation==0)
            msg = getResources().getString(R.string.tie);
        else if(evaluation>0 && playerColor==Tile.WHITE || evaluation<0 && playerColor==Tile.BLACK)
            msg = getResources().getString(R.string.you_won);
        else
            msg = getResources().getString(R.string.you_lost);

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

    private void chooseColorDialog(){
        String title = getResources().getString(R.string.choose_color), msg = getResources().getString(R.string.choose_color_txt);

        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(msg)
            .setNegativeButton(getResources().getString(R.string.white), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    playerColor = Tile.WHITE;
                    flipTilesAI();
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
        twoInARow=0;
        evaluation=0;
        turnBlack = true; // First turn plays black

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

        if(gameMode==MODE_VS_AI_EASY)
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
}
