package networking;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	
	private Socket socket;
	
	public TCPClient(String address, int serverPort)
	{
		try {
			socket = new Socket(address,serverPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void listen()
	{
		
	}

}
