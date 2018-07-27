import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ClientTest 
{

	public static void main(String[] args) 
	{
		Client CodyClient;
		CodyClient = new Client("127.0.0.1");
		CodyClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CodyClient.startRunning();
	}

}
