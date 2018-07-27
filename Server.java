import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;


public class Server extends JFrame
{
	private Font chatFont;
	private Font textFont;
	private Color chatWindColor;
	private Color userTxtColor;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	/*constructor
	* Builds the server Bascially
	*
	*/
	public Server(String host) 
	{
		super("Cody's Instant Messenging service");
		setLocationRelativeTo(null);
		userText = new JTextField();
		userText.setEditable(false);
		userTxtColor = new Color(128,128,128);
		textFont = new Font("arial", 10, 14);
		userText.addActionListener(
				new ActionListener() 
				{
					public void actionPerformed(ActionEvent event) 
					{
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		
		add(userText, BorderLayout.NORTH);
		userText.setBackground(userTxtColor);
		userText.setFont(textFont);
		userText.setForeground(Color.WHITE);
		chatWindow = new JTextArea();
		chatWindColor = new Color(0,0,0);
		chatFont = new Font("helvetica", 10, 16);
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow));
		chatWindow.setBackground(chatWindColor);
		chatWindow.setForeground(Color.WHITE);
		chatWindow.setFont(chatFont);
		chatWindow.setLineWrap(true);
		chatWindow.setWrapStyleWord(true);
		setSize(950, 750);
		setVisible(true);
		
	}
	//set up and run the server
	public void startRunning() 
	{
		try 
		{
			final int PORT = 6789;
			final int BACKLOG = 100;
			server = new ServerSocket(PORT, BACKLOG);
			while(true) 
			{
				try
				{
					waitForConnection();
					setupStreams();
					whileChatting();
					
				}
				catch(EOFException eofException)
				{
					showMessage("\n Server ended the connection!");
				}finally 
				{
					attemptReconnect();
				}
			}
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
		
	}
	
	//wait for connection, then display connection information
	private void waitForConnection() throws IOException
	{
		showMessage(" Waiting for somone to connect... \n");
		connection = server.accept();
		showMessage(" Now Connected To " + connection.getInetAddress(). getHostName());
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now  setup!	\n");
		
	}
	
	//during the chat conversation
	private void whileChatting() throws IOException
	{
		String MESSAGE = " You are Connected! ";
		sendMessage(MESSAGE);
		ableToType(true);
		do
		{
			try
			{
				MESSAGE = (String) input.readObject();
				showMessage("\n" + MESSAGE);
			}catch(ClassNotFoundException classNotFoundException)
			{
				showMessage("\nERROR 405");
			}
		}while(!MESSAGE.equals("CLIENT - END"));
		
	}
	
	//closes streams
	private void closeConnection()
	{
		showMessage("\n Closing connection... \n");
		ableToType(false);
		try 
		{
			output.close();
			input.close();
			connection.close();
			
		}catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	private void attemptReconnect() throws IOException
	{
		closeConnection();
		waitForConnection();
		setupStreams();
		whileChatting();
	}
	
	//send a message to client
	private void sendMessage(String message)
	{
		try 
		{
			if (!message.isEmpty())
			{
				output.writeObject("SERVER - " + message);
				output.flush();
				showMessage("\nSERVER - " + message);
			}
			
		}catch(IOException ioException)
		{
			chatWindow.append("\n Can not send that message");
			
		}
	}
	
	//shows the messages in the chat window
	private void showMessage(final String text)
	{
		SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run() 
					{
						chatWindow.append(text);
					}
				}
			);
	}
	
	//let the user type in their box
	private void ableToType(final boolean tof)
	{
		SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run() 
					{
						userText.setEditable(tof);
					}
				}
			);
	}
}
