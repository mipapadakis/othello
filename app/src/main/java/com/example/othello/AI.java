package com.example.othello;

import java.util.ArrayList;
import java.util.Random;

class AI {
    private static final int BIG_BONUS = 100;
    private static final int SMALL_BONUS = 20;
    private static final int PENALTY = -5;
    private static int difficulty;

    private static final int[][] ratingTable =
            {   {200,   0,  40,  40,  40,  40,   0, 200},
                {  0,-200,   0,   0,   0,   0,-200,   0},
                { 40,   0,  10,  10,  10,  10,   0,  40},
                { 40,   0,  10,   5,   5,  10,   0,  40},
                { 40,   0,  10,   5,   5,  10,   0,  40},
                { 40,   0,  10,  10,  10,  10,   0,  40},
                {  0,-200,   0,   0,   0,   0,-200,   0},
                {200,   0,  40,  40,  40,  40,   0, 200}
            };

    static void play(Tile[][] board, int color, int difficulty){
        AI.difficulty = difficulty;
        if(difficulty<0)
            AI.difficulty=0;
        reduceUnnecessaryCalculations(board, difficulty);
        calculateMove(board, color, true);
    }

    private static void reduceUnnecessaryCalculations(Tile[][] board, int difficulty){
        int countGreen = 0;
        for(Tile[] row : board){
            for(final Tile tile: row){
                if(tile.isEmpty())
                    countGreen++;
            }
        }
        if(difficulty>countGreen)
            AI.difficulty = countGreen;
    }

    //Suppose that we placed a <color> tile in the position (i,j) of board.
    //Return a positive integer (variable <score>), that evaluates how good that move is.
    //Return -1 if the move is not allowed.
    private static int evaluateMove(Tile[][] board, int i, int j, int color){
        if(!board[i][j].isEmpty())
            return -1; //Can't place a tile on a non-Green position

        Tile examineTile = board[i][j];
        int score = 0;

        //Check if a tile can be played on this i,j position, and count how many enemy tiles
        //will be flipped. Add this number to the score.
        for (int direction = 0; direction < 8; direction++) {
            if(Tile.flipAllowed(board[i][j], color, direction)){
                do{
                    examineTile=examineTile.neighbor[direction];
                    score++;
                    if(examineTile.isEdge()){
                        score += 2; //Some extra points for any edge tiles being flipped
                    }
                }while(examineTile.neighbor[direction].getColor()!=color);
                examineTile=board[i][j];
            }
        }

        if(score<=0) //Is it an invalid move?
            return -1;

        ///////////POSITION RATING BONUS:///////////
        score += ratingTable[i][j];

        return score;
    }

    //Make a <difficulty>-rated move and return its evaluation.
    //For example, if difficulty==0, then calculateMove() calculates a move of level Easy.
    private static int calculateMove(Tile[][] board, int color, boolean makeTheMove){
        ArrayList<int[]> availableMoves = new ArrayList<>(); //Save in this list all the available moves, plus their evaluation.
        Tile[][] tempBoard = new Tile[8][8];
        int[] item; // item = {i, j, <evaluation of move>}

        //Find all available moves:
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                item = new int[]{i,j,evaluateMove(board, i, j, color)};
                if(item[2] != -1){ //Flipping is valid in position (i,j)
                    availableMoves.add(item);
                }
            }
        }

        if(availableMoves.size()==0)
            return -BIG_BONUS;

        if(difficulty>0) {
            //Create tempBoard:
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    tempBoard[i][j] = new Tile();
                }
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    tempBoard[i][j].setNeighbors(tempBoard, i, j);
                }
            }
            for (int m = 0; m < availableMoves.size(); m++) {
                //tempBoard = copy of board
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        tempBoard[i][j].setColor(board[i][j].getColor());
                    }
                }

                //availableMoves.get(m)[0] = i
                //availableMoves.get(m)[1] = j
                //availableMoves.get(m)[2] = evaluation
                Tile.flipTiles(tempBoard[availableMoves.get(m)[0]][availableMoves.get(m)[1]], color); //make the <m> move on the tempBoard.

                //Calculate evaluation of the best move on tempBoard for the player (assume its a move of level <difficulty> - 1 ):
                difficulty--;
                if (color == Tile.BLACK) {
                    if (Tile.ableToMove(tempBoard, Tile.WHITE) == Tile.CANT_PLAY)
                        availableMoves.get(m)[2] += BIG_BONUS; //This AI's move results in the player's inability to calculateMove!
                    availableMoves.get(m)[2] -= calculateMove(tempBoard, Tile.WHITE, false); //Subtract it from our evaluation of <m>
                } else if (color == Tile.WHITE) {
                    if (Tile.ableToMove(tempBoard, Tile.BLACK) == Tile.CANT_PLAY)
                        availableMoves.get(m)[2] += BIG_BONUS; //This AI's move results in the player's inability to calculateMove!
                    availableMoves.get(m)[2] -= calculateMove(tempBoard, Tile.BLACK, false); //Subtract it from our evaluation of <m>
                }

            }
        }

        item = availableMoves.get(0);
        //Find the move with max evaluation and store it in the <item> variable:
        for(int[] it: availableMoves){
            if(it[2]>item[2])
                item=it;
        }

        //If there are more than 1 moves that are equal to the maximum, pick one at random:
        ArrayList<int[]> max = new ArrayList<>();
        for(int[] it: availableMoves){
            if(it[2]==item[2])
                max.add(it); //Save in list <max> all the moves that have equal evaluation with the max evaluation
        }
        item = max.get( (new Random()).nextInt(max.size()) ); //Find random integer in [0, availableMoves.size)

        if(makeTheMove){ Tile.flipTiles(board[item[0]][item[1]], color); }
        return item[2];
    }
}
