package com.example.othello;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class BoardActivity extends AppCompatActivity {
    protected static final String KEY_MODE = "mode";
    protected static final int MODE_VS_AI=0;
    protected static final int MODE_ONLINE=1;
    protected static final int MODE_TWO_USERS=2;
    private ImageView nowPlaysIV;
    protected Toast toast;
    protected Tile[][] board;
    protected static boolean turnBlack;
    protected static int gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);
        turnBlack = true; // First turn plays black
        gameMode = MODE_TWO_USERS; // By default: two users
        nowPlaysIV = findViewById(R.id.nowPlaysIV);

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
                            if(turnBlack)
                                valid = Tile.flipTiles(tile, Tile.BLACK);
                            else
                                valid = Tile.flipTiles(tile, Tile.WHITE);

                            if(valid)
                                setTurnBlack(!turnBlack);
                            else
                                showToast(getResources().getString(R.string.invalid));
                        }
                    });
                }
                else if(gameMode==MODE_ONLINE){
                    showToast("Online PvP not Implemented yet");
                    boolean valid;
                    if(turnBlack)
                        valid = Tile.flipTiles(tile, Tile.BLACK);
                    else
                        valid = Tile.flipTiles(tile, Tile.WHITE);

                    if(valid)
                        setTurnBlack(!turnBlack);
                    else
                        showToast(getResources().getString(R.string.invalid));
                }
                else{
                    showToast("Player vs. AI not Implemented yet");
                    boolean valid;
                    if(turnBlack)
                        valid = Tile.flipTiles(tile, Tile.BLACK);
                    else
                        valid = Tile.flipTiles(tile, Tile.WHITE);

                    if(valid)
                        setTurnBlack(!turnBlack);
                    else
                        showToast(getResources().getString(R.string.invalid));
                }
            }
        }

        /*
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                justClicked = board[i][j];
                if(gameMode==MODE_TWO_USERS){
                    justClicked.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean valid;
                            if(turnBlack)
                                valid = justClicked.flipTiles(justClicked, Tile.BLACK);
                            else
                                valid = justClicked.flipTiles(justClicked, Tile.WHITE);

                            if(valid)
                                setTurnBlack(!turnBlack);
                            else
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
        /*
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                board[i][j].setOnClickListener(getApplicationContext(), gameMode);
            }
        }*/
    }

    private void setTurnBlack(boolean b){
        turnBlack=b;
        if(turnBlack)
            nowPlaysIV.setImageResource(R.drawable.black);
        else
            nowPlaysIV.setImageResource(R.drawable.white);
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
