import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Screen extends JFrame
{
	JTextArea ta = null;
	JScrollPane sp = null;
	private static final long serialVersionUID = 1L-100;
	
	public Screen(double timeLimit)
	{
		ta = new JTextArea("Welcome to TUC-Chess");
		this.print("Time limit: " + timeLimit + " minutes");
		ta.setBackground(Color.WHITE);
		ta.setEditable(false);
		
		sp = new JScrollPane();
		sp.setViewportView(ta);
		
		this.add(sp);
				
		this.setBounds(750, 100, 400, 500);
		ImageIcon img = new ImageIcon("chess/chessimage.jpg");
		this.setIconImage(img.getImage());
		this.setVisible(true);
		this.setTitle("TUC-Chess Screen");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void print(String s1)
	{
		ta.append("\n" + s1);
		
		int length = ta.getText().length();
		ta.setCaretPosition(length);
	}
	
}
