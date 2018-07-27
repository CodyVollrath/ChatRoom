import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;

public class Client extends JFrame
{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String serverIP;
	private String message = "";
	private Socket connection;
	private Color userTxtColor;
	private Color chatWindColor;
	private Font chatFont;
	private Font textFont;
	//constructor
	public Client(String host)
	{
		super("Client beta!");
		setLocationRelativeTo(null);
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userTxtColor = new Color(128,128,128);
		textFont = new Font("arial", 10, 14);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event)
					{
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}	
			);
		
		add(userText, BorderLayout.NORTH);
		userText.setFont(textFont);
		userText.setForeground(Color.WHITE);
		userText.setBackground(userTxtColor);
		chatWindow = new JTextArea();
		chatWindColor = new Color(0,0,0);
		chatFont = new Font("helvetica", 10, 16);
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		chatWindow.setBackground(chatWindColor);
		chatWindow.setForeground(Color.WHITE);
		chatWindow.setFont(chatFont);
		chatWindow.setLineWrap(true);
		chatWindow.setWrapStyleWord(true);
		setSize(950, 750);
		setVisible(true);
	}
	
	//connect
	public void startRunning()
	{
		try {
			connectToServer();
			setupStreams();
			whileChatting();
			
		}catch(EOFException eofException)
		{
			showMessage("\n Client terminated connection");
		}catch(IOException ioException)
		{
			ioException.printStackTrace();
		}finally
		{
			closeApplication();
		}
	}
	
	//connects to server
	private void connectToServer() throws IOException
	{
		showMessage("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to:" + connection.getInetAddress().getHostName());
	}
	//set up streams to send and receive messages
	private void setupStreams() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams Connected");
	}
	//while chanting with server
	private void whileChatting() throws IOException
	{
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException)
			{
				showMessage("\n I don't know that object type");
			}
		}while(!message.equals("SERVER - END"));
	}
	//closes connection
	public void closeApplication()
	{
		showMessage("\nCheck Connections");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	//send messages to server
	private void sendMessage(String message)
	{
		try
		{
			if (!message.isEmpty())
			{
				output.writeObject("CLIENT -" + message);
				output.flush();
				showMessage("\nCLIENT - " + message);
			}
			
			
		}catch(IOException ioException)
		{
			chatWindow.append("\n error sending message");
			
		}
	}
	//change/update chatwindow
	private void showMessage(final String m)
	{
		SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						chatWindow.append(m);
					}
				}
		);
	}
	//gives user permission to type into the text box
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
