package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import components.AI;
import components.EntityInformation;
import components.EntityProfile;
import entities.Entity;
import rendering.Window;
import runtime.Main;
import terrains.Terrain;

public class UDPClient {
	
	private static final int BUFFER_SIZE = 1024;
	
	private DatagramSocket socket;
	
	private Thread listenThread;
	
	private byte[] dataBuffer;
	
	private boolean running;
	
	private float latency = 0;
	
	private float timeDelta = 0;
	
	private List<Terrain> tempTerrains;
	
	private Client client;
	
	public UDPClient(Client client)
	{
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.client = client;
		dataBuffer = new byte[BUFFER_SIZE];
	}
	
	public void listen()
	{
		
		running = true;
		
		// Initialise listener thread
		listenThread = new Thread(new Runnable() {

			@Override
			public void run() {
					try {
						while(running) // While game is running
						{
							DatagramPacket packet = new DatagramPacket(dataBuffer,dataBuffer.length);
							socket.receive(packet);
							
							String message = new String(packet.getData(),0,packet.getLength());
							
							if(message.startsWith("ID"))
							{
								Client.id = UUID.fromString(message.split(":")[1]);
							}
							
							if(message.contains("SYNC"))
							{
								String[] values = message.split(":");
								long sentTimeStamp = Long.parseLong(values[1]);
								long serverTimeStamp = Long.parseLong(values[2]);
								long currentTimeStamp = System.currentTimeMillis();
								long roundTripTime = currentTimeStamp - sentTimeStamp;
								latency = roundTripTime / 2f;
								float serverDelta = serverTimeStamp - currentTimeStamp;
								timeDelta = serverDelta + latency;
							}
							
							if(message.startsWith("CHANGED TARGET"))
							{
								String valueStr = message.split(":")[1];
								String[] values = valueStr.split(",");
								UUID entityID = UUID.fromString(values[0]);
								float targetX = Float.parseFloat(values[1]);
								float targetY = Float.parseFloat(values[2]);
								float posX = Float.parseFloat(values[3]);
								float posY = Float.parseFloat(values[4]);
								float posZ = Float.parseFloat(values[5]);
								float rotX = Float.parseFloat(values[6]);
								float rotY = Float.parseFloat(values[7]);
								float rotZ = Float.parseFloat(values[8]);
								if(Client.checkEntityExists(entityID))
								{
									// Update existing entity's AI target
									Entity aiEntity = Client.getEntityByID(entityID);
									AI ai = aiEntity.getComponentByType(AI.class);
									if(ai != null) ai.setTarget(new Vector2f(targetX,targetY));
								}
								else
								{
									/* 
									 * Create new entity with an AI component 
									 * and set a target to follow
									 */
									Entity aiEntity = new Entity(Main.testModel,new Vector3f(posX,posY,posZ),rotX,rotY,rotZ,1);
									aiEntity.setID(entityID);
									AI ai = new AI("ai",new ArrayList<>(),
											tempTerrains,new ArrayList<>(),new HashMap<>());
									ai.setSlowingRadius(7);
									ai.setAvoidanceForce(0.1f);
									ai.setTarget(new Vector2f(targetX,targetY));
									aiEntity.addComponent(ai); // Add an AI component
									ai.start();
									Client.addEntityToQueue(aiEntity);
								}

							}
							
							if(message.startsWith("ENTITY HEALTH:"))
							{
								System.out.println(message);
								int health = Integer.parseInt(message.split(":")[1]);
								EntityInformation playerInfo = client.getPlayer().getComponentByType(EntityInformation.class);
								playerInfo.setHealth(health);
							}
							
							if(message.startsWith("Position"))
							{
								String posStr = message.split(" ")[1];
								String[] values = posStr.split(",");
								UUID uuid = UUID.fromString(values[0]);
								float x = Float.parseFloat(values[1]);
								float y = Float.parseFloat(values[2]);
								float z = Float.parseFloat(values[3]);
								float rotX = Float.parseFloat(values[4]);
								float rotY = Float.parseFloat(values[5]);
								float rotZ = Float.parseFloat(values[6]);
								if(uuid.equals(Client.id))
								{
									Client.clientPosition = new Vector3f(x,y,z);
									Client.clientRotation = new Vector3f(rotX,rotY,rotZ);
									Client.lastReceivedSnapshot = Integer.parseInt(values[7]);
									Client.lastFrameCount = Integer.parseInt(values[8]);
									long currentTime = System.currentTimeMillis();
									Client.timeBetweenUpdates = (currentTime - Client.lastUpdateTime)/1000f;
									Client.lastUpdateTime = System.currentTimeMillis();
								}
								else
								{
									if(Client.checkPeerExists(uuid))
									{
										PeerClient peer = Client.getPeerByID(uuid);
										peer.setPrevTimeStamp(peer.getNextTimeStamp());
										peer.setNextTimeStamp(System.currentTimeMillis());
										peer.setNextPosition(new Vector3f(x,y,z));
										peer.setNextRotX(rotX);
										peer.setNextRotY(rotY);
										peer.setNextRotZ(rotZ);
									}
									else
									{
										if(Main.testModel != null)
										{
											Entity peerEntity = new Entity(Main.testModel,new Vector3f(x,y,z),rotX,rotY,rotZ,1);
											PeerClient peer = new PeerClient(uuid,peerEntity);
											peer.setPreviousPosition(peerEntity.getPosition());
											peer.setPrevRotX(rotX);
											peer.setPrevRotY(rotY);
											peer.setPrevRotZ(rotZ);
											peer.setPrevTimeStamp(System.currentTimeMillis());
											peer.setNextTimeStamp(System.currentTimeMillis());
											Client.addPeer(peer);
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			
		}); 
				
		listenThread.start(); // Start listener thread
	}
	
	public void send(byte[] data, InetAddress serverAddress, int serverPort) 
	{
		try {
			DatagramPacket packet = new DatagramPacket(data,data.length,serverAddress,serverPort);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void end()
	{
		running = false;
		try {
			socket.close();
			listenThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public float getLatency() {
		return latency;
	}

	public float getTimeDelta() {
		return timeDelta;
	}
	
	public void setTempTerrains(List<Terrain> terrains)
	{
		this.tempTerrains = terrains;
	}
	
	

}
