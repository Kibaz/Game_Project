package networking;

import java.io.IOException;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import terrains.Terrain;

public class Server {
	
	private UDPHandler udpHandler;
	private TCPHandler tcpHandler;
	
	private float upTime;
	
	private double lastTimeInterval;
	private static float deltaTime;
	
	private boolean running = false;
	
	public Server(int udpPort, int tcpPort)
	{
		try {
			udpHandler = new UDPHandler(udpPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			tcpHandler = new TCPHandler(tcpPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		initGLFW(); // Initialise GLFW for Timer
	}
	
	private void initGLFW()
	{
		if(!GLFW.glfwInit())
		{
			throw new IllegalStateException("Unable to initialise GLFW");
		}
	}
	
	public void start()
	{
		// Start listening on UDP and TCP ports
		udpHandler.listen();
		tcpHandler.listen();
		lastTimeInterval = GLFW.glfwGetTime();
		running = true;
	}
	
	public void stop()
	{
		running = false;
	}
	
	/*
	 * Carry out all calculations,
	 * communications associated with
	 * clients per server tick
	 */
	public void update(List<Terrain> terrains)
	{	
		while(running)
		{
			// Calculate delta time
			calculateDeltaTime(); 
			// Calculate server up-time
			upTime += deltaTime;
			
			for(Client client: ClientManager.getClients())
			{
				client.update(terrains); // Update client
				// Send the data for each client to every other connected client
				for(Client other: ClientManager.getClients())
				{
					String message = "Position " + client.getID() + "," + 
							client.getPlayer().getPosition().x + "," +
							client.getPlayer().getPosition().y + "," +
							client.getPlayer().getPosition().z + "," +
							client.getPlayer().getRotX() + "," +
							client.getPlayer().getRotY() + "," +
							client.getPlayer().getRotZ() + "," +
							client.getSnapshotCount();
					udpHandler.send(message.getBytes(), other.getIpAddress(), other.getPort());
				}
			}

		}
	}
	
	/*
	 * Calculate the delta time in server ticks
	 */
	private void calculateDeltaTime()
	{
		double currentTimeInterval = GLFW.glfwGetTime();
		deltaTime = (float) (currentTimeInterval - lastTimeInterval);
		lastTimeInterval = currentTimeInterval;
	}
	
	public static float getDeltaTime()
	{
		return deltaTime;
	}

}
