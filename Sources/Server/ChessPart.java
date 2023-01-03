import javax.swing.ImageIcon;


public abstract class ChessPart
{
	protected int player = 0;	// 0 is for the white player and 1 for the black one
	protected ImageIcon icon = null;
	protected int pointsWorth = 0;
	
	public ChessPart(int player, ImageIcon icon)
	{
		this.player = player;
		this.icon = icon;
	}
	
	public int getPlayer()
	{
		return player;
	}
	
	public ImageIcon getIcon()
	{
		return icon;
	}
	
	public int getPoints()
	{
		return pointsWorth;
	}
}
