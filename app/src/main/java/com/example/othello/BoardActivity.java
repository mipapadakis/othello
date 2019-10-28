package com.example.othello;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BoardActivity extends AppCompatActivity {
    protected static final String KEY_MODE = "mode";
    protected static final int MODE_VS_AI=0;
    protected static final int MODE_ONLINE=1;
    protected static final int MODE_TWO_USERS=2;
    private ImageView nowPlaysIV;
    private TextView[] countTV;
    protected Toast toast;
    protected Tile[][] board;
    protected static boolean turnBlack;
    protected static int gameMode;
    private int twoInARow; //Checks if both players have no available move => when (twoInARow==2), game ends.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide(); //Hide ActionBar
        twoInARow=0;
        turnBlack = true; // First turn plays black
        gameMode = MODE_TWO_USERS; // By default: two users
        nowPlaysIV = findViewById(R.id.nowPlaysIV);

        countTV = new TextView[2];
        countTV[0] = findViewById(R.id.countBlackTV);
        countTV[1] = findViewById(R.id.countWhiteTV);

        Intent intent = getIntent();
        if(intent.hasExtra(KEY_MODE)) {
            Bundle extras = intent.getExtras();
            assert extras != null;
            if(extras.getInt(KEY_MODE)==MODE_ONLINE)
                gameMode = MODE_ONLINE;
            else if(extras.getInt(KEY_MODE)==MODE_VS_AI)
                gameMode = MODE_VS_AI;
        }

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
    }

    protected void onPause() {
        if(toast!=null){
            toast.cancel();
        }
        super.onPause();
    }

    private void addClickListeners(){
        for(Tile[] row : board){
            for(final Tile tile: row){
                if(gameMode==MODE_TWO_USERS){
                    tile.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean valid;
                            if (turnBlack)
                                valid = Tile.flipTiles(tile, Tile.BLACK);
                            else
                                valid = Tile.flipTiles(tile, Tile.WHITE);

                            if (valid) {
                                setTurnBlack(!turnBlack);
                                updateCounts();
                                if(!ableToMove()){
                                    //TODO: Game Over dialog
                                }

                            } else
                                showToast(getResources().getString(R.string.invalid));
                        }
                    });
                }
                else if(gameMode==MODE_ONLINE){
                    showToast("Online PvP not Implemented yet");
                }
                else{
                    showToast("Player vs. AI not Implemented yet");
                }
            }
        }
    }

    private boolean ableToMove(){
        int ableToMove = Tile.ableToMove(board, getCurrentColor());

        if(ableToMove==Tile.CAN_PLAY) {
            twoInARow=0;
            return true;
        }
        else if(ableToMove==Tile.CANT_PLAY){
            twoInARow++;
            if(twoInARow==2) {
                showToast(getResources().getString(R.string.no_moves));
                return false;
            }
            if(getCurrentColor()==Tile.BLACK) {
                //Black has no available move. White plays again.
                showToast(String.format(getResources().getString(R.string.cant_play), getResources().getString(R.string.black), getResources().getString(R.string.white)));
            }
            else{
                //White has no available move. Black plays again.
                showToast(String.format(getResources().getString(R.string.cant_play), getResources().getString(R.string.black), getResources().getString(R.string.white)));
            }
            setTurnBlack(!turnBlack);
        }
        else{ // ableToMove == Tile.BOARD_FULL
            int[] c = countTiles();
            if(c[Tile.BLACK]>c[Tile.WHITE]) {
                showToast("Game Over. Black wins!");
            }
            else if(c[Tile.BLACK]<c[Tile.WHITE]){
                showToast("Game Over. White wins!");
            }
            else{
                showToast("Game Over. It's a tie!");
            }
        }
        return false;
    }

    // int[0] = int[Tile.BLACK] = countTV of black tiles
    // int[1] = int[Tile.WHITE] = countTV of white tiles
    // int[2] = int[Tile.GREEN] = countTV of green tiles
    private int[] countTiles(){
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

    //Show how many tiles of each color exist on the board.
    private void updateCounts(){
        int[] c = countTiles();
        countTV[Tile.BLACK].setText(String.format(getResources().getString(R.string.count),c[Tile.BLACK]));
        countTV[Tile.WHITE].setText(String.format(getResources().getString(R.string.count),c[Tile.WHITE]));
    }

    private int getCurrentColor(){
        if(turnBlack)
            return Tile.BLACK;
        else
            return Tile.WHITE;
    }
    private void setTurnBlack(boolean b){
        turnBlack=b;
        if(turnBlack) {
            nowPlaysIV.setImageResource(R.drawable.black);
        }
        else {
            nowPlaysIV.setImageResource(R.drawable.white);
        }
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

    protected void showToast(String str){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(BoardActivity.this, str, Toast.LENGTH_SHORT);
        toast.show();
    }
}
