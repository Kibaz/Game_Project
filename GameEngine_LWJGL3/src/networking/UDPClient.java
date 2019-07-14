package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.UUID;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import rendering.Window;
import runtime.Main;

public class UDPClient {
	
	private static final int BUFFER_SIZE = 1024;
	
	private DatagramSocket socket;
	
	private Thread listenThread;
	
	private byte[] dataBuffer;
	
	private boolean running;
	
	private float latency = 0;
	
	private float timeDelta = 0;
	
	public UDPClient()
	{
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
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
								System.out.println(Client.id);
							}
							
							if(message.contains("SYNC"))
							{
								String[] values = message.split(":");
								long sentTimeStamp = Long.parseLong(values[1]);
								long serverTimeStamp = Long.parseLong(values[2]);
								long currentTimeStamp = System.currentTimeMillis();
								long roundTripTime = currentTimeStamp - sentTimeStamp;
								latency = roundTripTime / 2f;
								System.out.println("Latency " + latency);
								
								float serverDelta = serverTimeStamp - currentTimeStamp;
								timeDelta = serverDelta + latency;
								System.out.println("Time Delta: " + timeDelta);
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
								}
								else
								{
									if(Client.checkPeerExists(uuid))
									{
										PeerClient peer = Client.getPeerByID(uuid);
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
											peer.setPrevRotX(peerEntity.getRotX());
											peer.setPrevRotY(peerEntity.getRotY());
											peer.setPrevRotZ(peerEntity.getRotZ());
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
	
	

}
