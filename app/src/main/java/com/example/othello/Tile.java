package com.example.othello;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

class Tile{
    private static final int GREEN = 65280;
    static final int WHITE = 16777215;
    static final int BLACK = 0;
    private Button button;
    private int color;
    private  Tile[] neighbor; //neighbor[0] is the tile above this board. Move in a clockwise rotation to find the rest (explained in detail in method setNeighbors).

    Tile(){
        neighbor = new Tile[8];
        button=null;
        color=GREEN;
    }

    void setButton(View v){ button = (Button) v; }
    Button getButton(){ return button; }
    void setColor(int color){
        if(color==WHITE)
            button.setBackgroundResource(R.drawable.white);
        if(color==BLACK)
            button.setBackgroundResource(R.drawable.black);
        if(color==GREEN)
            button.setBackgroundResource(R.drawable.green);
        this.color = color;
    }
    private int getColor(){ return color; }
    /*
    public boolean isEmpty(){ return color==GREEN; }
    public boolean isWhite(){ return color==WHITE; }
    public boolean isBlack(){ return color==BLACK; }

    public Tile getN(){  return neighbor[0]; }
    public Tile getNE(){ return neighbor[1]; }
    public Tile getE(){  return neighbor[2]; }
    public Tile getSE(){ return neighbor[3]; }
    public Tile getS(){  return neighbor[4]; }
    public Tile getSW(){ return neighbor[5]; }
    public Tile getW(){  return neighbor[6]; }
    public Tile getNW(){ return neighbor[7]; }


    public void setColorN(int color){
        if(neighbor[0]!=null)
            neighbor[0].setColor(color);
    }
    public void setColorNE(int color){
        if(neighbor[1]!=null)
            neighbor[1].setColor(color);
    }
    public void setColorE(int color){
        if(neighbor[2]!=null)
            neighbor[2].setColor(color);
    }
    public void setColorSE(int color){
        if(neighbor[3]!=null)
            neighbor[3].setColor(color);
    }
    public void setColorS(int color){
        if(neighbor[4]!=null)
            neighbor[4].setColor(color);
    }
    public void setColorSW(int color){
        if(neighbor[5]!=null)
            neighbor[5].setColor(color);
    }
    public void setColorW(int color){
        if(neighbor[6]!=null)
            neighbor[6].setColor(color);
    }
    public void setColorNW(int color){
        if(neighbor[7]!=null)
            neighbor[7].setColor(color);
    }*/

    void setNeighbors(Tile[][] board, int row, int col){
        Tile tile = board[row][col];

        //   [NW][N ][NE]     [7] [0] [1]
        //   [W ][  ][E ]  =  [6] [ ] [2]
        //   [SW][S ][SE]     [5] [4] [3]


        // NORTH = neighbor[0]
        if(row==0)
            neighbor[0]=null;
        else
            neighbor[0]=board[row-1][col];

        // NORTH-EAST = neighbor[1]
        if(row==0 || col==7)
            neighbor[1]=null;
        else
            neighbor[1]=board[row-1][col+1];

        // EAST = neighbor[2]
        if(col==7)
            neighbor[2]=null;
        else
            neighbor[2]=board[row][col+1];

        // SOUTH-EAST = neighbor[3]
        if(row==7 || col==7)
            neighbor[3]=null;
        else
            neighbor[3]=board[row+1][col+1];

        // SOUTH = neighbor[4]
        if(row==7)
            neighbor[4]=null;
        else
            neighbor[4]=board[row+1][col];

        // SOUTH-WEST = neighbor[5]
        if(row==7 || col==0)
            neighbor[5]=null;
        else
            neighbor[5]=board[row+1][col-1];

        // WEST = neighbor[6]
        if(col==0)
            neighbor[6]=null;
        else
            neighbor[6]=board[row][col-1];

        // NORTH-WEST = neighbor[7]
        if(row==0 || col==0)
            neighbor[7]=null;
        else
            neighbor[7]=board[row-1][col-1];
    }

    //Called when a player makes a move. Returns false if the move is invalid. Else, returns true and flips the appropriate tiles of the board.
    static boolean flipTiles(Tile t, int color){
        Tile examineTile=t;
        boolean flag=false;

        if(t.color!=GREEN)
            return false;
        for(int direction=0; direction<8; direction++){
            if(flipAllowed(t, color, direction)){
                if(!flag)
                    flag=true;

                do{
                    examineTile=examineTile.neighbor[direction];
                    examineTile.setColor(color);
                }while(examineTile.neighbor[direction].getColor()!=color);
                examineTile=t;
            }
        }
        if(flag)
            t.setColor(color);
        return flag;
    }

    // Parameter <color> = the color of the tile that was just placed.
    // Parameter <direction> = the direction in which we examine if any tiles will be flipped.
    // Return true if the move leads to any tile-flipping in the specific direction. Else, return false.
    private static boolean flipAllowed(Tile  t, int color, int direction){
        Tile examineTile = t.neighbor[direction]; // direction: N=0, NE=1, E=2, SE=3, S=4, SW=5, W=6, NW=7
        int oppositeColor;

        if(color==WHITE)
            oppositeColor = BLACK;
        else if(color==BLACK)
            oppositeColor = WHITE;
        else
            return false;

        if(examineTile==null || examineTile.getColor()!=oppositeColor)
            return false;

        do{
            examineTile = examineTile.neighbor[direction];
            if(examineTile==null || examineTile.getColor()==GREEN)
                return false;
        }while(examineTile.getColor()==oppositeColor);
        return true;
    }

}

//TODO: check if a player has no valid move. If both, game ends => declare winner.
//TODO: check if the board is full and declare winner.