import javax.swing.ImageIcon;


public class Rook extends ChessPart
{
	private static int rookBlocks = 3;
	
	public Rook(int player, ImageIcon icon)
	{
		super(player, icon);
		this.pointsWorth = 3;
	}
	
	public static int getRookBlocks()
	{
		return rookBlocks;
	}

}
