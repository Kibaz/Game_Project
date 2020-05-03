package networking;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import components.Motion;
import entities.Entity;
import equip.EquipInventory;
import inputs.Input;
import inputs.KeyboardHandler;
import physics.CollisionTest;
import physics.Ellipsoid;
import physics.PairManager;
import physics.Plane;
import physics.SAP;
import physics.Triangle;
import physics.Utils;
import rendering.Window;
import runtime.Main;
import terrains.Terrain;
import utils.Maths;

public class Client {
	
	private static final float SYNC_THRESHOLD = 5;
	private static final float INPUT_THRESHOLD = 1/8f;
	
	private static final float FIXED_PHYSICS_UPDATE = 1/60f;
	
	private static final float UPPER_SLOPE_THRESHOLD = 0.6f;
	private static final float LOWER_SLOPE_THRESHOLD = -0.6f;
	
	private static final float GROUND_DETECT_LENGTH = 1f;
	
	private int frameCount = 0;
	
	private float groundAngle;
	
	private boolean grounded = false;
	
	private Entity player;
	
	private InetAddress serverAddress;
	private int serverTCPPort;
	private int serverUDPPort;
	
	private UDPClient udpClient;
	private TCPClient tcpClient;
	
	private float syncTime = 0;

	private static final float EPSILON = 0.000000001f;

	private float inputTime = 0;
	
	private List<Input> inputs;
	private List<InputSnapshot> inputSnapshots;
	
	private InputSnapshot currentSnapshot;
	
	private int inputCount = 0;
	
	public static int lastReceivedSnapshot = 0;
	public static int lastFrameCount = 0;
	public static long lastUpdateTime;
	public static float timeBetweenUpdates = 0;
	private int previousSnapshot = 0;
	
	public static Vector3f clientPosition = new Vector3f(100,0,90);
	public static Vector3f clientRotation = new Vector3f(0,0,0);
	
	public static UUID id;
	
	private static ConcurrentMap<UUID,PeerClient> peerClients = new ConcurrentHashMap<>();
	
	private static Map<UUID,Entity> entityQueue = new HashMap<>();
	
	// Physics simulation variables
	private Ellipsoid ellipse;
	private CollisionTest collTest;
	private float collisionRecursionDepth;
	
	private Entity currentCollidedEntity = null;
	
	private boolean applyGravity = false;
	
	private List<CapturedFrame> capturedFrames;
	
	private int lastKeyState = GLFW.GLFW_RELEASE;
	
	public Client(Entity player,String serverAddress, int serverUDPPort, int serverTCPPort)
	{
		try {
			this.serverAddress = InetAddress.getByName(serverAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.serverUDPPort = serverUDPPort;
		this.serverTCPPort = serverTCPPort;
		this.udpClient = new UDPClient(this);
		this.tcpClient = new TCPClient(serverAddress,serverTCPPort);
		this.player = player;
		this.ellipse = new Ellipsoid(player);
		ellipse.setRotation(-90, 0, 0);
		this.collTest = new CollisionTest();
		this.capturedFrames = new ArrayList<>();
		inputs = new ArrayList<>();
		inputSnapshots = new ArrayList<>();
		currentSnapshot = new InputSnapshot();
		lastUpdateTime = System.currentTimeMillis();
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
		Motion motion = player.getComponentByType(Motion.class);
		if(Client.id != null)
		{
			player.setRotY(Client.clientRotation.y);
			player.setPosition(Client.clientPosition);		
			player.getAABB().resetBox(player.getPosition());
			ellipse.reset(player.getPosition());
			reconcileInputs(terrains);
		}
		
		syncTime += Window.getFrameTime();
		if(syncTime > SYNC_THRESHOLD)
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
		
		checkInputs(motion);
		
		// Client-side prediction apply inputs once sent
		doClientPredictions(motion,terrains);
		
		//capturedFrames.add(new CapturedFrame(frameCount++,Window.getFrameTime()));
	}
	
	private void doClientPredictions(Motion motion, List<Terrain> terrains)
	{
		player.increaseRotation(0, motion.getCurrentTurnSpeed() * Window.getFrameTime(), 0);
		
		float distance = motion.getCurrentSpeed() * Window.getFrameTime();
		float dx = (float) (distance * Math.sin(Math.toRadians(player.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(player.getRotY())));
		
		motion.setCurrentVelocity(dx, 0, dz);
		
		applyGravity = checkGravityState(motion);
		
		if(applyGravity)
		{
			motion.applyGravity(FIXED_PHYSICS_UPDATE);
		}
		
		motion.setGravityVector(new Vector3f(0,motion.getJumpSpeed() * FIXED_PHYSICS_UPDATE,0));
		entityCollision(motion);
		collideWithTerrains(motion,terrains);
		player.getAABB().resetBox(player.getPosition());
		ellipse.reset(player.getPosition());
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
					motion.setJumpSpeed(0);
					player.getPosition().y = terrainHeight;
					motion.setAirborne(false);
				}
			}
		}
	}
	
	private void entityCollision(Motion motion)
	{
		collideAndSlide(motion);
	}
	
	private void gravityCollision(Motion motion)
	{
		collTest.eRadius = ellipse.getRadius();
		collTest.R3Position = new Vector3f(player.getPosition());
		collTest.R3Position.set(collTest.R3Position.x, collTest.R3Position.y + collTest.eRadius.y, collTest.R3Position.z);
		collTest.R3Velocity = motion.getGravityVector();
		
		if(motion.getGravityVector().length() > EPSILON && applyGravity)
		{
			// Iterate until we have our final position
			collisionRecursionDepth = 0;
			
			// calculate position and velocity in eSpace
			Vector3f eSpacePosition = new Vector3f(collTest.R3Position.x/collTest.eRadius.x,
					collTest.R3Position.y/collTest.eRadius.y,
					collTest.R3Position.z/collTest.eRadius.z);
			
			Vector3f eSpaceVelocity = new Vector3f(collTest.R3Velocity.x/collTest.eRadius.x,
					collTest.R3Velocity.y/collTest.eRadius.y,
					collTest.R3Velocity.z/collTest.eRadius.z);
			
			Vector3f finalPosition = collideWithWorld(eSpacePosition, eSpaceVelocity,motion,true);
			finalPosition.set(finalPosition.x * collTest.eRadius.x, 
					(finalPosition.y * collTest.eRadius.y) - collTest.eRadius.y,
					finalPosition.z * collTest.eRadius.z);
				
			player.setPosition(finalPosition);

		}
	}
	
	private boolean checkGravityState(Motion motion)
	{
		
		if(currentCollidedEntity == null)
		{
			return true;
		}
		
		Vector3f rayOrigin = player.getPosition();
		boolean isGround = false;
		/*
		 *  Use ray-casting against the current entities triangles to determine
		 *  whether the player is in the air or touching solid ground
		 */
		for(Triangle triangle: currentCollidedEntity.getTriangles())
		{
			Vector3f p1 = triangle.getPoints()[0];
			Vector3f p2 = triangle.getPoints()[1];
			Vector3f p3 = triangle.getPoints()[2];

			if(rayIntersectsTriangle(rayOrigin,new Vector3f(0,-GROUND_DETECT_LENGTH,0),p1,p2,p3))
			{
				isGround = true;
			}
		}
		
		if(!isGround)
		{
			motion.setAirborne(true);
		}
		
		if(motion.isAirborne())
		{
			return true;
		}
		
		for(int i = 0; i < currentCollidedEntity.getTriangles().length; i++)
		{
			Triangle curr = currentCollidedEntity.getTriangles()[i];
			Vector2f p1 = new Vector2f(curr.getPoints()[0].x,curr.getPoints()[0].z);
			Vector2f p2 = new Vector2f(curr.getPoints()[1].x,curr.getPoints()[1].z);
			Vector2f p3 = new Vector2f(curr.getPoints()[2].x,curr.getPoints()[2].z);
			
			
			
			if(pointInTriangle(new Vector2f(player.getPosition().x,player.getPosition().z),p1,p2,p3))
			{

				float steepness = 0;
				
				if(motion.getCurrentVelocity().length() > 0)
				{
					steepness = curr.calculateSteepness(new Vector3f(motion.getCurrentVelocity()));
				}
				
				if(steepness > 0)
				{
					return true;
				}

				
				if(steepness < LOWER_SLOPE_THRESHOLD)
				{
					return true;
				}
			}
		}
		
		/*Vector3f forward = new Vector3f((float) Math.sin(Math.toRadians(player.getRotY())),0,
				(float) Math.cos(Math.toRadians(player.getRotY())));
		float steepness = closest.calculateSteepness(forward);*/

		
		return false;
	}
	
	private boolean rayIntersectsTriangle(Vector3f rayOrigin,Vector3f rayDir, Vector3f p1, Vector3f p2, Vector3f p3)
	{
		Vector3f pvec = Vector3f.cross(rayDir, Vector3f.sub(p3, p1, null), null);
		
		float det = Vector3f.dot(Vector3f.sub(p2, p1, null), pvec);
		
		if(det < EPSILON) return false;
		
		if(Math.abs(det) < EPSILON) return false;
		
		float invDet = 1 / det;
		
		Vector3f tvec = Vector3f.sub(rayOrigin, p1, null);
		
		float u = Vector3f.dot(tvec, pvec) * invDet;
		
		if(u < 0 || u > 1) return false;
		
		Vector3f qvec = Vector3f.cross(tvec, Vector3f.sub(p2, p1, null), null);
		
		float v = Vector3f.dot(rayDir, qvec) * invDet;
		
		if(v < 0 || u + v > 1) return false;
		
		float t = Vector3f.dot(Vector3f.sub(p3, p1, null),qvec) * invDet;
		
		return true;
		
	}
	
	private boolean pointInTriangle(Vector2f point, Vector2f p1, Vector2f p2, Vector2f p3)
	{
		boolean hasNeg, hasPos;
		
		float d1 = sign(point,p1,p2);
		float d2 = sign(point,p2,p3);
		float d3 = sign(point,p3,p1);
		
		hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0);
		hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0);
		
		return !(hasNeg && hasPos);
	}
	
	private float sign(Vector2f p1, Vector2f p2, Vector2f p3)
	{
		return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y); 
	}
	
	private void collideAndSlide(Motion motion)
	{
		collTest.eRadius = ellipse.getRadius();
		collTest.R3Position = new Vector3f(player.getPosition());
		collTest.R3Position.set(collTest.R3Position.x, collTest.R3Position.y + collTest.eRadius.y, collTest.R3Position.z);
		collTest.R3Velocity = motion.getCurrentVelocity();
		
		if(motion.getCurrentVelocity().length() > EPSILON)
		{
			// Iterate until we have our final position
			collisionRecursionDepth = 0;
			
			// calculate position and velocity in eSpace
			Vector3f eSpacePosition = new Vector3f(collTest.R3Position.x/collTest.eRadius.x,
					collTest.R3Position.y/collTest.eRadius.y,
					collTest.R3Position.z/collTest.eRadius.z);
			
			Vector3f eSpaceVelocity = new Vector3f(collTest.R3Velocity.x/collTest.eRadius.x,
					collTest.R3Velocity.y/collTest.eRadius.y,
					collTest.R3Velocity.z/collTest.eRadius.z);
			
			Vector3f finalPosition = collideWithWorld(eSpacePosition, eSpaceVelocity,motion,false);
			finalPosition.set(finalPosition.x * collTest.eRadius.x, 
					(finalPosition.y * collTest.eRadius.y) - collTest.eRadius.y,
					finalPosition.z * collTest.eRadius.z);
			
			player.setPosition(finalPosition);
		}
		
		
		
		
		collTest.R3Position = new Vector3f(player.getPosition());
		collTest.R3Position.set(collTest.R3Position.x, collTest.R3Position.y + collTest.eRadius.y, collTest.R3Position.z);
		collTest.R3Velocity = motion.getGravityVector();
		
		
		
		if(motion.getGravityVector().length() > EPSILON && applyGravity)
		{
			// Iterate until we have our final position
			collisionRecursionDepth = 0;
			
			// calculate position and velocity in eSpace
			Vector3f eSpacePosition = new Vector3f(collTest.R3Position.x/collTest.eRadius.x,
					collTest.R3Position.y/collTest.eRadius.y,
					collTest.R3Position.z/collTest.eRadius.z);
			
			Vector3f eSpaceVelocity = new Vector3f(collTest.R3Velocity.x/collTest.eRadius.x,
					collTest.R3Velocity.y/collTest.eRadius.y,
					collTest.R3Velocity.z/collTest.eRadius.z);
			
			Vector3f finalPosition = collideWithWorld(eSpacePosition, eSpaceVelocity,motion,true);
			finalPosition.set(finalPosition.x * collTest.eRadius.x, 
					(finalPosition.y * collTest.eRadius.y) - collTest.eRadius.y,
					finalPosition.z * collTest.eRadius.z);
				
			player.setPosition(finalPosition);

		}
	}
	
	private Vector3f collideWithWorld(Vector3f eSpacePos, Vector3f eSpaceVel, Motion motion, boolean isGravity)
	{
		float veryCloseDistance = 0.01f;
		
		if(collisionRecursionDepth > 5)
		{
			return eSpacePos;
		}
		
		collTest.velocity = eSpaceVel;
		collTest.normalizedVel = new Vector3f(eSpaceVel);
		collTest.normalizedVel.normalise();
		collTest.basePoint = eSpacePos;
		collTest.foundCollision = false;
		// Check for collision
		checkCollision();
		if(!collTest.foundCollision)
		{
			return Vector3f.add(eSpacePos, eSpaceVel, null);
		}
		
		if(isGravity)
		{
			motion.setAirborne(false);
			motion.setJumpSpeed(0);
		}
		
		Vector3f destinationPoint = Vector3f.add(eSpacePos, eSpaceVel, null);
		Vector3f newBasePoint = eSpacePos;
		
		if(collTest.nearestDistance >= veryCloseDistance)
		{
			Vector3f v = eSpaceVel;
			v.normalise().scale((float)collTest.nearestDistance - veryCloseDistance);
			newBasePoint = Vector3f.add(collTest.basePoint, v, null);
			v.normalise();
			collTest.intersectionPoint = Vector3f.sub(collTest.intersectionPoint, 
					(Vector3f) v.scale(veryCloseDistance), null);
		}
		
		// Determine the sliding plane
		Vector3f slidePlaneOrigin = collTest.intersectionPoint;
		Vector3f slidePlaneNormal = Vector3f.sub(newBasePoint, collTest.intersectionPoint, null);
		slidePlaneNormal.normalise();
		Plane slidingPlane = new Plane(slidePlaneOrigin, slidePlaneNormal);
		
		Vector3f newDestPoint = Vector3f.sub(destinationPoint, (Vector3f) slidePlaneNormal.scale((float) slidingPlane.signedDistTo(destinationPoint)), null);
		Vector3f newVelocityVector = Vector3f.sub(newDestPoint, collTest.intersectionPoint, null);
		
		// Recurse
		
		// don't recurse if the new velocity is very small
		if(newVelocityVector.length() < veryCloseDistance)
		{
			return newBasePoint;
		}
		
		collisionRecursionDepth++;
		return collideWithWorld(newBasePoint,newVelocityVector,motion,isGravity);
	}
	
	private void checkCollision()
	{
		if(SAP.activeList.size() == 0 || SAP.activeList.isEmpty())
		{
			currentCollidedEntity = null;
		}
		
		for(int i = 0; i < SAP.activeList.size(); i++)
		{
			PairManager current = SAP.activeList.get(i);
			Entity first = current.getFirst();
			Entity second = current.getSecond();
			Entity collider = null;
			
			if(first.equals(player))
			{
				collider = second;
			}
			
			if(second.equals(player))
			{
				collider = first;
			}
			
			if(collider != null)
			{
				currentCollidedEntity = collider;
				for(int j = 0; j < collider.getTriangles().length; j++)
				{
					Vector3f p1 = collider.getTriangles()[j].getPoints()[0];
					p1 = new Vector3f(p1.x/collTest.eRadius.x, p1.y/collTest.eRadius.y, p1.z/collTest.eRadius.z);
					Vector3f p2 = collider.getTriangles()[j].getPoints()[1];
					p2 = new Vector3f(p2.x/collTest.eRadius.x, p2.y/collTest.eRadius.y, p2.z/collTest.eRadius.z);
					Vector3f p3 = collider.getTriangles()[j].getPoints()[2];
					p3 = new Vector3f(p3.x/collTest.eRadius.x, p3.y/collTest.eRadius.y, p3.z/collTest.eRadius.z);
					Utils.checkTriangle(collTest, p1, p2, p3,collider.getTriangles()[j]);
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
	
	public void equipItem(Entity item)
	{
		int currState = GLFW.glfwGetKey(Window.getWindowID(), GLFW.GLFW_KEY_ENTER);
		if(currState == GLFW.GLFW_PRESS && lastKeyState == GLFW.GLFW_RELEASE)
		{
			EquipInventory inventory = player.getComponentByType(EquipInventory.class);
			if(inventory.isEquipped(item))
			{
				inventory.unequip(item);
			}
			else
			{
				inventory.equip(item);
			}
		}
		
		lastKeyState = currState;
		
	}
	
	private void applyInputs(List<Input> inputs,Motion motion)
	{
		for(Input input: inputs)
		{
			float[] distances = applyInput(input);
			motion.setCurrentVelocity(distances[0], 0, distances[1]);
			applyGravity = false;
			entityCollision(motion);
			player.getAABB().resetBox(player.getPosition());
			ellipse.reset(player.getPosition());
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
				Motion motion = player.getComponentByType(Motion.class);
				applyInputs(snapshot.getInputs(),motion);
				i++;
			}
		}
		
		
		
	}
	
	private float[] applyInput(Input input)
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
		
		return new float[] {dx,dz};
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
	
	public static Map<UUID,Entity> getEntityQueue()
	{
		return entityQueue;
	}
	
	public static ConcurrentMap<UUID,PeerClient> getPeerClients()
	{
		return peerClients;
	}
	
	public static PeerClient getPeerByID(UUID id)
	{
		return peerClients.get(id);
	}
	
	public static Entity getEntityByID(UUID id)
	{
		return entityQueue.get(id);
	}
	
	public static boolean checkPeerExists(UUID id)
	{
		return peerClients.containsKey(id);
	}
	
	public static boolean checkEntityExists(UUID id)
	{
		return entityQueue.containsKey(id);
	}
	
	public static void addPeer(PeerClient peer)
	{
		peerClients.put(peer.getID(),peer);
	}
	
	public static void addEntityToQueue(Entity entity)
	{
		entityQueue.put(entity.getID(), entity);
	}
	
	public static void removePeer(PeerClient peer)
	{
		peerClients.remove(peer.getID());
	}

	public List<Input> getInputs() {
		return inputs;
	}
	
	public UDPClient getUDPClient()
	{
		return udpClient;
	}
	
	public Entity getPlayer()
	{
		return player;
	}
	
	
	

}
