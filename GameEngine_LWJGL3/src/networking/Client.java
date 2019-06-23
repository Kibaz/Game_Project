package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;
import entities.Entity;
import inputs.Input;
import inputs.KeyInput;
import inputs.KeyboardHandler;
import models.BaseModel;
import models.TexturedModel;
import rendering.Loader;
import rendering.Window;
import runtime.Main;
import texturing.ModelTexture;

public class Client {
	
	// Global input tracking
	public static Map<Integer,Input> inputs = new HashMap<>(); // Map to store inputs with reference to order/index
	private static final double SEND_INPUT_INTERVAL = 0.03; // Fixed interval for sending input data
	private static float inputTime = 0;
	
	// Hold a reference ID for this client
	private static int ID;
	
	private static Map<Integer,PlayerData> otherEntities = new HashMap<>();
	
	// Store client and server synchronisation time for updates
	// This will calculate latency adjustments as well
	private static float time = 0;
	private static float currentUpdateTime = 0;
	private static float prevUpdateTime = 0;
	
	private static DatagramSocket udpSocket;
	public static InetAddress serverAddress;
	public static int serverPort;
	private static Thread clientThread;
	
	// Store previous and current player positions received from server
	// Use this data to implement entity interpolation
	private static Vector3f previousPlayerPosition;
	private static Vector3f currentPlayerPosition;
	
	private static float prevPlayerRX;
	private static float prevPlayerRY;
	private static float prevPlayerRZ;
	
	private static float currentPlayerRX;
	private static float currentPlayerRY;
	private static float currentPlayerRZ;
	
	
	private static byte[] buffer = new byte[512];
	
	public static void listen()
	{
		try {
			udpSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		// Do not listen if UDP socket has not been initialised
		if(udpSocket == null)
		{
			return;
		}
		
		// run client listening for server information in separate thread
		clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{
					while(true)
					{
						// Configure Datagram packet
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						// Block whilst waiting for serve response
						udpSocket.receive(packet);
						
						String msg = new String(packet.getData(), 0, packet.getLength());
						if(msg.startsWith("ID: "))
						{
							ID = Integer.parseInt(msg.substring(msg.lastIndexOf(' ') + 1));
						}
						if(msg.startsWith("Position: "))
						{
							String values = msg.split(":")[1];
							int receivedID = Integer.parseInt(values.substring(values.lastIndexOf(',')+1));
							if( receivedID != ID && !otherEntities.containsKey(receivedID))
							{
								// Create a new entity to render
								createEntity(values);
							}
							
							Vector3f position = new Vector3f(Float.parseFloat(values.split(",")[0]),
									Float.parseFloat(values.split(",")[1]),Float.parseFloat(values.split(",")[2]));
							float rx = Float.parseFloat(values.split(",")[3]);
							float ry = Float.parseFloat(values.split(",")[4]);
							float rz = Float.parseFloat(values.split(",")[5]);
							if(otherEntities.containsKey(receivedID))
							{
								PlayerData current = otherEntities.get(receivedID);
								if(current.getNextPosition() != null)
								{
									current.setPreviousPosition(current.getNextPosition());
								}
								current.setNextPosition(position);
							}
							
							currentPlayerPosition = position;
						}
						

					}
				}catch (SocketException e)
				{
					System.out.println("Client udp socket closed...");
				}catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		
		// Start client thread
		clientThread.start();
	}
	
	/*
	 * Handle sending of client's input
	 * Reduce network traffic by sending over strict interval
	 * This will reduce the number of packets sent over the network
	 * Thus reducing latency
	 */
	public static void sendInputs()
	{
		inputTime += Window.getFrameTime();
		if(inputTime > SEND_INPUT_INTERVAL)
		{
			inputTime %= SEND_INPUT_INTERVAL; // Reset input timer
			
			// Scan inputs using iterator to remove inputs once processed
			Iterator<Entry<Integer,Input>> it = inputs.entrySet().iterator();
			// Count total time of key presses
			float wKeyTime = 0;
			float sKeyTime = 0;
			float aKeyTime = 0;
			float dKeyTime = 0;
			while(it.hasNext())
			{
				Input current = it.next().getValue();
				if(current instanceof KeyInput)
				{
					KeyInput input = (KeyInput) current;
					switch(input.getKey())
					{
					case 'w': wKeyTime += input.getTimeStamp(); break;
					case 's': sKeyTime += input.getTimeStamp(); break;
					case 'a': aKeyTime += input.getTimeStamp(); break;
					case 'd': dKeyTime += input.getTimeStamp(); break;
					case ' ': String message = " key pressed " + input.getTimeStamp(); send(message.getBytes()); break;
					}
				}
				
				it.remove();
			}
			
			if(wKeyTime > 0)
			{
				String message = "w key pressed " + wKeyTime;
				send(message.getBytes());
			}
			
			if(sKeyTime > 0)
			{
				String message = "s key pressed " + sKeyTime;
				send(message.getBytes());
			}
			
			if(aKeyTime > 0)
			{
				String message = "a key pressed " + aKeyTime;
				send(message.getBytes());
			}
			
			if(dKeyTime > 0)
			{
				String message = "d key pressed " + dKeyTime;
				send(message.getBytes());
			}
		}

	}
	
	// Send data to the server
	public static void send(byte[] data)
	{
		try {
			DatagramPacket packet = new DatagramPacket(data,data.length,serverAddress,serverPort);
			udpSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void disconnect()
	{
		send(("Client " + ID + " disconnected").getBytes());
		udpSocket.close();
		try {
			clientThread.join(); // wait for thread to terminate
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Getters and Setters
	

	public static Vector3f getCurrentPlayerPosition() {
		return currentPlayerPosition;
	}

	public static float getPrevPlayerRX() {
		return prevPlayerRX;
	}

	public static float getPrevPlayerRY() {
		return prevPlayerRY;
	}

	public static float getPrevPlayerRZ() {
		return prevPlayerRZ;
	}

	public static float getCurrentPlayerRX() {
		return currentPlayerRX;
	}

	public static float getCurrentPlayerRY() {
		return currentPlayerRY;
	}

	public static float getCurrentPlayerRZ() {
		return currentPlayerRZ;
	}

	public static Vector3f getPreviousPlayerPosition() {
		return previousPlayerPosition;
	}

	public static void setPreviousPlayerPosition(Vector3f previousPlayerPosition) {
		Client.previousPlayerPosition = previousPlayerPosition;
	}
	
	public static float getUpdateTime()
	{
		return currentUpdateTime - prevUpdateTime;
	}

	/*
	 * Use this method to update the 
	 * "updateTime" interval
	 */
	
	public static void increaseUpdateTime()
	{
		time += Window.getFrameTime();
	}
	
	public static Map<Integer, PlayerData> getOtherEntities() {
		return otherEntities;
	}

	private static void createEntity(String data)
	{
		Vector3f position = new Vector3f(Float.parseFloat(data.split(",")[0]),
				Float.parseFloat(data.split(",")[1]),Float.parseFloat(data.split(",")[2]));
		float rx = Float.parseFloat(data.split(",")[3]);
		float ry = Float.parseFloat(data.split(",")[4]);
		float rz = Float.parseFloat(data.split(",")[5]);
		//PlayerData playerData = new PlayerData(Main.testEnt,Main.testAnimChar);
		//otherEntities.put(Integer.parseInt(data.split(",")[6]), playerData);
	}

	
	
	
	
	
	
	
	

}
