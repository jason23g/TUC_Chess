import javax.swing.ImageIcon;

public class Pawn extends ChessPart 
{
	
	public Pawn(int player, ImageIcon icon)
	{
		super(player, icon);
		this.pointsWorth = 1;
	}

}
