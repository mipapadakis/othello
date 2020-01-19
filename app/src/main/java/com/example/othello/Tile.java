package com.example.othello;

import android.view.View;
import android.widget.Button;

class Tile{
    static final int BLACK = 0; //Don't change the value!
    static final int WHITE = 1; //Don't change the value!
    static final int GREEN = 2; //Don't change the value!
    static final int CANT_PLAY = 100;
    static final int CAN_PLAY = 101;
    private static final int BOARD_FULL = 102;
    private Button button;
    private int color;
    Tile[] neighbor; //neighbor[0] is the tile above this board. Move in a clockwise rotation to find the rest (explained in detail in method setNeighbors).
    //int row;
    //int col;

    Tile(){
        neighbor = new Tile[8];
        button=null;
        color=GREEN;
    }

    Tile( Tile t){
        neighbor = new Tile[8];
        setButton(t.getButton());
        setColor(t.getColor());
    }

    void setButton(View v){ button = (Button) v; }
    Button getButton(){ return button; }
    void setColor(int color){
        if(button!=null){
            if(color==WHITE)
                button.setBackgroundResource(R.drawable.white);
            if(color==BLACK)
                button.setBackgroundResource(R.drawable.black);
            if(color==GREEN)
                button.setBackgroundResource(R.drawable.green);
        }
        this.color = color;
    }
    int getColor(){ return color; }
    boolean isEmpty(){ return color==GREEN; }
    boolean isWhite(){ return color==WHITE; }
    boolean isBlack(){ return color==BLACK; }
    boolean isEdge(){ return neighbor[0]==null || neighbor[2]==null || neighbor[4]==null || neighbor[6]==null;}
    boolean isCorner(){ return (neighbor[6]==null && neighbor[0]==null) || (neighbor[0]==null && neighbor[2]==null)
                            || (neighbor[6]==null && neighbor[4]==null) || (neighbor[4]==null && neighbor[2]==null);}

    /*
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

    //Return CANT_PLAY if the player with <color> tiles has no valid move to calculateMove.
    //Return BOARD_FULL if the board is full.
    //Return CAN_PLAY otherwise
    static int ableToMove(Tile[][] board, int color){
        boolean full=true;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(board[i][j].isEmpty()) {
                    if(full)
                        full = false;
                    //check if a tile can be played on this i,j position:
                    for (int direction = 0; direction < 8; direction++) {
                        if (flipAllowed(board[i][j], color, direction))
                            return CAN_PLAY;
                    }
                }
            }
        }
        if(full)
            return BOARD_FULL;
        return CANT_PLAY;
    }

    //Called when a player makes a move. Returns false if the move is invalid. Else, returns true and flips the appropriate tiles of the board.
    static boolean flipTiles(Tile t, int color){
        Tile examineTile=t;
        boolean flag=false;

        if(t.color!=GREEN)
            return false;

        //check
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
    static boolean flipAllowed(Tile t, int color, int direction){
        Tile examineTile = t.neighbor[direction]; // direction: N=0, NE=1, E=2, SE=3, S=4, SW=5, W=6, NW=7
        int oppositeColor;

        if(color==WHITE)
            oppositeColor = BLACK;
        else if(color==BLACK)
            oppositeColor = WHITE;
        else
            return false;

        if(!t.isEmpty() || examineTile==null || examineTile.getColor()!=oppositeColor)
            return false;

        do{
            examineTile = examineTile.neighbor[direction];
            if(examineTile==null || examineTile.getColor()==GREEN)
                return false;
        }while(examineTile.getColor()==oppositeColor);
        return true;
    }
}