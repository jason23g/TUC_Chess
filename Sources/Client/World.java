package Client;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;


public class World
{
/*	private String[][] board = null;
	private int rows = 7;
	private int columns = 5;*/
	private Board table = null;
	//String[][] board = null;
	private int myColor = 0;
	private ArrayList<String> availableMoves = null;
	private int rookBlocks = 3;		// rook can move towards <rookBlocks> blocks in any vertical or horizontal direction
	private int nTurns = 0;
	private int nBranches = 0;
	private int noPrize = 9;
	public int nKings = 2;
	public int whitePieces = 10;
	public int blackPieces = 10;
	public int numberOfWhiteKings = 0;
	public int numberOfWhiteRooks = 0;
	public int numberOfWhitePawns = 0;
	public int numberOfBlackKings = 0;
	public int numberOfBlackRooks = 0;
	public int numberOfBlackPawns = 0;
	private int whitePoints = 0;
	private int blackPoints = 0;
	private NaryTree MCTSTree = null; 
	public String finMove="";
	public int whiteKingX = 6;
	public int whiteKingY = 2;
	public int blackKingX = 0;
	public int blackKingY = 2;
	
	public int maxDepth = 6;
	
	public World()
	{
		table = new Board();
		//board = table.board;
		
		
		
		/* represent the board
		
		BP|BR|BK|BR|BP
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP
		*/
		
		// initialization of the board
		for(int i=0; i<table.rows; i++)
			for(int j=0; j<table.columns; j++)
				table.board[i][j] = " ";
		
		// setting the black player's chess parts
		
		// black pawns
		for(int j=0; j<table.columns; j++)
			table.board[1][j] = "BP";
		
		table.board[0][0] = "BP";
		table.board[0][table.columns-1] = "BP";
		
		// black rooks
		table.board[0][1] = "BR";
		table.board[0][table.columns-2] = "BR";
		
		// black king
		table.board[0][table.columns/2] = "BK";
		
		// setting the white player's chess parts
		
		// white pawns
		for(int j=0; j<table.columns; j++)
			table.board[table.rows-2][j] = "WP";
		
		table.board[table.rows-1][0] = "WP";
		table.board[table.rows-1][table.columns-1] = "WP";
		
		// white rooks
		table.board[table.rows-1][1] = "WR";
		table.board[table.rows-1][table.columns-2] = "WR";
		
		// white king
		table.board[table.rows-1][table.columns/2] = "WK";
		
		// setting the prizes
		for(int j=0; j<table.columns; j++)
			table.board[table.rows/2][j] = "P";
		
		availableMoves = new ArrayList<String>();
	}
	
	public void setMyColor(int myColor)
	{
		this.myColor = myColor;
	}
	
	 //option 0 for random Action 
	// option 1 for minimax action
	public String selectAction(int option)
	{
		String move = "";
		int depth = 6;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		availableMoves = new ArrayList<String>();
				
		if(this.myColor == 0)		// I am the white player
			this.whiteMoves();
		else					// I am the black player
			this.blackMoves();
		
		// keeping track of the branch factor
		nTurns++;
		nBranches += availableMoves.size();
		
		if(option == 0)
			move = this.selectRandomAction();
		else if(option == 1) {
			this.finMove = null;
			this.maxDepth = 4;
			miniMax(this.maxDepth, alpha, beta, this.myColor, false);
			move = this.finMove;
		}
		else if(option == 2) {
			this.finMove = null;
			this.maxDepth = 6;
			miniMax(this.maxDepth, alpha, beta, this.myColor, true);
			move = this.finMove;
		}
		else if(option == 3) {
			this.finMove = null;
			MCTSTree = new NaryTree(table, this.myColor);
			MCTSTree.root.board = table;
			MCTSTree.root.children = null;
			MCTSTree.root.numberOfVisits = 0;
			MCTSTree.root.reward = 0;
			MCTS(MCTSTree.root);
			move = this.finMove;
		}
		
		return move;
	}
	
	private void whiteMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
		int pieces = 0;
		int numberOfKings = 0;
		int numberOfRooks = 0;
		int numberOfPawns = 0;
		
		boolean kingAlive = false;
				
		for(int i=0; i<table.rows; i++)
		{
			for(int j=0; j<table.columns; j++)
			{
				firstLetter = Character.toString(table.board[i][j].charAt(0));
				
				// if it there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				if(firstLetter.equals("W"))
					pieces++; //count the white pieces left on the board
				
				if(secondLetter.equals("K"))
					kingAlive = true;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(table.board[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					numberOfPawns++;
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(table.board[i-1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(table.board[i-1][j-1].charAt(0));						
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									   Integer.toString(i-1) + Integer.toString(j-1);
								
							availableMoves.add(move);
						}											
					}
					
					// check if it can move crosswise to the right
					if(j!=table.columns-1 && i!=0)
					{
						firstLetter = Character.toString(table.board[i-1][j+1].charAt(0));
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							
							move = Integer.toString(i) + Integer.toString(j) + 
									   Integer.toString(i-1) + Integer.toString(j+1);							
							availableMoves.add(move);
						}
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					numberOfRooks++;
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(table.board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == table.rows)
							break;
						
						firstLetter = Character.toString(table.board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(table.board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == table.columns)
							break;
						
						firstLetter = Character.toString(table.board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					numberOfKings++;
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(table.board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < table.rows)
					{
						firstLetter = Character.toString(table.board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(table.board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < table.columns)
					{
						firstLetter = Character.toString(table.board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
		this.whitePieces = pieces;
		this.numberOfWhiteKings = numberOfKings;
		this.numberOfWhiteRooks = numberOfRooks;
		this.numberOfWhitePawns = numberOfPawns;
		//if(!kingAlive)
		//	nKings -= 1;

	}
	
	
	private void blackMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
		int pieces = 0;
		int numberOfKings = 0;
		int numberOfRooks = 0;
		int numberOfPawns = 0;
		boolean kingAlive = false;
				
		for(int i=0; i<table.rows; i++)
		{
			for(int j=0; j<table.columns; j++)
			{
				firstLetter = Character.toString(table.board[i][j].charAt(0));
				
				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				if(firstLetter.equals("B"))
					pieces++; //count the black pieces left on the board
				
				if(secondLetter.equals("K"))
					kingAlive = true;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(table.board[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					numberOfPawns++;
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(table.board[i+1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=table.rows-1)
					{
						firstLetter = Character.toString(table.board[i+1][j-1].charAt(0));
						
						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									   Integer.toString(i+1) + Integer.toString(j-1);
								
							availableMoves.add(move);
						}																	
					}
					
					// check if it can move crosswise to the right
					if(j!=table.columns-1 && i!=table.rows-1)
					{
						firstLetter = Character.toString(table.board[i+1][j+1].charAt(0));
						
						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									   Integer.toString(i+1) + Integer.toString(j+1);
								
							availableMoves.add(move);
						}
							
						
						
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					numberOfRooks++;
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(table.board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == table.rows)
							break;
						
						firstLetter = Character.toString(table.board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(table.board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == table.columns)
							break;
						
						firstLetter = Character.toString(table.board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					numberOfKings++;
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(table.board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < table.rows)
					{
						firstLetter = Character.toString(table.board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(table.board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < table.columns)
					{
						firstLetter = Character.toString(table.board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
		
		this.blackPieces = pieces;
		this.numberOfBlackKings = numberOfKings;
		this.numberOfBlackRooks = numberOfRooks;
		this.numberOfBlackPawns = numberOfPawns;
		//if(!kingAlive)
		//	nKings -= 1;

	}
	
	private String selectRandomAction()
	{		
		Random ran = new Random();
		int x = ran.nextInt(availableMoves.size());
		
		return availableMoves.get(x);
	}
	
	//option false : No use of alpha-beta prunning 
   // option true : Use of alpha-beta prunning
	public int miniMax(int depth,int alpha,int beta,int color,boolean prunning) {
			
			int maxEval;
			int minEval;
			int eval;
			String unmadeMove = "";

			
			if (depth == 0 || checkEnd()) {
				//System.out.println("final move "+this.finMove);
				return evaluateMove();
			}
			
			//Maximizing Player
			if(this.myColor == color) {
				
				maxEval = Integer.MIN_VALUE;
				
				this.availableMoves = new ArrayList<String>();
				
				if(color == 0)
					this.whiteMoves();
				else
					this.blackMoves();
				//ArrayList<String> whitePossibleMoves = new ArrayList<String>();
				ArrayList<String> whitePossibleMoves = this.availableMoves;
				
				sortMoves(whitePossibleMoves);
				
				for(int i = 0; i< whitePossibleMoves.size();i++) {
					
					int x1 = Integer.parseInt(Character.toString(whitePossibleMoves.get(i).charAt(0)));
					int y1 = Integer.parseInt(Character.toString(whitePossibleMoves.get(i).charAt(1)));
					int x2 = Integer.parseInt(Character.toString(whitePossibleMoves.get(i).charAt(2)));
					int y2 = Integer.parseInt(Character.toString(whitePossibleMoves.get(i).charAt(3)));
					
					String victim = makeMove(x1, y1, x2, y2, 9, 9,color);

					
					if(color == 0)
						eval = miniMax(depth-1,alpha,beta,1,prunning);
					else
						eval = miniMax(depth-1,alpha,beta,0,prunning);
					//maxEval	= max(maxEval,eval);
					

					unmakeMove(x2, y2, x1, y1, 9, 9, victim,color);
					
					unmadeMove = "";
					
					unmadeMove += x1;
					unmadeMove += y1;
					unmadeMove += x2;
					unmadeMove += y2;
					
					if(table.board[x1][y1].equals("BK") && depth == this.maxDepth) {
						blackKingX = x2;
						blackKingY = y2;
						if(defendKing(1))
							eval = Integer.MIN_VALUE;
					}
					else if(table.board[x1][y1].equals("WK") && depth == this.maxDepth) {
						whiteKingX = x2;
						whiteKingY = y2;
						if(defendKing(0))
							eval = Integer.MIN_VALUE;
					}

					//if capturing the king is among the possible first moves, then give it the highest priority
					if((victim.equals("BK") || victim.equals("WK")) && depth == this.maxDepth) {
						this.finMove = unmadeMove;
						return maxEval;
					}
					
					
					if(eval > maxEval) {
						
						maxEval = eval;
						
						if(depth == this.maxDepth) {
							this.finMove = unmadeMove;
						}		
				    }
					
					if(prunning) {
						alpha =  max(alpha,eval);
						
						if( beta <= alpha)
							break;
					}

				}
				
				return maxEval;
				
				
			}
			
			//Minimizing Player
			else {
					
					minEval = Integer.MAX_VALUE;
					
					this.availableMoves = new ArrayList<String>();
					
					if(color == 0)
						this.whiteMoves();
					else
						this.blackMoves();
					//ArrayList<String> blackPossibleMoves = new ArrayList<String>();
					ArrayList<String> blackPossibleMoves = this.availableMoves;

					sortMoves(blackPossibleMoves);
					
					for(int i = 0; i< blackPossibleMoves.size();i++) {
						
						int x1 = Integer.parseInt(Character.toString(blackPossibleMoves.get(i).charAt(0)));
						int y1 = Integer.parseInt(Character.toString(blackPossibleMoves.get(i).charAt(1)));
						int x2 = Integer.parseInt(Character.toString(blackPossibleMoves.get(i).charAt(2)));
						int y2 = Integer.parseInt(Character.toString(blackPossibleMoves.get(i).charAt(3)));
						
						String victim = makeMove(x1, y1, x2, y2, 9, 9,color);
						
						
						if(color == 0)
							eval = miniMax(depth-1,alpha,beta,1,prunning);
						else
							eval = miniMax(depth-1,alpha,beta,0,prunning);
						
						
						
						unmakeMove(x2, y2, x1, y1, 9, 9, victim,color);
						
						unmadeMove = "";
					
						unmadeMove += x1;
						unmadeMove += y1;
						unmadeMove += x2;
						unmadeMove += y2;
						
						minEval = min(eval, minEval);
						
						
						if(prunning) {
							beta =   min(beta,eval);
							
							if( beta <= alpha)
								break;
						}
						 
					}
					
					return minEval;
				
				
				
			}
			
			
		}
	
	public int max(int x,int y) {
		int maxValue = 0;
		
		if(x < y)
			maxValue = y;
		else
			maxValue = x;
		
		return maxValue;
	}
	
	public int min(int x, int y) {
		
		int minValue = 0;
		
		if(x < y)
			minValue = x;
		else
			minValue = y;
		
		return minValue;
		
	}
	
	public int evaluateMove() {
		int score = 0;
		int weightPawn = 1;
		int weightRook = 3;
		int weightKing = 8;
		int valueOfWhitePieces = this.numberOfWhitePawns*weightPawn + this.numberOfWhiteRooks*weightRook + this.numberOfWhiteKings*weightKing;
		int valueOfBlackPieces = this.numberOfBlackPawns*weightPawn + this.numberOfBlackRooks*weightRook + this.numberOfBlackKings*weightKing;
		
		if(this.myColor == 0)
			score = (valueOfWhitePieces + this.whitePoints) - (valueOfBlackPieces + this.blackPoints);
		else
			score = (valueOfBlackPieces + this.blackPoints) - (valueOfWhitePieces + this.whitePoints);
		
		
		
		return score;
	}
	
	
	public double getAvgBFactor()
	{
		return nBranches / (double) nTurns;
	}
	
	//Moves from (x1,y1) to (x2,y2). Returns the name of the pawn that was captured, if any
		public String makeMove(int x1, int y1, int x2, int y2, int prizeX, int prizeY,int color)
		{
			
			//System.out.println("Make move from: "+board[x1][y1]+" to "+board[x2][y2]);
			String chesspart = Character.toString(table.board[x1][y1].charAt(1));
			String victim = " ";
			
			boolean pawnLastRow = false;
			
			// check if it is a move that has made a move to the last line
			if(chesspart.equals("P"))
				if( (x1==table.rows-2 && x2==table.rows-1) || (x1==1 && x2==0) )
				{
					victim = table.board[x2][y2];
					table.board[x2][y2] = " ";	// in a case an opponent's chess part has just been captured
					table.board[x1][y1] = " ";
					
					if(color == 0)
						whitePoints++;
					else
						blackPoints++;
					
					pawnLastRow = true;
				}
			
			// otherwise
			if(!pawnLastRow)
			{
				victim = table.board[x2][y2];
				
				 
				if(victim.equals("BK") || victim.equals("WK"))
					this.nKings--;
				
				if(color == 0) {
					whitePoints += pointsWorth(victim);
					
				}
				else {
					blackPoints += pointsWorth(victim);
					
				}
				table.board[x2][y2] = table.board[x1][y1];
				table.board[x1][y1] = " ";
				
			}
			
			// check if a prize has been added in the game
			if(prizeX != noPrize)
				table.board[prizeX][prizeY] = "P";
			
			return victim;
		}
		
		public void unmakeMove(int x2, int y2, int x1, int y1, int prizeX, int prizeY, String victim,int color)
		{
			
			//System.out.println("Unmake move from: "+board[x2][y2]+" to "+board[x1][y1]);
			String chesspart;
			
			if(table.board[x2][y2].length() == 2)
				chesspart = Character.toString(table.board[x2][y2].charAt(1));
			else
				chesspart = " ";
		
			boolean pawnLastRow = false;
			
			// check if it is a move that has made a move to the last line
			if(chesspart.equals(" "))
				if( (x1==table.rows-2 && x2==table.rows-1) || (x1==1 && x2==0) )
				{
					table.board[x2][y2] = victim;	// put back the captured pawn
					
					if(color == 0) {
						table.board[x1][y1] = "WP";
						whitePoints--;
					}
					else {
						table.board[x1][y1] = "BP";
						blackPoints--;
					}
					
					pawnLastRow = true;
				}
			
			// otherwise
			if(!pawnLastRow)
			{
				table.board[x1][y1] = table.board[x2][y2];
				table.board[x2][y2] = victim; // put back the captured pawn
				
				if(victim.equals("BK") || victim.equals("WK"))
					this.nKings++;
				
				if(color == 0) {
					whitePoints -= pointsWorth(victim);
					
					
				}
				else {
					blackPoints -= pointsWorth(victim);
					
				}
				
			}
			
			// check if a prize has been added in the game
			if(prizeX != noPrize)
				table.board[prizeX][prizeY] = "P";
		}
		
		
		private int pointsWorth(String victim) {
			
			int points;
			Random r = new Random();
			int randomPick;
			
			if(victim.equals(" ")) {
				points = 0;
				return points;
			}
			
			if(victim.equals("P")) {
				randomPick = r.nextInt(10);
				if(randomPick == 0)// 0.1 probability of not receiving a point
					points = 0;
				else // 0.9 probability of receiving a point
					points = 1;
					
				return points;
			}
			
			if(Character.toString(victim.charAt(1)).equals("P"))
				points = 1;
			else if(Character.toString(victim.charAt(1)).equals("R"))
				points = 3;
			else if(Character.toString(victim.charAt(1)).equals("K"))
				points = 8;
			else
				points = 0;
			
			return points;
		}
	
	
		private boolean checkEnd() {
			
			int total_pieces = this.blackPieces + this.whitePieces;
			
			if(this.nKings == 1 || total_pieces == 2) {
			
				return true;
			}
			
			return false;
		}
		
		//https://www.baeldung.com/java-monte-carlo-tree-search?fbclid=IwAR2ikonffNmUoDyr44z6mTdWoDyBLXZeTP-R0t6DVruWLWXjzV1NQDgpr3M

		public void MCTS(Node root) {
			
			long startTime = 0;
			long endTime = 0;
			final long executionTime = 6000;
			Node currentNode = root;
			int r = 0;
			int numOfSim = 0;
			
			startTime = System.currentTimeMillis();
			
			while(endTime - startTime < executionTime) {
				
				currentNode = root;
				
				currentNode = select(currentNode);
				
				if(currentNode.numberOfVisits > 0) {
					expandNode(currentNode, currentNode.color);
					currentNode = currentNode.children.get(0);
					r = simulateGame(currentNode, currentNode.color);
				}
				else {
					r = simulateGame(currentNode, currentNode.color);
				}
				
				backpropagate(currentNode, r);
				
				endTime = System.currentTimeMillis();
				numOfSim++;
			}
			
			Node bestState = findBestUCT(root);
			this.finMove = bestState.board.getFromMove();
			
			
			
		}
		
		
		private Node select(Node current) {
			
			Node node = current;
			//Node tmpNode = null;
			
			while(node.children != null){
				
				node = findBestUCT(node);
				/*
				 * if(tmpNode == null) { System.out.println("ROOT IS NULL"); break; }
				 */
				
				//node = tmpNode;
				
			}
			
			return node;
		}
		
		private Node findBestUCT(Node node) {
			
			double uct = 0d;
			double lnN = 0d;
			double epsilon = 0.001;
			double maxUCT = Double.NEGATIVE_INFINITY;
			Node bestNode = null;
			final double MAX_REWARD = 31d;
			
			if(node.children == null) {
				return node;
			}
			
			for(Node n: node.children) {
				lnN = Math.log(node.numberOfVisits + epsilon);
				uct = ((double)node.reward/MAX_REWARD) + (1/Math.sqrt(2d))*(Math.sqrt((lnN)/(n.numberOfVisits+epsilon)));
				if(uct >= maxUCT) {
					maxUCT = uct;
					bestNode = n;
				}
			}
			
			/*
			 * if(bestNode == null) System.out.println("fuck");
			 */
			
			return bestNode;
		}
		
		
		private void expandNode(Node node,int color) {
			
			
			LinkedList<Board> possibleBoards = getAllPossibleBoards(color);
			
			for(Board board : possibleBoards) {
				
				Node n = new Node(board);
				node.add(n);
				
			}
			
		}
		
		private LinkedList<Board> getAllPossibleBoards(int color) {
			
			Board tmpBoard = null;
			
			tmpBoard = (Board)this.table.clone();
			
			String victim = "";
			
			this.availableMoves = new ArrayList<String>();
			
			if(color == 0)
				this.whiteMoves();
			else 
				this.blackMoves();
			
			ArrayList<String> PossibleMoves = this.availableMoves;
			LinkedList<Board> possibleBoards = new LinkedList<Board>();
			
			for(int i = 0; i< PossibleMoves.size();i++) {
				
				int x1 = Integer.parseInt(Character.toString(PossibleMoves.get(i).charAt(0)));
				int y1 = Integer.parseInt(Character.toString(PossibleMoves.get(i).charAt(1)));
				int x2 = Integer.parseInt(Character.toString(PossibleMoves.get(i).charAt(2)));
				int y2 = Integer.parseInt(Character.toString(PossibleMoves.get(i).charAt(3)));
				
 				victim = makeMove(x1, y1, x2, y2, 9, 9,color);
				table.setFromMove(PossibleMoves.get(i));
				possibleBoards.add(table);
				
				table = (Board)tmpBoard.clone();
							
			}
			
			table = tmpBoard;
			
			return possibleBoards;
		}
		
		
		private int simulateGame(Node node,int color) {
			
			Board tmpTable = null;
			tmpTable = (Board) this.table.clone();
			int depth = 50;
			int player_color = color;
			int index;
			int reward = 0;
			String move = null;
			
			this.whitePieces = 10;
			this.blackPieces = 10;
			this.whitePoints = 0;
			this.blackPoints = 0;
			
			
			this.table = (Board) node.board.clone();
			
			for(int i = 0;i<depth;i++) {
				
				this.availableMoves = new ArrayList<String>();
				
				if(player_color == 0)
					this.whiteMoves();
				else 
					this.blackMoves();
				
				
				if(this.availableMoves.size() == 0)
					break;
				
				ArrayList<String> PossibleMoves = this.availableMoves;
				Random ran = new Random();

				index = ran.nextInt(PossibleMoves.size());
				
				move = PossibleMoves.get(index);
				
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
				
				String victim = makeMove(x1, y1, x2, y2, 9, 9,player_color);
				
				if(player_color == 0)
					player_color = 1;
				else 
					player_color = 0;
			
			
			}
			
			int weightPawn = 1;
			int weightRook = 3;
			int weightKing = 8;
			int valueOfWhitePieces = this.numberOfWhitePawns*weightPawn + this.numberOfWhiteRooks*weightRook + this.numberOfWhiteKings*weightKing;
			int valueOfBlackPieces = this.numberOfBlackPawns*weightPawn + this.numberOfBlackRooks*weightRook + this.numberOfBlackKings*weightKing;
			
			if(color == 0)
				reward = (valueOfWhitePieces + this.whitePoints) - (valueOfBlackPieces + this.blackPoints);
			else
				reward = (valueOfBlackPieces + this.blackPoints) - (valueOfWhitePieces + this.whitePoints);
			
			this.table = tmpTable;
			
			return reward;
		}
		
		
/*		private int EvaluateReward(Board table,int color) {
			
			String firstLetter = null;
			String secondLetter = null;
			int reward_black_player = 0;
			int reward_white_player = 0;
			int wpieces = 0;
			int bpieces = 0;
			
			for(int i=0; i<table.rows; i++)
			{
				for(int j=0; j<table.columns; j++)
				{
					
					
					firstLetter = Character.toString(table.board[i][j].charAt(0));
					
					if(table.board[i][j].length() == 2)
						secondLetter = Character.toString(table.board[i][j].charAt(1));
					else
						secondLetter = "";
				
				if(firstLetter.equals("W")) {
					if(secondLetter.equals("P"))
						reward_white_player += 1;
					else if(secondLetter.equals("R"))
						reward_white_player += 3;
					else if(secondLetter.equals("K"))
						reward_white_player += 8;
					else
						reward_white_player += 0;
					
					wpieces += 1;
				}
				else if(firstLetter.equals("B")) {
					if(secondLetter.equals("P"))
						reward_black_player += 1;
					else if(secondLetter.equals("R"))
						reward_black_player += 3;
					else if(secondLetter.equals("K"))
						reward_black_player += 8;
					else
						reward_black_player += 0;
					
					bpieces += 1;
					
				}
					
				}
				
			}
			
			if(color == 0)
				return (reward_white_player + wpieces) - (reward_black_player + bpieces);
			else
				return (reward_black_player + bpieces) - (reward_white_player + wpieces);
			
		}*/
		
		
		private void backpropagate(Node bottomNode, int reward) {
			
			Node tmp = bottomNode;
			
			while(tmp != null) {
				
				tmp.numberOfVisits++;
				
				if(tmp.color == this.myColor)
					tmp.reward += reward;

				tmp = tmp.pred;
				
			}
		}
		
		private boolean defendKing(int color) {
			boolean isThreatened =  false;
			
		if(color == 0) {	
					
		//Check if any opponet's pawn threats white king	
		if(this.whiteKingX + 1 <= 6 && this.whiteKingX +1 >= 0 && this.whiteKingY -1 <= 4 && this.whiteKingY -1 >= 0 && table.board[this.whiteKingX + 1][this.whiteKingY -1].equals("WP"))
			isThreatened = true;
		else if(this.whiteKingX + 1 <= 6 && this.whiteKingX +1 >= 0 && this.whiteKingY +1 <= 4 && this.whiteKingY +1 >= 0 && table.board[this.whiteKingX + 1][this.whiteKingY +1].equals("WP"))
			isThreatened = true;
		//Check if any opponet's king threats white king
		if(this.whiteKingX + 1 <= 6 && this.whiteKingX +1 >= 0 && this.whiteKingY +1 <= 4 && this.whiteKingY +1 >= 0 && table.board[this.whiteKingX + 1][this.whiteKingY + 1].equals("WK"))
			isThreatened = true;
		else if(this.whiteKingX - 1 <= 6 && this.whiteKingX -1 >= 0 && this.whiteKingY -1 <= 4 && this.whiteKingY -1 >= 0 && table.board[this.whiteKingX - 1][this.whiteKingY -1].equals("WK"))
			isThreatened = true;
		else if(this.whiteKingX + 1 <= 6 && this.whiteKingX + 1 >= 0 && this.whiteKingY -1 <= 4 && this.whiteKingY -1 >= 0 && table.board[this.whiteKingX + 1][this.whiteKingY -1].equals("WK"))
			isThreatened = true;
		else if(this.whiteKingX - 1 <= 6 && this.whiteKingX -1 >= 0 && this.whiteKingY +1 <= 4 && this.whiteKingY +1 >= 0 && table.board[this.whiteKingX - 1][this.whiteKingY +1].equals("WK"))
			isThreatened = true;
		else if(this.whiteKingY - 1 <=  4 && this.whiteKingY - 1 >= 0 && table.board[this.whiteKingX ][this.whiteKingY - 1].equals("WK"))
			isThreatened = true;
		else if(this.whiteKingY + 1 <=  4 && this.whiteKingY + 1 >= 0 && table.board[this.whiteKingX ][this.whiteKingY + 1].equals("WK"))
			isThreatened = true;	
		else if(this.whiteKingX - 1 <=  6 && this.whiteKingX - 1 >= 0 && table.board[this.whiteKingX - 1][this.whiteKingY].equals("WK"))
			isThreatened = true;
		else if(this.whiteKingX + 1 <=  6 && this.whiteKingX + 1 >= 0 && table.board[this.whiteKingX + 1][this.whiteKingY].equals("WK"))
			isThreatened = true;
		//Check if any opponet's rook threats white king
		for(int i = 1;i <= 3;i++) {
			if(this.whiteKingX + i <=  6 && this.whiteKingX +i >= 0 && table.board[this.whiteKingX + i][this.whiteKingY ].equals("WR"))
			{
				isThreatened = true;
				break;
			}
			else if(this.whiteKingY - i <=  4 && this.whiteKingY - i >= 0 && table.board[this.whiteKingX ][this.whiteKingY -i].equals("WR"))
			{
				isThreatened = true;
				break;
			}
			else if(this.whiteKingX - i <= 6 && this.whiteKingX - i >= 0  && table.board[this.whiteKingX-i][this.whiteKingY ].equals("WR") )
			{
				isThreatened = true;
				break;
			}
			else if(this.whiteKingY + i <= 4 && this.whiteKingY + i >= 0 && table.board[this.whiteKingX ][this.whiteKingY + i].equals("WR"))
			{
				isThreatened = true;
				break;
			}
		
		}
		
		}
		else {
			
			//Check if any opponet's pawn threats black king
			
			if(this.blackKingX + 1 <= 6 && this.blackKingX +1 >= 0 && this.blackKingY -1 <= 4 && this.blackKingY -1 >= 0 && table.board[this.blackKingX + 1][this.blackKingY -1].equals("BP"))
				isThreatened = true;
			if(this.blackKingX + 1 <= 6 && this.blackKingX +1 >= 0 && this.blackKingY +1 <= 4 && this.blackKingY +1 >= 0 && table.board[this.blackKingX + 1][this.blackKingY +1].equals("BP"))
				isThreatened = true;
			
			//Check if any opponet's king threats Black king
			if(this.blackKingX + 1 <= 6 && this.blackKingX +1 >= 0 && this.blackKingY +1 <= 4 && this.blackKingY +1 >= 0 && table.board[this.blackKingX + 1][this.blackKingY + 1].equals("BK"))
				isThreatened = true;
			else if(this.blackKingX + 1 <= 6 && this.blackKingX +1 >= 0 && this.blackKingY -1 <= 4 && this.blackKingY -1 >= 0 && table.board[this.blackKingX +1][this.blackKingY -1].equals("BK"))
				isThreatened = true;
			else if(this.blackKingX - 1 <= 6 && this.blackKingX - 1 >= 0 && this.blackKingY -1 <= 4 && this.blackKingY -1 >= 0 && table.board[this.blackKingX - 1][this.blackKingY -1].equals("BK"))
				isThreatened = true;
			else if(this.blackKingX - 1 <= 6 && this.blackKingX -1 >= 0 && this.blackKingY +1 <= 4 && this.blackKingY +1 >= 0 && table.board[this.blackKingX - 1][this.blackKingY +1].equals("BK"))
				isThreatened = true;
			else if(this.blackKingY - 1 <=  4 && this.blackKingY - 1 >= 0 && table.board[this.blackKingX ][this.blackKingY - 1].equals("BK"))
				isThreatened = true;
			else if(this.blackKingY + 1 <=  4 && this.blackKingY + 1 >= 0 && table.board[this.blackKingX ][this.blackKingY + 1].equals("BK"))
				isThreatened = true;	
			else if(this.blackKingX - 1 <=  6 && this.blackKingX - 1 >= 0 && table.board[this.blackKingX - 1][this.blackKingY].equals("BK"))
				isThreatened = true;
			else if(this.blackKingX + 1 <=  6 && this.blackKingX + 1 >= 0 && table.board[this.blackKingX + 1][this.blackKingY].equals("BK"))
				isThreatened = true;
			
			//Check if any opponet's rook threats black king
			for(int i =1;i <= 3;i++) {
				if(this.blackKingX + i <=  6 && this.blackKingX + i >= 0 && table.board[this.blackKingX + i][this.blackKingY ].equals("BR"))
				{
					isThreatened = true;
					break;
				}
				else if(this.blackKingY - i <=  4 && this.blackKingY - i >= 0 && table.board[this.blackKingX ][this.blackKingY - i].equals("BR"))
				{
					isThreatened = true;
					break;
				}
				else if(this.blackKingX - i <= 6 && this.blackKingX - i >= 0  && table.board[this.blackKingX - i][this.blackKingY ].equals("BR") )
				{
					isThreatened = true;
					break;
				}
				else if(this.blackKingY + i <= 4 && this.blackKingY + i >= 0 && table.board[this.blackKingX ][this.blackKingY + i].equals("BR"))
				{
					isThreatened = true;
					break;
				}
			
			}
			
		}
		
			
			return isThreatened;
		}
		
		private void sortMoves(ArrayList<String> moves){
			
			int x2 = 0;
			int y2 = 0;
			
			if(moves == null)
				return;
			
			ArrayList<SimpleImmutableEntry<Integer, String>> priorities = new ArrayList<SimpleImmutableEntry<Integer, String>>();
			
			for(int i = 0; i < moves.size(); i++) {
				x2 = Integer.parseInt(Character.toString(moves.get(i).charAt(2)));
				y2 = Integer.parseInt(Character.toString(moves.get(i).charAt(3)));
				
				if(table.board[x2][y2] == " ")
					priorities.add(new SimpleImmutableEntry<Integer, String>(4, moves.get(i)));
				else if(table.board[x2][y2] == "P")//Presents are last in priority
					priorities.add(new SimpleImmutableEntry<Integer, String>(3, moves.get(i))); 
				else if(Character.toString(table.board[x2][y2].charAt(1)) == "P")// Pawns are third most important
					priorities.add(new SimpleImmutableEntry<Integer, String>(2, moves.get(i)));
				else if(Character.toString(table.board[x2][y2].charAt(1)) == "R")// Rooks are second most important
					priorities.add(new SimpleImmutableEntry<Integer, String>(1, moves.get(i)));
				else if(Character.toString(table.board[x2][y2].charAt(1)) == "K")// Kings are the most important
					priorities.add(new SimpleImmutableEntry<Integer, String>(0, moves.get(i)));
				else
					priorities.add(new SimpleImmutableEntry<Integer, String>(4, moves.get(i)));
			}
			
			Collections.sort(priorities, new Comparator<SimpleImmutableEntry<Integer, String>>(){
			    @Override
			    public int compare(final SimpleImmutableEntry<Integer, String> o1, final SimpleImmutableEntry<Integer, String> o2) {
			        if(o1.getKey() > o2.getKey())
			        	return -1;
			        else if(o1.getKey() == o2.getKey())
			        	return 0;
			        else
			        	return 1;
			    }
			});
			
			
			for(int j = 0; j < moves.size(); j++) {
				moves.set(j, priorities.get(j).getValue());
			}
			
		}
		
		public static void main(String[] args) {
			
			String move = "";
			World world = new World();
			//world.MCTSTree.root.color = 1;
			
			world.miniMax(4, Integer.MIN_VALUE, Integer.MAX_VALUE, world.myColor, true);
			
			world.MCTSTree.root.board = world.table;
			world.MCTS(world.MCTSTree.root);
			move = world.finMove;
			int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
			int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
			int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
			int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
			world.makeMove(x1, y1, x2, y2, 9, 9, world.myColor);
			System.out.println("Next move: "+move);
			
			world.MCTSTree.root.board = world.table;
			world.MCTSTree.root.children = null;
			world.MCTS(world.MCTSTree.root);
			move = world.finMove;			
			System.out.println("Next move: "+move);
		}
		
		
	
}
