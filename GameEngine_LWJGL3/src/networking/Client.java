package networking;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import components.Motion;
import entities.Entity;
import inputs.Input;
import inputs.KeyboardHandler;
import rendering.Window;
import terrains.Terrain;

public class Client {
	
	private static final float SYNC_THRESHOLD = 5;
	private static final float INPUT_THRESHOLD = 1/8f;
	
	private Entity player;
	
	private InetAddress serverAddress;
	private int serverTCPPort;
	private int serverUDPPort;
	
	private UDPClient udpClient;
	private TCPClient tcpClient;
	
	private float syncTime = 0;


	private float inputTime = 0;
	
	private List<Input> inputs;
	private List<InputSnapshot> inputSnapshots;
	
	private InputSnapshot currentSnapshot;
	
	private int inputCount = 0;
	
	public static int lastReceivedSnapshot = 0;
	private int previousSnapshot = 0;
	
	public static Vector3f clientPosition = new Vector3f(100,0,90);
	public static Vector3f clientRotation = new Vector3f(0,0,0);
	
	public static UUID id;
	
	private static Map<UUID,PeerClient> peerClients = new HashMap<>();
	
	public Client(Entity player,String serverAddress, int serverUDPPort, int serverTCPPort)
	{
		try {
			this.serverAddress = InetAddress.getByName(serverAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.serverUDPPort = serverUDPPort;
		this.serverTCPPort = serverTCPPort;
		this.udpClient = new UDPClient();
		this.tcpClient = new TCPClient(serverAddress,serverTCPPort);
		this.player = player;
		inputs = new ArrayList<>();
		inputSnapshots = new ArrayList<>();
		currentSnapshot = new InputSnapshot();
	}
	
	public void listen()
	{
		udpClient.listen();
	}
	
	public void connect()
	{
		// UDP is connection-less, however send initialisation message to server
		// Server will retrieve an IP Address and Port from client init message
		udpClient.send("Connecting to server".getBytes(),serverAddress, serverUDPPort);
	}
	
	public void update(List<Terrain> terrains)
	{
		
		if(Client.id != null)
		{
			player.setPosition(Client.clientPosition);
			player.setRotY(Client.clientRotation.y);
		}
		
		reconcileInputs(terrains);
		
		syncTime += Window.getFrameTime();
		if(syncTime > INPUT_THRESHOLD)
		{
			sync();
			syncTime %= SYNC_THRESHOLD;
		}
		
		inputTime += Window.getFrameTime();
		if(inputTime > INPUT_THRESHOLD)
		{
			
			if(currentSnapshot.getInputs().size() > 0 || !currentSnapshot.getInputs().isEmpty())
			{
				// Send current input snapshot
				sendSnapshot(currentSnapshot);
				currentSnapshot = new InputSnapshot();
				inputSnapshots.add(currentSnapshot);
				//inputs.clear();
			}
			
			inputTime %= INPUT_THRESHOLD;
		}
		
		Motion motion = player.getComponentByType(Motion.class);
		checkInputs(motion);
		
		// Client-side prediction apply inputs once sent
		doClientPredictions(motion,terrains);
		
	}
	
	private void doClientPredictions(Motion motion, List<Terrain> terrains)
	{
		player.increaseRotation(0, motion.getCurrentTurnSpeed() * Window.getFrameTime(), 0);
		
		float distance = motion.getCurrentSpeed() * Window.getFrameTime();
		float dx = (float) (distance * Math.sin(Math.toRadians(player.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(player.getRotY())));
		motion.setCurrentVelocity(dx,0,dz);
		
		player.increasePosition(dx, 0, dz);
		
		motion.applyGravity();
		
		player.increasePosition(0, motion.getJumpSpeed() * Window.getFrameTime(), 0);
		
		collideWithTerrains(motion,terrains);
		
	}
	
	private void collideWithTerrains(Motion motion,List<Terrain> terrains)
	{
		for(Terrain terrain: terrains)
		{
			if(terrain.isEntityOnTerrain(player))
			{
				float terrainHeight = terrain.getTerrainHeight(player.getPosition().x, player.getPosition().z);
				if(player.getPosition().y < terrainHeight)
				{
					player.getPosition().y = terrainHeight;
					motion.setAirborne(false);
				}
			}
		}
	}
	
	private void checkInputs(Motion motion)
	{
		/* Check whether a key has been pressed */
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_W))
		{
			motion.setCurrentSpeed(20);
			Input input = new Input("w key pressed",Window.getFrameTime());
			currentSnapshot.addInput(input);
			inputs.add(input);
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S))
		{
			motion.setCurrentSpeed(-10);
			Input input = new Input("s key pressed",Window.getFrameTime());
			currentSnapshot.addInput(input);
			inputs.add(input);
		}
		else
		{
			motion.setCurrentSpeed(0);
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D))
		{
			motion.setCurrentTurnSpeed(-160);
			Input input = new Input("d key pressed",Window.getFrameTime());
			currentSnapshot.addInput(input);
			inputs.add(input);
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A))
		{
			motion.setCurrentTurnSpeed(160);
			Input input = new Input("a key pressed",Window.getFrameTime());
			currentSnapshot.addInput(input);
			inputs.add(input);
		}
		else
		{
			motion.setCurrentTurnSpeed(0);
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE)){
			motion.jump();
			Input input = new Input("space key pressed",Window.getFrameTime());
			currentSnapshot.addInput(input);
			inputs.add(input);
		}
	}
	
	private void applyInputs(List<Input> inputs, List<Terrain> terrains)
	{
		for(Input input: inputs)
		{
			applyInput(terrains,input);
		}
		
		Motion motion = player.getComponentByType(Motion.class);
		motion.applyGravity();
		
		player.increasePosition(0, motion.getJumpSpeed() * Window.getFrameTime(), 0);
		
		for(Terrain terrain: terrains)
		{
			if(terrain.isEntityOnTerrain(player))
			{
				float terrainHeight = terrain.getTerrainHeight(player.getPosition().x, player.getPosition().z);
				if(player.getPosition().y < terrainHeight)
				{
					player.getPosition().y = terrainHeight;
					motion.setAirborne(false);
				}
			}
		}
	}
	
	private void reconcileInputs(List<Terrain> terrains)
	{
		int i = 0;
		while(i < inputSnapshots.size())
		{
			InputSnapshot snapshot = inputSnapshots.get(i);
			if(snapshot.getIndex() <= Client.lastReceivedSnapshot)
			{
				inputSnapshots.remove(i);
			}
			else
			{
				applyInputs(snapshot.getInputs(),terrains);
				i++;
			}
		}
	}
	
	private void applyInput(List<Terrain> terrains,Input input)
	{
		Motion motion = player.getComponentByType(Motion.class);
		
		if(input.getInput().startsWith("w "))
		{
			motion.setCurrentSpeed(20);
		}	
		else if(input.getInput().startsWith("s "))
		{
			motion.setCurrentSpeed(-10);
		}
		else
		{
			motion.setCurrentSpeed(0);
			motion.setCurrentVelocity(0,0,0);
		}
		
		if(input.getInput().startsWith("d "))
		{
			motion.setCurrentTurnSpeed(-160);
			
		}
		else if(input.getInput().startsWith("a "))
		{
			motion.setCurrentTurnSpeed(160);
		}
		else
		{
			motion.setCurrentTurnSpeed(0);
		}
		
		if(input.getInput().startsWith("space "))
		{
			motion.jump();
		}
		
		player.increaseRotation(0, motion.getCurrentTurnSpeed() * input.getTime(), 0);
		
		float distance = motion.getCurrentSpeed() * input.getTime();
		float dx = (float) (distance * Math.sin(Math.toRadians(player.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(player.getRotY())));
		motion.setCurrentVelocity(dx,0,dz);
		
		player.increasePosition(motion.getCurrentVelocity().x, 
				motion.getCurrentVelocity().y, 
				motion.getCurrentVelocity().z);
	}
	
	private void sendSnapshot(InputSnapshot snapshot)
	{
		sendUDP(snapshot.convertToByteArray());
	}
	
	/*
	 * Method to handle synchronisation of 
	 * client's clock with the server's clock.
	 * 
	 * A packet is sent every 5 seconds to reduce
	 * bottleneck whilst ensuring frequent enough
	 * synchronisation for accurate calculations
	 * of latency
	 */
	public void sync()
	{
		long timeStamp = System.currentTimeMillis();
		String message = "SYNC:" + timeStamp;
		sendUDP(message.getBytes());
	}
	
	public void sendUDP(byte[] data)
	{
		udpClient.send(data, serverAddress, serverUDPPort);
	}
	
	public void end()
	{
		udpClient.end();
	}
	
	public void processInput(Input input)
	{
		inputs.add(input);
	}
	
	public int getInputCount()
	{
		return inputCount;
	}
	
	public static int getLastReceivedSnapshot()
	{
		return lastReceivedSnapshot;
	}
	
	public static void setLastReceivedSnapshot(int snapshot)
	{
		lastReceivedSnapshot = snapshot;
	}
	
	public static Map<UUID,PeerClient> getPeerClients()
	{
		return peerClients;
	}
	
	public static PeerClient getPeerByID(UUID id)
	{
		return peerClients.get(id);
	}
	
	public static boolean checkPeerExists(UUID id)
	{
		return peerClients.containsKey(id);
	}
	
	public static void addPeer(PeerClient peer)
	{
		peerClients.put(peer.getID(),peer);
	}
	
	public static void removePeer(PeerClient peer)
	{
		peerClients.remove(peer.getID());
	}

	public List<Input> getInputs() {
		return inputs;
	}
	
	
	

}
