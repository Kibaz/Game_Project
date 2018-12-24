package networking;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.lwjgl.util.vector.Vector3f;

import entities.Player;
import peerData.PeerPlayerData;

public class Client {
	
	// Fields
	private static DatagramSocket socket;
	/* TCP socket for handling initial connection
	 * Could also be used for a chat system
	 */
	
	public static PeerClientHandler handler = new PeerClientHandler();
	
	private static Thread clientThread;
	private static boolean connected = false;
	
	public static InetAddress serverAddress;
	public static int port = 8192;
	
	public static int ID = 0;
	
	private static byte[] buffer = new byte[100];
	
	
	/*
	 * Establish a connection with the corresponding server
	 * Send packets to server periodically
	 * Receive packets from the server periodically
	 */
	public static void connect()
	{
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("Cannot establish a connection");
			e.printStackTrace();
		}
		
		if(socket != null)
		{
			connected = true;
		}
		
		clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				/*
				 * While a connection is established
				 */

					try {
						while(connected)
						{
							/* Listen for packets from the server */
							DatagramPacket response = new DatagramPacket(buffer, buffer.length);
							socket.receive(response);
							
							String msg = new String(response.getData(), 0, response.getLength());
							
							byte[] data = parseData(response.getLength(),response.getData());
							
							/* Determine actions received from server/other clients */
							if(msg.contains("pressed"))
							{
								// Get ID appended to end of message received
								String[] splitMsg = msg.split(" ");
								int clientID = Integer.parseInt(splitMsg[splitMsg.length-1]);
								
								// Get client by ID
								if(!handler.getPeers().isEmpty() && handler.getPeers().size() != 0)
								{	
									PeerClient peer = handler.getPeers().get(clientID);
									if(peer.getServerRequests().isEmpty() && peer.getServerRequests().size() == 0)
									{
										peer.processRequest(0, msg);
									}
									else
									{
										/* Shift requests, drop 2nd last request received */
										peer.processRequest(1, peer.getServerRequests().get(0));
										peer.processRequest(0, msg);
									}
									
								}
							}
							
							if(msg.contains("released"))
							{
								String[] splitMsg = msg.split(" ");
								int clientID = Integer.parseInt(splitMsg[splitMsg.length-1]);
								
								// Get client by ID
								if(!handler.getPeers().isEmpty() && handler.getPeers().size() != 0)
								{	
									PeerClient peer = handler.getPeers().get(clientID);
									if(peer.getServerRequests().isEmpty() && peer.getServerRequests().size() == 0)
									{
										peer.processRequest(0, msg);
									}
									else
									{
										/* Shift requests, drop 2nd last request received */
										peer.processRequest(1, peer.getServerRequests().get(0));
										peer.processRequest(0, msg);
									}
									
								}
							}
							
							/* Store information about current client */
							if(msg.startsWith("ID"))
							{
								ID = Integer.parseInt(msg.split(" ")[1]);
								PeerClient client = new PeerClient();
								client.setID(ID);
								handler.addPeer(ID, client);
							}
							
							if(msg.startsWith("PlayerData"))
							{
								PeerPlayerData ppData = new PeerPlayerData(data);
								if(handler.getPeers().containsKey(ppData.getClientID()))
								{
									PeerClient peer = handler.getPeers().get(ppData.getClientID());
									peer.setPlayerData(ppData);
									if(peer.getServerRequests().isEmpty() && peer.getServerRequests().size() == 0)
									{
										peer.processRequest(0, msg);
									}
									else
									{
										/* Shift requests, drop 2nd last request received */
										peer.processRequest(1, peer.getServerRequests().get(0));
										peer.processRequest(0, msg);
									}
								}
								else
								{
									PeerClient peer = new PeerClient();
									peer.setID(ppData.getClientID());
									handler.addPeer(ppData.getClientID(), new PeerClient());
									peer.setPlayerData(ppData);
									if(peer.getServerRequests().isEmpty() && peer.getServerRequests().size() == 0)
									{
										peer.processRequest(0, msg);
									}
									else
									{
										/* Shift requests, drop 2nd last request received */
										peer.processRequest(1, peer.getServerRequests().get(0));
										peer.processRequest(0, msg);
									}
								}
								
							}
							
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			
		});
		
		clientThread.start();
	}
	
	/*
	 * Parse byte array data into correct length
	 * Excluding any empty/excess blocks of data
	 */
	private static byte[] parseData(int length, byte[] data)
	{
		byte[] result = new byte[length];
		for(int i = 0; i < length; i++)
		{
			result[i] = data[i];
		}
		
		return result;
	}
	
	/*
	 * Check whether the client has already been registered
	 * in the handler by the current client based on "ID"
	 */
	private static boolean alreadyExists(PeerClient peer)
	{
		boolean exists = false;
		for(PeerClient client: handler.getPeers())
		{
			if(peer.getID() == client.getID())
			{
				exists = true;
				break;
			}
		}
		
		return exists;
	}
	
	/*
	 * Send packets to the server
	 */
	public static void send(byte[] data)
	{
		try {
			DatagramPacket packet = new DatagramPacket(data,data.length,serverAddress,port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Disconnect from the server
	 */
	public static void disconnect()
	{
		// Tell the server this client is disconnecting
		String msg = "Disconnect " + ID;
		send(msg.getBytes());
		
		try {
			connected = false;
			socket.close();
			clientThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	

}
