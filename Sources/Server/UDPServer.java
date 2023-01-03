import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;

/*
   Author : Michalis Mamakos   
   UDPServer : UDPServer class is used as the server of TUC-Chess
 */

public class UDPServer
{
	private static final int PORT = 9876;
	private DatagramSocket serverSocket = null;
	private byte[] receiveData  = null;
	private byte[] sendData = null;
	private int size = 200;
	private DatagramPacket receivePacket = null;
	private DatagramPacket sendPacket = null;
	private static InetAddress IPWhite = null;
	private static InetAddress IPBlack = null;
	private static int PORTWhite = 0;
	private static int PORTBlack = 0;
	private int counterMsg = 0;
	private Board board = null;
	private Controller controller = null;
	private Screen screen = null;
	private String nameWhite = "";
	private String nameBlack = "";
	public static int scoreWhite = 0;
	public static int scoreBlack = 0;
	private final String filename = "scores.log";
	private static boolean closeOnTermination = false;
	
	public UDPServer()
	{
		// initialization of the fields
		try
		{
			serverSocket = new DatagramSocket(PORT);
			
			receiveData = new byte[size];
			sendData = new byte[size];
			
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			sendPacket = new DatagramPacket(sendData, sendData.length);
		}
		catch(SocketException e)
		{
			// print the occured exception
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
		
		// create the board
		board = new Board();
		
		// create the controller
		controller = new Controller(board);
		
		// create the screen
		screen = new Screen(controller.getTimeLimit());
	}
	
	private void receivePlayers()
	{
		try
		{
			screen.print("Waiting for the first player to join...");
			
			// waiting for a message from the first player
			serverSocket.receive(receivePacket);
			nameWhite = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			IPWhite = receivePacket.getAddress();
			PORTWhite = receivePacket.getPort();
			counterMsg++;
			
			// informing the player that s/he is the white one
			String s1 = "PW";
			sendData = s1.getBytes("UTF-8");
			sendPacket.setData(sendData);
			sendPacket.setLength(sendData.length);
			sendPacket.setAddress(IPWhite);
			sendPacket.setPort(PORTWhite);
			serverSocket.send(sendPacket);
			
			screen.print(nameWhite + " is the white player.");
			
			screen.print("Waiting for the second player to join...");
			
			// waiting for a message from the second player
			serverSocket.receive(receivePacket);
			nameBlack = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			IPBlack = receivePacket.getAddress();
			PORTBlack = receivePacket.getPort();
			counterMsg++;
			
			// informing the player that s/he is the black one
			s1 = "PB";
			sendData = s1.getBytes("UTF-8");
			sendPacket.setData(sendData);
			sendPacket.setLength(sendData.length);
			sendPacket.setAddress(IPBlack);
			sendPacket.setPort(PORTBlack);
			serverSocket.send(sendPacket);
			
			screen.print(nameBlack + " is the black player.");
			
			screen.print("Game starting in...");
			for(int i=3; i>=0; i--)
			{
				screen.print(""+i);
				
				// do not wait for a second if the index equals 0
				if(i==0)
					break;
				
				try
				{
					synchronized(this)
					{
						this.wait(1000);
					}
				}
				catch(InterruptedException e)
				{
					screen.print(e.getClass().getName() + " : " + e.getMessage());
				}
			}			
		}
		catch(IOException e)
		{
			screen.print(e.getClass().getName() + " : " + e.getMessage());
		}
	}
	
	private void sendBeginning()
	{
		String s1 = "GB";
		
		try
		{
			sendData = s1.getBytes("UTF-8");
			sendPacket.setData(sendData);
			sendPacket.setLength(sendData.length);
			
			// inform the white player that the game has begun
			sendPacket.setAddress(IPWhite);
			sendPacket.setPort(PORTWhite);
			serverSocket.send(sendPacket);
			
			// inform the black player that the game has begun
			sendPacket.setAddress(IPBlack);
			sendPacket.setPort(PORTBlack);
			serverSocket.send(sendPacket);
		}
		catch(IOException e)
		{
			screen.print(e.getClass().getName() + " : " + e.getMessage());
		}
		
		screen.print("The game has just begun!");
	}
	
	private void listening()
	{		
		InetAddress address = null;
		String msg = "";
		String s1 = "";
		
		while(true)
		{	
			try
			{
				// waiting for a message
				serverSocket.receive(receivePacket);
				msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
				address = receivePacket.getAddress();
				counterMsg++;
				
				// the received message must have a length of 4
				if(msg.length() != 4)
				{
					screen.print("Message of wrong length has been received.");
					break;
				}
				
				if(counterMsg%2 == 1)	// it is the white player's turn
				{	
					screen.print("Received message from " + nameWhite + " : " + msg);
					
					// check if the message was received from the proper player
					if(!address.equals(IPWhite))
					{
						screen.print("It was the white player's turn. Wrong message.");
						break;
					}
					
					// check if the move of the message is legal
					if(controller.checkLegal(msg, 0))
					{
						if(controller.checkEnd())	// the game has ended
						{
							s1 = "GE" + this.getScore();
							
							sendData = s1.getBytes("UTF-8");
							sendPacket.setData(sendData);
							sendPacket.setLength(sendData.length);
							
							// inform the white player that the game has ended
							sendPacket.setAddress(IPWhite);
							sendPacket.setPort(PORTWhite);
							serverSocket.send(sendPacket);
							
							// inform the black player that the game has ended
							sendPacket.setAddress(IPBlack);
							sendPacket.setPort(PORTBlack);
							serverSocket.send(sendPacket);
							
							this.printWinner();
							
							break;
						}
						else					// the game has not ended
						{
							// the next player to play is 1(=black)
							s1 = "T1" + msg;
							
							// add bonus to the message ("99" == no prize)
							s1 += board.generateBonus();
							
							// add score
							s1 += this.getScore();
							
							sendData = s1.getBytes("UTF-8");
							sendPacket.setData(sendData);
							sendPacket.setLength(sendData.length);
							
							// inform the white player that the move has been made
							sendPacket.setAddress(IPWhite);
							sendPacket.setPort(PORTWhite);
							serverSocket.send(sendPacket);
							
							// inform the black player that the move has been made
							sendPacket.setAddress(IPBlack);
							sendPacket.setPort(PORTBlack);
							serverSocket.send(sendPacket);
							
							this.printMove(msg, nameWhite);
						}	
					}
					else	// the move was illegal
					{
						screen.print("Illegal move by " + nameWhite);
						break;
					}
				}
				else	// it is the black player's turn
				{
					screen.print("Received message from " + nameBlack + " : " + msg);
					
					// check if the message was received from the proper player
					if(!address.equals(IPBlack))
					{
						screen.print("It was the black player's turn. Wrong message.");
						break;
					}
					
					// check if the move of the message is legal
					if(controller.checkLegal(msg, 1))
					{
						if(controller.checkEnd())	// the game has ended
						{
							s1 = "GE" + this.getScore();
							
							sendData = s1.getBytes("UTF-8");
							sendPacket.setData(sendData);
							sendPacket.setLength(sendData.length);
							
							// inform the white player that the game has ended
							sendPacket.setAddress(IPWhite);
							sendPacket.setPort(PORTWhite);
							serverSocket.send(sendPacket);
							
							// inform the black player that the game has ended
							sendPacket.setAddress(IPBlack);
							sendPacket.setPort(PORTBlack);
							serverSocket.send(sendPacket);	
							
							this.printWinner();
							
							break;
						}
						else						// the game has not ended
						{
							// the next player to play is 0(=white)
							s1 = "T0" + msg;
							
							// add bonus to the message ("99" == no prize)
							s1 += board.generateBonus();
							
							// add score
							s1 += this.getScore();
							
							sendData = s1.getBytes("UTF-8");
							sendPacket.setData(sendData);
							sendPacket.setLength(sendData.length);
							
							// inform the white player that the move has been made
							sendPacket.setAddress(IPWhite);
							sendPacket.setPort(PORTWhite);
							serverSocket.send(sendPacket);
							
							// inform the black player that the move has been made
							sendPacket.setAddress(IPBlack);
							sendPacket.setPort(PORTBlack);
							serverSocket.send(sendPacket);
							
							this.printMove(msg, nameBlack);
						}	
					}
					else
					{
						screen.print("Illegal move by " + nameBlack);
						break;
					}
				}
			}
			catch(IOException e)
			{
				screen.print(e.getClass().getName() + " : " + e.getMessage());
			}	
		}	
	}
	
	private String getScore()
	{
		String score = "";
		
		if(scoreWhite > 9)
			score += Integer.toString(scoreWhite);
		else
			score += "0" + Integer.toString(scoreWhite);
		
		if(scoreBlack > 9)
			score += Integer.toString(scoreBlack);
		else
			score += "0" + Integer.toString(scoreBlack);
		
		return score;
	}
	
	private void printWinner()
	{
		String score = this.getScore();
		
		if(scoreWhite > scoreBlack)
			screen.print(nameWhite + " is the winner! Score : " + score.substring(0, 2)+ "-" + score.substring(2,4));
		else if(scoreWhite < scoreBlack)
			screen.print(nameBlack + " is the winner! Score : " + score.substring(0, 2)+ "-" + score.substring(2,4));
		else
			screen.print("Draw! Score : " + score.substring(0, 2)+ "-" + score.substring(2,4));
		
		this.saveToFile();
	}
	
	private void saveToFile()
	{
		try
		{
			FileWriter fw = new FileWriter(this.filename, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			
			String s = this.nameWhite + " " + scoreWhite + " " 
						+ this.nameBlack + " " + scoreBlack + " " + LocalDateTime.now();

			out.println(s);
			out.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void printMove(String msg, String name)
	{
		String pos1 = msg.substring(0, 2);
		String pos2 = msg.substring(2, 4);
		
		screen.print(name + " has made a move from " + pos1 + " to " + pos2 +". Score : " + this.getScore().substring(0, 2)
						    + "-" + this.getScore().substring(2, 4));
	}
	
	public static void increaseScore(int player, int points)
	{
		// 0 == the white player, 1 == black player
		if(player == 0)
			scoreWhite += points;
		else
			scoreBlack += points;
	}
	
	// testing
	public static void main(String[] args)
	{
		UDPServer server = new UDPServer();
		
		// receive the first messages from the players
		server.receivePlayers();
		
		// the game has begun - send relevant message to players
		server.sendBeginning();
		
		// keep on listening and act accordingly
		server.listening();
		
		// optionally close graphics and terminate
		if(args.length == 1)
			if(Integer.parseInt(args[0]) == 0)	
				closeOnTermination = false;
			else
				closeOnTermination = true;
		
		if(closeOnTermination)
		{
			server.board.dispose();
			server.screen.dispose();
		}
		
	}

}
