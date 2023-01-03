package Client;
import java.util.ArrayList;

public class Node {
	
	public ArrayList<Node> children = null;
	public Node pred = null;
    public Board board;
    public int reward;
    public int numberOfVisits;
    public int color;
    

    public Node(Board board)
    {
 
        this.board = board;
        this.reward = 0;
        this.numberOfVisits = 0;
        this.color = -1;
      
        
    }

    public void add(Node n)
    {
        if (children == null) children = new ArrayList<>();
        children.add(n);
        n.pred = this;
        
        if(this.color == 0)
        	n.color = 1;
        else
        	n.color = 0;
    }
    
    
}
