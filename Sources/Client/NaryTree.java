package Client;
import java.util.ArrayList;

public class NaryTree {
	
	public Node root;
	public int numOfNodes = 0;
	
	public NaryTree(Board board, int player_color) {
		
		root = new Node(board);
		root.color = player_color;
		
	}
	

}
