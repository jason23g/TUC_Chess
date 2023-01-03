
public class Controller
{
	private Board board = null;
	private int rows = 0;
	private int columns = 0;
	private int nKings = 2;
	private int nChessParts = 20;
	private int white = 0;
	private int black = 1;
	private long startingTime = 0;
	private double timeLimit = 14.0; // minutes
	
	public Controller(Board board)
	{
		this.board = board;
		this.rows = this.board.getRows();
		this.columns = this.board.getColumns();
		this.startingTime = System.nanoTime();
	}
	
	public boolean checkLegal(String msg, int player)
	{
		int x1 = Integer.parseInt(Character.toString(msg.charAt(0)));
		int y1 = Integer.parseInt(Character.toString(msg.charAt(1)));
		int x2 = Integer.parseInt(Character.toString(msg.charAt(2)));
		int y2 = Integer.parseInt(Character.toString(msg.charAt(3)));
		
		// check the physical constraints
		if(x1>(rows-1) || x1<0)
			return false;
		else if(x2>(rows-1) || x2<0)
			return false;
		else if(y1>(columns-1) || y1<0)
			return false;
		else if(y2>(columns-1) || y2<0)
			return false;
		
				
		// check if the starting point is empty
		if(board.getChessLabel()[x1][y1].getEmpty())
			return false;
			
		// check if the chess part at the starting belongs to the proper player
		int legalPlayer = board.getChessLabel()[x1][y1].getCp().getPlayer();
		
		if(legalPlayer != player)
			return false;
		
		ChessPart cp = board.getChessLabel()[x1][y1].getCp();
		
		if(legalPlayer == white)		// it is the white player who sent the action
		{
			if(cp instanceof Pawn)
				return checkWhitePawn(x1,y1,x2,y2);
			else if(cp instanceof Rook)
				return checkWhiteRook(x1,y1,x2,y2);
			else
				return checkWhiteKing(x1,y1,x2,y2);
		}
		else	// it is the black player who sent the action
		{
			if(cp instanceof Pawn)
				return checkBlackPawn(x1,y1,x2,y2);
			else if(cp instanceof Rook)
				return checkBlackRook(x1,y1,x2,y2);
			else
				return checkBlackKing(x1,y1,x2,y2);
		}
		
	}
	
	private boolean checkWhitePawn(int x1, int y1, int x2, int y2)
	{
		
		// check if the move is towards the last row
		if(x1==1 && x2==0 && y1==y2 && board.getChessLabel()[x2][y2].getEmpty())
		{
			this.checkPrize(x2, y2, white);
			this.addPoints(white, 1);
			board.getChessLabel()[x1][y1].removeCp();
			nChessParts--;
			return true;
		}
		
		// check if it can move one vertical position ahead
		if( (x1 == (x2+1)) && (y1==y2) && (board.getChessLabel()[x2][y2].getEmpty()) )
		{
			this.checkPrize(x2, y2, white);
			board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
			board.getChessLabel()[x1][y1].removeCp();
			return true;
		}
		
		// check if it can check if it can move crosswise to the left (cannot move on a bonus)
		if( (x1 == (x2+1)) && (y1 == (y2+1)) && (!board.getChessLabel()[x2][y2].getEmpty()) 
			 && (board.getChessLabel()[x2][y2].getCp().getPlayer() == black) )
		{
			int points = board.getChessLabel()[x2][y2].getCp().getPoints();
			this.addPoints(white, points);
			
			if(board.getChessLabel()[x2][y2].getCp() instanceof King)
				nKings--;
			else
				nChessParts--;
			
			// check also if the move is towards the last row
			if(x2==0)
			{
				this.addPoints(white, 1);
				board.getChessLabel()[x1][y1].removeCp();
				board.getChessLabel()[x2][y2].removeCp();
				return true;
			}
			else
			{
				board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
				board.getChessLabel()[x1][y1].removeCp();
				return true;			
			}	
		}
		
		// check if it can move crosswise to the right (cannot move on a bonus)
		if( (x1 == (x2+1)) && (y1 == (y2-1)) && (!board.getChessLabel()[x2][y2].getEmpty())  
			 && (board.getChessLabel()[x2][y2].getCp().getPlayer() == black) )
		{
			int points = board.getChessLabel()[x2][y2].getCp().getPoints();
			this.addPoints(white, points);
			
			if(board.getChessLabel()[x2][y2].getCp() instanceof King)
				nKings--;
			else
				nChessParts--;
			
			// check also if the move is towards the last row
			if(x2==0)
			{
				this.addPoints(white, 1);
				board.getChessLabel()[x1][y1].removeCp();
				board.getChessLabel()[x2][y2].removeCp();
				return true;
			}
			else
			{
				board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
				board.getChessLabel()[x1][y1].removeCp();
				return true;			
			}	
		}
		
		return false;
	}
	
	private boolean checkWhiteRook(int x1, int y1, int x2, int y2)
	{
		int rookBlocks = Rook.getRookBlocks();
		
		// check if it can move upwards
		for(int i=0; i<rookBlocks; i++)
		{
			if((x1-(i+1)) < 0)
				break;
			
			// if there is a white chess part ahead do not keep on iterating
			if(!board.getChessLabel()[x1-(i+1)][y1].getEmpty() 
				&& board.getChessLabel()[x1-(i+1)][y1].getCp().getPlayer() == white)
			{
				break;
			}
			
			if( (x1-(i+1) == x2) && (y1==y2) )
			{	
				if( (!board.getChessLabel()[x2][y2].getEmpty()) 
					&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == black) )
				{
					int points = board.getChessLabel()[x2][y2].getCp().getPoints();
					this.addPoints(white, points);
					
					if(board.getChessLabel()[x2][y2].getCp() instanceof King)
						nKings--;
					else
						nChessParts--;
					
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else if(board.getChessLabel()[x2][y2].getEmpty())
				{
					this.checkPrize(x2, y2, white);
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else
				{
					return false;
				}
			}
			
			// in case that there are two chess parts ahead it is not legal to attack the second now
			// by detouring the first one
			if( (x1-(i+2) >= 0) && (!board.getChessLabel()[x1-(i+1)][y1].getEmpty()
								     || board.getChessLabel()[x1-(i+1)][y1].getHasPrize()) )
				break;
			
		}
		
		// check if it can move downwards
		for(int i=0; i<rookBlocks; i++)
		{
			if((x1+(i+1)) == rows)
				break;
			
			// if there is a white chess part ahead do not keep on iterating
			if(!board.getChessLabel()[x1+(i+1)][y1].getEmpty() 
				&& board.getChessLabel()[x1+(i+1)][y1].getCp().getPlayer() == white)
			{
				break;
			}
			
			if( (x1+(i+1) == x2) && (y1==y2) )
			{
				if( (!board.getChessLabel()[x2][y2].getEmpty())
					&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == black) )
				{
					int points = board.getChessLabel()[x2][y2].getCp().getPoints();
					this.addPoints(white, points);
					
					if(board.getChessLabel()[x2][y2].getCp() instanceof King)
						nKings--;
					else
						nChessParts--;
					
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else if(board.getChessLabel()[x2][y2].getEmpty())
				{
					this.checkPrize(x2, y2, white);
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else
				{
					return false;
				}
			}
			
			// in case that there are two chess parts ahead it is not legal to attack the second
			// by detouring the first
			if( (x1+(i+2) < rows) && (!board.getChessLabel()[x1+(i+1)][y1].getEmpty()
									  || board.getChessLabel()[x1+(i+1)][y1].getHasPrize()) )
				break;
			
		}
		
		// check if it can move on the left
		for(int i=0; i<rookBlocks; i++)
		{
			if((y1-(i+1)) < 0)
				break;
			
			// if there is a white chess part ahead do not keep on iterating
			if(!board.getChessLabel()[x1][y1-(i+1)].getEmpty() 
				 && board.getChessLabel()[x1][y1-(i+1)].getCp().getPlayer() == white)
			{
				break;
			}
			
			if( (x1==x2) && ((y1-(i+1)) == y2))
			{
				if( (!board.getChessLabel()[x2][y2].getEmpty()) 
					&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == black) )
				{
					int points = board.getChessLabel()[x2][y2].getCp().getPoints();
					this.addPoints(white, points);
					
					if(board.getChessLabel()[x2][y2].getCp() instanceof King)
						nKings--;
					else
						nChessParts--;
					
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else if(board.getChessLabel()[x2][y2].getEmpty())
				{
					this.checkPrize(x2, y2, white);
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else
				{
					return false;
				}
			}
			
			// in case that there are two chess parts ahead it is not legal to attack the second now
			// by detouring the first one
			if( (y1-(i+2) >= 0) && (!board.getChessLabel()[x1][y1-(i+1)].getEmpty()
									 || board.getChessLabel()[x1][y1-(i+1)].getHasPrize()) )
				break;

		}
		
		// check if it can move on the right
		for(int i=0; i<rookBlocks; i++)
		{
			if((y1+(i+1)) > (columns-1))
				break;
			
			// if there is a white chess part ahead do not keep on iterating
			if(!board.getChessLabel()[x1][y1+(i+1)].getEmpty() 
				&& board.getChessLabel()[x1][y1+(i+1)].getCp().getPlayer() == white)
			{
				break;
			}
			
			if( (x1==x2) && ((y1+(i+1)) == y2))
			{
				if( (!board.getChessLabel()[x2][y2].getEmpty()) 
					&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == black) )
				{
					int points = board.getChessLabel()[x2][y2].getCp().getPoints();
					this.addPoints(white, points);
					
					if(board.getChessLabel()[x2][y2].getCp() instanceof King)
						nKings--;
					else
						nChessParts--;
					
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else if(board.getChessLabel()[x2][y2].getEmpty())
				{
					this.checkPrize(x2, y2, white);
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else
				{
					return false;
				}
			}
			
			// in case that there are two chess parts ahead it is not legal to attack the second now
			// by detouring the first one
			if( (y1+(i+2) < columns) && (!board.getChessLabel()[x1][y1+(i+1)].getEmpty()
										  || board.getChessLabel()[x1][y1+(i+1)].getHasPrize()) )
				break;
			
		}
		
		return false;
	}
		
	private boolean checkWhiteKing(int x1, int y1, int x2, int y2)
	{
		boolean valid = false;
		
		if( ((x1-1) == x2 ) && y2==y1 )			// check if it can move upwards
			valid = true;
		else if ( ((x1+1) == x2 ) && y2==y1 )	// check if it can move downwards
			valid = true;
		else if( x1==x2 && ((y1-1) == y2) ) // check if it can move on the left
			valid = true;
		else if( x1==x2 && ((y1+1) == y2) ) // check if it can move on the right
			valid = true;
		
		if(valid)
		{
			if( (!board.getChessLabel()[x2][y2].getEmpty()) 
				&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == black) )
			{
				int points = board.getChessLabel()[x2][y2].getCp().getPoints();
				this.addPoints(white, points);
				
				if(board.getChessLabel()[x2][y2].getCp() instanceof King)
					nKings--;
				else
					nChessParts--;
				
				board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
				board.getChessLabel()[x1][y1].removeCp();
				return true;				
			}
			else if(board.getChessLabel()[x2][y2].getEmpty())
			{
				this.checkPrize(x2, y2, white);
				board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
				board.getChessLabel()[x1][y1].removeCp();
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkBlackPawn(int x1, int y1, int x2, int y2)
	{
		// check if the move is beyond the last row
		if(x1==rows-2 && x2==rows-1 && board.getChessLabel()[x2][y2].getEmpty())
		{
			this.checkPrize(x2, y2, black);
			this.addPoints(black,1);
			board.getChessLabel()[x1][y1].removeCp();
			nChessParts--;
			return true;
		}
		
		// check if it can move one vertical position ahead
		if( (x1 == (x2-1)) && (y2==y1) && (board.getChessLabel()[x2][y2].getEmpty()) )
		{
			this.checkPrize(x2, y2, black);
			board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
			board.getChessLabel()[x1][y1].removeCp();
			return true;
		}
		
		// check if it can check if it can move crosswise to the left
		if( (x1 == (x2-1)) && (y1 == (y2+1)) && (!board.getChessLabel()[x2][y2].getEmpty()) 
			 && (board.getChessLabel()[x2][y2].getCp().getPlayer() == white) )
		{
			int points = board.getChessLabel()[x2][y2].getCp().getPoints();
			this.addPoints(black, points);
			
			if(board.getChessLabel()[x2][y2].getCp() instanceof King)
				nKings--;
			else
				nChessParts--;
			
			// check also if the move is towards the last row
			if(x2==rows-1)
			{
				this.addPoints(white, 1);
				board.getChessLabel()[x1][y1].removeCp();
				board.getChessLabel()[x2][y2].removeCp();
				return true;
			}
			else
			{
				board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
				board.getChessLabel()[x1][y1].removeCp();
				return true;			
			}	
		}
		
		// check if it can move crosswise to the right
		if( (x1 == (x2-1)) && (y1 == (y2-1)) && (!board.getChessLabel()[x2][y2].getEmpty())  
			 && (board.getChessLabel()[x2][y2].getCp().getPlayer() == white) )
		{
			int points = board.getChessLabel()[x2][y2].getCp().getPoints();
			this.addPoints(black, points);
			
			if(board.getChessLabel()[x2][y2].getCp() instanceof King)
				nKings--;
			else
				nChessParts--;
			
			// check also if the move is towards the last row
			if(x2==rows-1)
			{
				this.addPoints(white, 1);
				board.getChessLabel()[x1][y1].removeCp();
				board.getChessLabel()[x2][y2].removeCp();
				return true;
			}
			else
			{
				board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
				board.getChessLabel()[x1][y1].removeCp();
				return true;			
			}
		}
		
		return false;		
	}
	
	private boolean checkBlackRook(int x1, int y1, int x2, int y2)
	{
		int rookBlocks = Rook.getRookBlocks();
		
		// check if it can move upwards
		for(int i=0; i<rookBlocks; i++)
		{
			if((x1-(i+1)) < 0)
				break;
			
			// if there is a black chess part ahead do not keep on iterating
			if(!board.getChessLabel()[x1-(i+1)][y1].getEmpty() 
				&& board.getChessLabel()[x1-(i+1)][y1].getCp().getPlayer() == black)
			{	
				break;
			}
			
			if( (x1-(i+1) == x2) && y1==y2 )
			{
				if( (!board.getChessLabel()[x2][y2].getEmpty()) 
					&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == white) )
				{
					int points = board.getChessLabel()[x2][y2].getCp().getPoints();
					this.addPoints(black, points);
					
					if(board.getChessLabel()[x2][y2].getCp() instanceof King)
						nKings--;
					else
						nChessParts--;
					
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else if(board.getChessLabel()[x2][y2].getEmpty())
				{
					this.checkPrize(x2, y2, black);
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else
				{
					return false;
				}
			}
			
			// in case that there are two chess parts ahead it is not legal to attack the second now
			// by detouring the first one
			if( (x1-(i+2) >= 0) && (!board.getChessLabel()[x1-(i+1)][y1].getEmpty()
								     || board.getChessLabel()[x1-(i+1)][y1].getHasPrize()) )
				break;
		}
		
		// check if it can move downwards
		for(int i=0; i<rookBlocks; i++)
		{
			if((x1+(i+1)) > (rows-1))
				break;
			
			// if there is a black chess part ahead do not keep on iterating
			if(!board.getChessLabel()[x1+(i+1)][y1].getEmpty() 
					&& board.getChessLabel()[x1+(i+1)][y1].getCp().getPlayer() == black)
			{	
					break;
			}
			
			if( (x1+(i+1) == x2) && (y1==y2) )
			{
				if( (!board.getChessLabel()[x2][y2].getEmpty()) 
						&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == white) )
				{
					int points = board.getChessLabel()[x2][y2].getCp().getPoints();
					this.addPoints(black, points);
					
					if(board.getChessLabel()[x2][y2].getCp() instanceof King)
						nKings--;
					else
						nChessParts--;
					
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else if(board.getChessLabel()[x2][y2].getEmpty())
				{
					this.checkPrize(x2, y2, black);
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else
				{
					return false;
				}
			}
			
			// in case that there are two chess parts ahead it is not legal to attack the second now
			// by detouring the first one
			if( (x1+(i+2) < rows) && (!board.getChessLabel()[x1+(i+1)][y1].getEmpty()
									   || board.getChessLabel()[x1+(i+1)][y1].getHasPrize()) )
				break;
			
		}
		
		// check if it can move on the left
		for(int i=0; i<rookBlocks; i++)
		{
			if((y1-(i+1)) < 0)
				break;
			
			// if there is a black chess part ahead do not keep on iterating
			if(!board.getChessLabel()[x1][y1-(i+1)].getEmpty() 
					&& board.getChessLabel()[x1][y1-(i+1)].getCp().getPlayer() == black)
			{	
				break;
			}
			
			if( (x1==x2) && ((y1-(i+1)) == y2))
			{
				if( (!board.getChessLabel()[x2][y2].getEmpty()) 
						&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == white) )
				{
					int points = board.getChessLabel()[x2][y2].getCp().getPoints();
					this.addPoints(black, points);
					
					if(board.getChessLabel()[x2][y2].getCp() instanceof King)
						nKings--;
					else
						nChessParts--;
					
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else if(board.getChessLabel()[x2][y2].getEmpty())
				{
					this.checkPrize(x2, y2, black);
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else
				{
					return false;
				}
			}	
			
			// in case that there are two chess parts ahead it is not legal to attack the second now
			// by detouring the first one
			if( (y1-(i+2) >= 0) && (!board.getChessLabel()[x1][y1-(i+1)].getEmpty()
		                             || board.getChessLabel()[x1][y1-(i+1)].getHasPrize()) )
				break;
			
		}
		
		// check if it can move on the right
		for(int i=0; i<rookBlocks; i++)
		{
			if((y1+(i+1)) == columns)
				break;
			
			// if there is a black chess part ahead do not keep on iterating
			if(!board.getChessLabel()[x1][y1+(i+1)].getEmpty() 
					&& board.getChessLabel()[x1][y1+(i+1)].getCp().getPlayer() == black)
			{	
				break;
			}
			
			if( (x1==x2) && ((y1+(i+1)) == y2))
			{
				if( (!board.getChessLabel()[x2][y2].getEmpty()) 
					&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == white) )
				{
					int points = board.getChessLabel()[x2][y2].getCp().getPoints();
					this.addPoints(black, points);
					
					if(board.getChessLabel()[x2][y2].getCp() instanceof King)
						nKings--;
					else
						nChessParts--;
					
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else if(board.getChessLabel()[x2][y2].getEmpty())
				{
					this.checkPrize(x2, y2, black);
					board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
					board.getChessLabel()[x1][y1].removeCp();
					return true;
				}
				else
				{
					return false;
				}
			}	
			
			// in case that there are two chess parts ahead it is not legal to attack the second now
			// by detouring the first one
			if( (y1+(i+2) < columns) && (!board.getChessLabel()[x1][y1+(i+1)].getEmpty()
										  || board.getChessLabel()[x1][y1+(i+1)].getHasPrize()) )
				break;
						
		}
		
		return false;		
	}
	
	private boolean checkBlackKing(int x1, int y1, int x2, int y2)
	{
		boolean valid = false;
		
		if( ((x1-1) == x2 ) && y2==y1 )			// check if the move is upwards
			valid = true;
		else if ( ((x1+1) == x2) && y2==y1 )	// check if the move is downwards
			valid = true;
		else if( (x1 == x2) && ((y1-1) == y2) ) // check if move is on the left
			valid = true;
		else if( (x1 == x2) && ((y1+1) == y2) ) // check if move is on the right
			valid = true;
		
		if(valid)
		{
			if( (!board.getChessLabel()[x2][y2].getEmpty()) 
				&& (board.getChessLabel()[x2][y2].getCp().getPlayer() == white) )
			{
				int points = board.getChessLabel()[x2][y2].getCp().getPoints();
				this.addPoints(black, points);
				
				if(board.getChessLabel()[x2][y2].getCp() instanceof King)
					nKings--;
				else
					nChessParts--;
				
				board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
				board.getChessLabel()[x1][y1].removeCp();
				return true;				
			}
			else if(board.getChessLabel()[x2][y2].getEmpty())
			{
				this.checkPrize(x2, y2, black);
				board.getChessLabel()[x2][y2].setCp(board.getChessLabel()[x1][y1].getCp());
				board.getChessLabel()[x1][y1].removeCp();
				return true;
			}
		}
		
		return false;
	}
	
	private void addPoints(int player, int points)
	{
		// 0 == the white player, 1 == black player
		UDPServer.increaseScore(player, points);
	}
	
	public boolean checkEnd()
	{
		// if there is only one king in the game or two chessparts (=the two kings) the game has ended
		if(nKings==1 || nChessParts==2)
			return true;
		
		// check time limit
		long elapsedTime = System.nanoTime() - this.startingTime;
		double minutes = (double)elapsedTime / (1000000000.0 * 60);
		if(minutes > this.timeLimit)
			return true;
		
		return false;
	}
	
	private void checkPrize(int x, int y, int player)
	{
		if(board.getChessLabel()[x][y].getHasPrize())
		{
			this.addPoints(player, board.getChessLabel()[x][y].getPrize().getValue());
			board.getChessLabel()[x][y].removePrize();
		}
	}
	
	public double getTimeLimit()
	{
		return this.timeLimit;
	}

}
