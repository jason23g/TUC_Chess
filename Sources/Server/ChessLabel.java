import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class ChessLabel extends JLabel
{
	private boolean empty = true;	// empty of a chess part, it may still contain a prize
	private boolean hasPrize = false;
	private ImageIcon standardIcon = null;
	private ChessPart cp = null;
	private Prize prize = null;
	private static final long serialVersionUID = 1L - 200;
	
	// empty constructor - auto generated
	
	public void setStandardIcon(ImageIcon standardImg)
	{
		this.standardIcon = standardImg;
	}
	
	public void resetImage()
	{
		this.setIcon(standardIcon);
	}
	
	public void setCp(ChessPart cp)
	{
		this.cp = cp;
		this.setIcon(this.cp.getIcon());
		this.empty = false;
	}
	
	public void removeCp()
	{
		this.cp = null;
		this.resetImage();
		this.empty = true;
	}
	
	public void setPrize(Prize prize)
	{
		this.prize = prize;
		this.setIcon(this.prize.getIcon());
		this.hasPrize = true;
	}
	
	public void removePrize()
	{
		this.prize = null;
		this.resetImage();
		this.hasPrize = false;
	}
	
	public ChessPart getCp()
	{
		return cp;
	}
	
	public boolean getEmpty()
	{
		return empty;
	}
	
	public Prize getPrize()
	{
		return prize;
	}
	
	public boolean getHasPrize()
	{
		return hasPrize;
	}
	
}
