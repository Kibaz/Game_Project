package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import inputs.Input;

public class UDPHandler {
	
	private static final int BUFFER_SIZE = 1024;
	
	private DatagramSocket socket;
	private byte[] dataBuffer;
	
	public UDPHandler(int port) throws IOException
	{
		socket = new DatagramSocket(port);
		dataBuffer = new byte[BUFFER_SIZE];
	}
	
	public void listen()
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while(true)
					{
						DatagramPacket receivedPacket = new DatagramPacket(dataBuffer,dataBuffer.length);
						socket.receive(receivedPacket);
						
						String message = new String(receivedPacket.getData(),0,receivedPacket.getLength());
						
						if(message.contains("SYNC"))
						{
							long clientTimeStamp = Long.parseLong(message.split(":")[1]);
							String returnMsg = "SYNC:" + clientTimeStamp + ":" + System.currentTimeMillis();
							send(returnMsg.getBytes(),receivedPacket.getAddress(),receivedPacket.getPort());
						}
						
						// Retrieve client context
						int clientPort = receivedPacket.getPort();
						InetAddress clientIPAddress = receivedPacket.getAddress();
						
						Client client = ClientManager.getClientContext(clientIPAddress,clientPort);
						
						if(message.contains("Connecting to server"))
						{
							String returnMsg = "ID:" + client.getID();
							send(returnMsg.getBytes(),receivedPacket.getAddress(),receivedPacket.getPort());
						}
						
						// Handle input snapshots received from clients
						if(message.contains("INPUT SNAPSHOT"))
						{
							String snapshot = message.split(":")[1]; // Separate snapshot data
							String[] inputs = snapshot.split(","); // Get all input values
							for(int i = 0; i < inputs.length; i++)
							{
								String input = inputs[i];
								String[] inputVals = input.split(" ");
								float pressTime = Float.parseFloat(inputVals[3]);
								if(pressTime > 0)
								{
									String inputStr = inputVals[0] + " " + inputVals[1] + " " + inputVals[2];
									client.addInput(new Input(inputStr,pressTime));
								}
							}
							
							client.incrementSnaphots();
						}
						
					}
				} catch (IOException e) {
					
				}
				
			}
			
		}).start();
	}
	
	public void send(byte[] data,InetAddress ipAddress, int port)
	{
		try {
			DatagramPacket packet = new DatagramPacket(data,data.length, ipAddress, port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
