package othello.ai.battles;

public class AIbattle {
    protected static final int EASY_AI = 0;
    protected static final int MEDIUM_AI = 10;
    protected static final int HARD_AI = 20;
    protected static final int EXPERT_AI = 40; //The higher number, the more difficult
	protected static final int NUMBER_OF_BATTLES = 50;
    protected static final int[] RANGE = {-1, 1};
	protected static int C = 28;
    protected static Tile[][] board;
    protected static final int AIdefaultColor = Tile.BLACK;
    protected static final int AItestColor = Tile.WHITE;
	protected static int[] percentage, score;
	
	public static void main(String[] args) {
		int[][] maxTable = new int[8][8];
		int tmp, max = 0;
		int counter=0;
		percentage = new int[2];
		score = new int[NUMBER_OF_BATTLES];
		
		/////////////////////////Test RectangularValues/////////////////////////
		//initializeBoard();
		//Test.play(board, AItestColor, EXPERT_AI);
		////////////////////////////////////////////////////////////////////////
		
		Test.setRatingTableToDefault();
		for(int i=-100; i<=100; i++){
			System.out.println("Step "+(i+100) + " (C="+i+")");
			C = i;
			tmp = beginBattle(); //3^10 = 59,049‬ (=30h).
			if(tmp>max){
				max=tmp;
				//maxTable=Test.getRatingTable();
				System.out.println("Currently, the optimum Rating Table has a medium score of "+max);
				//Test.printRatingTable();
			}
		}
		/*
		Test.changeRatingTable(0, 0, RANGE[1]); //Corners
		Test.changeRatingTable(1, 1, RANGE[0]);
		//This produces RANGE^10 different rating tables, with the purpose of finding the most efficient one.
		for(int a=RANGE[0]; a<=RANGE[1]; a++){
			for(int b=RANGE[0]; b<=RANGE[1]; b++){
				for(int c=RANGE[0]; c<=RANGE[1]; c++){
					for(int d=RANGE[0]; d<=RANGE[1]; d++){
						for(int e=RANGE[0]; e<=RANGE[1]; e++){
							for(int f=RANGE[0]; f<=RANGE[1]; f++){
								for(int g=RANGE[0]; g<=RANGE[1]; g++){
									for(int h=RANGE[0]; h<=RANGE[1]; h++){
										for(int i=RANGE[0]; i<=RANGE[1]; i++){
											for(int j=RANGE[0]; j<=RANGE[1]; j++){
												counter++;
												if(counter%10==0)
													System.out.println("Step "+(counter));
												//System.out.println(a+""+b+""+c+""+d+""+e+""+f+""+g+""+h+""+i+""+j);
												Test.changeRatingTable(0, 0, a);//
												Test.changeRatingTable(0, 1, b);
												Test.changeRatingTable(0, 2, c);
												Test.changeRatingTable(0, 3, d);
												Test.changeRatingTable(1, 1, e);//
												Test.changeRatingTable(1, 2, f);
												Test.changeRatingTable(1, 3, g);
												Test.changeRatingTable(2, 2, h);
												Test.changeRatingTable(2, 3, i);
												Test.changeRatingTable(3, 3, j);
												//Test.printRatingTable();
												tmp = beginBattle(); //3^10 = 59,049‬ (=30h).
												if(tmp>max){
													max=tmp;
													maxTable=Test.getRatingTable();
													System.out.println("Currently, the optimum Rating Table (with a medium score of "+max+"), is:\n");
													Test.printRatingTable();
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Process finished, having compared "+(--counter)+" rating tables.\nResults:");
		System.out.println("Medium Score = "+max);
		Test.setRatingTable(maxTable);
		Test.printRatingTable();*/
	}
	
	private static int beginBattle(){
		//double[] results = new double[4];
		int[] count;
		int sum = 0;
		
		for(int i=0; i<NUMBER_OF_BATTLES; i++){
			initializeBoard();
			
			//Begin Battle:
			while(checkAbilityToMove(Tile.WHITE)==true && checkAbilityToMove(Tile.BLACK)==true){
				if(checkAbilityToMove(AIdefaultColor)==true)
					AI.play(board, AIdefaultColor, EXPERT_AI);

				if(checkAbilityToMove(AItestColor)==true)
					Test.play(board, AItestColor, EXPERT_AI);
			}

			//Count tiles
			count = countTiles(board);

			//Score of the testing AI
			score[i] = 1000*count[AItestColor]/64;
			if(count[AIdefaultColor]==0) //Case of early victory
				score[i] = 1000;
			sum+=score[i];
			
			//Who is the winner?
			if(count[AIdefaultColor]>count[AItestColor])
				percentage[AIdefaultColor]++;
			else if(count[AIdefaultColor]<count[AItestColor])
				percentage[AItestColor]++;
			
			
			//loadingBar(i+1, NUMBER_OF_BATTLES);
			//System.out.println("RESULTS:\nTesting AI Score: " + score[i] + "\nDefault AI: "+percentage[AIdefaultColor]+",\nTesting AI: "+percentage[AItestColor]);
		}
		
		//results[0] = 100*percentage[AItestColor]/NUMBER_OF_BATTLES; //Win percentage of Testing AI
		//results[1] = 100*percentage[AIdefaultColor]/NUMBER_OF_BATTLES; //Win percentage of Default AI
		//results[2] = 100*(NUMBER_OF_BATTLES - percentage[AIdefaultColor] - percentage[AItestColor]; //Ties
		//results[3] = sum/NUMBER_OF_BATTLES; //Score median
		
		/*Final results:
		System.out.println("\nWIN PERCENTAGE (IN "+NUMBER_OF_BATTLES+" BATTLES):\nTesting AI = " + (100*percentage[AItestColor]/NUMBER_OF_BATTLES) +"%");
		System.out.println("Default AI = " + (100*percentage[AIdefaultColor]/NUMBER_OF_BATTLES)+"%");
		System.out.println("      Ties = " + (100*(NUMBER_OF_BATTLES - percentage[AIdefaultColor] - percentage[AItestColor])/NUMBER_OF_BATTLES)+"%");
		System.out.println("\nSCORE MEDIAN = " + sum/NUMBER_OF_BATTLES);*/
		
		return sum/NUMBER_OF_BATTLES;
	}
	
	//return yes if player with <color> can play, else return false.
	private static boolean checkAbilityToMove(int color){
        int ableToMove = Tile.ableToMove(board, color);

        if(ableToMove==Tile.BOARD_FULL){
			//System.out.println("GAME OVER! Board Full.");
			return false;
		}
        else if(countTiles(board)[color]==0){
			//System.out.println(String.format("GAME OVER! %s has no tiles left.", color));
			return false;
		}
        else if(ableToMove==Tile.CANT_PLAY){
            if(color==Tile.BLACK) {
				if(Tile.ableToMove(board, Tile.WHITE) == Tile.CANT_PLAY){
                    //System.out.println("GAME OVER! No player has any legal moves.");
					return false;
                }
				//System.out.println("Black has no available move. White plays again.");
            }
            else{
                if(Tile.ableToMove(board, Tile.BLACK) == Tile.CANT_PLAY){
                    //System.out.println("GAME OVER! No player has any legal moves.");
					return false;
                }
				//System.out.println("White has no available move. Black plays again.");
            }
        }
		return true;
    }
	
	// int[0] = int[Tile.BLACK] = countTV of black tiles
    // int[1] = int[Tile.WHITE] = countTV of white tiles
    // int[2] = int[Tile.GREEN] = countTV of green tiles
    private static int[] countTiles(Tile[][] board){
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
	
	private static String toString(int color){
		if(color == Tile.BLACK)
			return "Black";
		if(color == Tile.WHITE)
			return "White";
		return "Green";
	}
	
	private static void initializeBoard(){
		board = new Tile[8][8];
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                board[i][j] = new Tile();
            }
        }
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
	}
	
	private static void loadingBar(int i, int total){
		System.out.print("[");
		for(int j=0; j<100; j++){
			if(j<100*i/total)
				System.out.print("|");
			else
				System.out.print(" ");
		}
		System.out.print("] "+(100*i/total)+"%\r");
	}
}
