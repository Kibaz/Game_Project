package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.GLFW;

public class Server {
	
	// Constants
	private final int BUFFER_SIZE = 512;
	private final float CLIENT_SYNC_TIME = 0.125f;
	
	// Fields
	private int port; // Port for listening for clients
	private byte[] buffer; // Buffer for collecting client messages
	
	private int clientID;
	
	// Store time intervals for updates etc...
	private double lastTimeInterval;
	private static float deltaTime;
	private float syncTime; // For synchronising with clients
	
	// Boolean values
	private boolean running;
	
	private ConcurrentHashMap<Integer,Client> clientHandler;
	
	// Constructor
	public Server(int port)
	{
		this.port = port;
		buffer = new byte[BUFFER_SIZE];
		initGLFW();
		lastTimeInterval = GLFW.glfwGetTime();
		running = false;
		syncTime = 0;
		clientID = 1;
		clientHandler = new ConcurrentHashMap<>();
	}
	
	/*
	 * Commence listening for UDP packets from clients
	 * Carry out processing of packet data
	 * Send packets back to clients
	 */
	public void start()
	{
		running = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try(DatagramSocket udpSocket = new DatagramSocket(port))
				{
					while(true)
					{
						DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
						udpSocket.receive(packet);
						
						String msg = new String(packet.getData(), 0, packet.getLength());
						System.out.println(msg);
						
						// Check whether the client already exists
						Client client = checkIfClientExists(packet.getAddress(), packet.getPort());
						
						
						if(client == null)
						{
							// Register a new client if it does not exist
							client = new Client(clientID,packet.getAddress(),packet.getPort());
							clientHandler.put(clientID, client);
							clientID++;
						}
						
						
						
						if(msg.contains("key pressed"))
						{
							client.getKeyInputs().put(msg.charAt(0), Client.KEY_PRESSED);
						}
						
						if(msg.contains("key released"))
						{
							client.getKeyInputs().put(msg.charAt(0), Client.KEY_RELEASED);
						}
						
					}
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}).start();

	}
	
	/*
	 * Initialise GLFW for use of a Server runtime clock
	 */
	private void initGLFW()
	{
		if(!GLFW.glfwInit())
		{
			throw new IllegalStateException("Unable to initialise GLFW");
		}
	}
	
	/*
	 * Carry out a full cycle of the Server's operations
	 * Update timer
	 */
	public void update()
	{
		updateClients();
		
		// Update time intervals
		double currentTime = GLFW.glfwGetTime();
		deltaTime = (float) (currentTime - lastTimeInterval);
		lastTimeInterval = currentTime;
	}
	
	/*
	 * Reset synchronisation time
	 * Synchronise player positions, data etc with clients
	 * Carry out client updates 8 times per second
	 */
	private void updateClients()
	{
		syncTime += deltaTime;
		if(syncTime > CLIENT_SYNC_TIME)
		{
			//System.out.println("Updating clients...");
			
			// Reset sync time
			syncTime %= CLIENT_SYNC_TIME;
		}
		
		for(int id: clientHandler.keySet())
		{
			Client current = clientHandler.get(id);
			current.movePlayer();
			System.out.println(current.getPlayerData().getPosition());
		}
	}
	
	private Client checkIfClientExists(InetAddress address, int port)
	{
		for(int id: clientHandler.keySet())
		{
			Client curr = clientHandler.get(id);
			if(curr.getAddress().equals(address) && port == curr.getPort())
			{
				return curr;
			}
		}
		return null;
	}
	
	// Getters and Setters
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	public static float getDeltaTime() {
		return deltaTime;
	}

	public void cleanUp()
	{
		
	}

}
