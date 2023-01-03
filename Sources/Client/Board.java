package Client;

import java.util.Arrays;

public class Board implements Cloneable{

	protected String[][] board = null;
	public int rows = 7;
	public int columns = 5;
	//protected int color = 0;
	private String fromMove = "";
	
	public Board() {
		this.board = new String[rows][columns];
		
	}

	public String getFromMove() {
		return fromMove;
	}

	public void setFromMove(String fromMove) {
		this.fromMove = fromMove;
	}
	
	@Override
	public Object clone()
    {
		Board b = null;
		try {
			b = (Board) super.clone();
		}
		catch(CloneNotSupportedException e){
			b = new Board(); 
		}
		
		b.board = this.board.clone();
		
		for(int i = 0; i < rows; i++) {
			b.board[i] = Arrays.copyOf(this.board[i], this.board[i].length);
		}
		b.fromMove = new String();
		
		return b;
    }
	

}
