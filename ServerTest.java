import javax.swing.JFrame;



public class ServerTest {

	public static void main(String[] args) 
	{
		Server ServerSuicide = new Server("127.0.0.1");
		ServerSuicide.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ServerSuicide.startRunning();

	}

}
