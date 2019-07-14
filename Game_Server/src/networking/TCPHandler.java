package networking;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPHandler {
	
	private ServerSocket socket;
	
	public TCPHandler(int port) throws IOException
	{
		socket = new ServerSocket(port);
	}
	
	public void listen()
	{
		
	}

}
