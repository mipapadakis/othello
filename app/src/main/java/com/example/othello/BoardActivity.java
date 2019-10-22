package com.example.othello;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BoardActivity extends AppCompatActivity {
    protected static String KEY_MODE = "mode";
    protected static int MODE_VS_AI=0;
    protected static int MODE_ONLINE=1;
    protected static int MODE_TWO_USERS=2;
    protected Button[][] tile;
    protected boolean turnBlack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);
        turnBlack=true; //First turn plays black
        tile = new Button[8][8];

        initialiseButtons();
        //Initial board has 2 whites and 2 blacks on the center.
        tile[3][3].setBackgroundResource(R.drawable.white);
        tile[3][4].setBackgroundResource(R.drawable.black);
        tile[4][3].setBackgroundResource(R.drawable.black);
        tile[4][4].setBackgroundResource(R.drawable.white);
        //TODO: methods getNorth(tile), getSouth(tile), getWest(tile), getEeast(tile), getSW(tile), getSE(tile), getNW(tile), getNE(tile)

        addClickListeners();
    }

    private void addClickListeners(){
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                tile[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(turnBlack) {
                            v.setBackgroundResource(R.drawable.black);
                        }
                        else{
                            v.setBackgroundResource(R.drawable.white);
                        }
                        turnBlack=!turnBlack;
                    }
                });
            }
        }
    }

    private void initialiseButtons(){
        tile[0][0]=findViewById(R.id.tile00);
        tile[0][1]=findViewById(R.id.tile01);
        tile[0][2]=findViewById(R.id.tile02);
        tile[0][3]=findViewById(R.id.tile03);
        tile[0][4]=findViewById(R.id.tile04);
        tile[0][5]=findViewById(R.id.tile05);
        tile[0][6]=findViewById(R.id.tile06);
        tile[0][7]=findViewById(R.id.tile07);

        tile[1][0]=findViewById(R.id.tile10);
        tile[1][1]=findViewById(R.id.tile11);
        tile[1][2]=findViewById(R.id.tile12);
        tile[1][3]=findViewById(R.id.tile13);
        tile[1][4]=findViewById(R.id.tile14);
        tile[1][5]=findViewById(R.id.tile15);
        tile[1][6]=findViewById(R.id.tile16);
        tile[1][7]=findViewById(R.id.tile17);

        tile[2][0]=findViewById(R.id.tile20);
        tile[2][1]=findViewById(R.id.tile21);
        tile[2][2]=findViewById(R.id.tile22);
        tile[2][3]=findViewById(R.id.tile23);
        tile[2][4]=findViewById(R.id.tile24);
        tile[2][5]=findViewById(R.id.tile25);
        tile[2][6]=findViewById(R.id.tile26);
        tile[2][7]=findViewById(R.id.tile27);

        tile[3][0]=findViewById(R.id.tile30);
        tile[3][1]=findViewById(R.id.tile31);
        tile[3][2]=findViewById(R.id.tile32);
        tile[3][3]=findViewById(R.id.tile33);
        tile[3][4]=findViewById(R.id.tile34);
        tile[3][5]=findViewById(R.id.tile35);
        tile[3][6]=findViewById(R.id.tile36);
        tile[3][7]=findViewById(R.id.tile37);

        tile[4][0]=findViewById(R.id.tile40);
        tile[4][1]=findViewById(R.id.tile41);
        tile[4][2]=findViewById(R.id.tile42);
        tile[4][3]=findViewById(R.id.tile43);
        tile[4][4]=findViewById(R.id.tile44);
        tile[4][5]=findViewById(R.id.tile45);
        tile[4][6]=findViewById(R.id.tile46);
        tile[4][7]=findViewById(R.id.tile47);

        tile[5][0]=findViewById(R.id.tile50);
        tile[5][1]=findViewById(R.id.tile51);
        tile[5][2]=findViewById(R.id.tile52);
        tile[5][3]=findViewById(R.id.tile53);
        tile[5][4]=findViewById(R.id.tile54);
        tile[5][5]=findViewById(R.id.tile55);
        tile[5][6]=findViewById(R.id.tile56);
        tile[5][7]=findViewById(R.id.tile57);

        tile[6][0]=findViewById(R.id.tile60);
        tile[6][1]=findViewById(R.id.tile61);
        tile[6][2]=findViewById(R.id.tile62);
        tile[6][3]=findViewById(R.id.tile63);
        tile[6][4]=findViewById(R.id.tile64);
        tile[6][5]=findViewById(R.id.tile65);
        tile[6][6]=findViewById(R.id.tile66);
        tile[6][7]=findViewById(R.id.tile67);

        tile[7][0]=findViewById(R.id.tile70);
        tile[7][1]=findViewById(R.id.tile71);
        tile[7][2]=findViewById(R.id.tile72);
        tile[7][3]=findViewById(R.id.tile73);
        tile[7][4]=findViewById(R.id.tile74);
        tile[7][5]=findViewById(R.id.tile75);
        tile[7][6]=findViewById(R.id.tile76);
        tile[7][7]=findViewById(R.id.tile77);
    }
}
