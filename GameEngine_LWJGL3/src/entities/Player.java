package entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import inputs.KeyboardHandler;
import models.BaseModel;
import models.TexturedModel;
import networking.Client;
import physics.AABB;
import physics.CollisionTest;
import physics.Ellipsoid;
import physics.EndPoint;
import physics.PairManager;
import physics.Plane;
import physics.SAP;
import physics.Utils;
import rendering.Window;
import runtime.Main;
import terrains.Terrain;
import texturing.ModelTexture;
import utils.DataTransfer;
import utils.OBJLoader;
import water.WaterPlane;
import water.WaterPlane;
import worldData.World;

public class Player extends Entity{
	
	// Constants
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float UP_FORCE = 27;
	
	// player movement variables
	private float ground_speed = 20;
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float jumpSpeed = 0;
	private float collisionRecursionDepth;
	
	// Axis-Aligned Bounding Box - To track player's position and check for collisions
	private AABB aabb;
	private Ellipsoid ellipse;
	
	private Vector3f velocityVector = new Vector3f(0,0,0);
	
	private boolean airborne = false;
	private CollisionTest collTest;
	
	// Track key states
	private int prev_W_KEY_state = GLFW.GLFW_RELEASE;
	private int prev_S_KEY_state = GLFW.GLFW_RELEASE;
	private int prev_D_KEY_state = GLFW.GLFW_RELEASE;
	private int prev_A_KEY_state = GLFW.GLFW_RELEASE;
	private int prev_SPACE_KEY_state = GLFW.GLFW_RELEASE;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		aabb = new AABB(this, super.getPosition());
		ellipse = new Ellipsoid(this);
		collTest = new CollisionTest();
		super.setStaticModel(false);
	}
	
	public void movePlayer(List<Terrain> terrains, List<WaterPlane> water, List<Entity> entities) 
	{
		checkUserInput();
		super.increaseRotation(0, currentTurnSpeed * Window.getFrameTime(), 0);
		float distance = currentSpeed * Window.getFrameTime();
		float distX = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float distZ = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		//super.increasePosition(distX, 0, distZ);
		// After increasing player's position, increase bounding box's position
		aabb.moveAABB(distX, 0, distZ);
		ellipse.moveCentre(distX, 0, distZ);
		jumpSpeed += GRAVITY * Window.getFrameTime();
		float distY = jumpSpeed * Window.getFrameTime();
		//super.increasePosition(0, jumpSpeed * Window.getFrameTime(),0);
		aabb.moveAABB(0, jumpSpeed * Window.getFrameTime(), 0);
		ellipse.moveCentre(0, jumpSpeed * Window.getFrameTime(), 0);
		for(Terrain t: terrains)
		{
			float terrainHeight = t.getTerrainHeight(super.getPosition().x, super.getPosition().z);
			if(t.isPlayerOnTerrain(this))
			{
				if(super.getPosition().y < terrainHeight)
				{
					jumpSpeed = 0;
					distY = terrainHeight - super.getPosition().y;
					//super.getPosition().setY(terrainHeight);
					aabb.setY(super.getModel().getBaseModel(), super.getPosition());
					ellipse.setY(super.getModel().getBaseModel(), super.getPosition());
					airborne = false;
				}
			}

		}
		
		for(WaterPlane model: water)
		{
			if(model.isPlayerInWater(this))
			{
				
			}
		}
		
		velocityVector.set(distX, distY, distZ);
		
		if(velocityVector.length() > 0.00000001f)
		{
			collideAndSlide(velocityVector);
		}

		
		// Enusre bounding box is always reset post collision calculations
		this.aabb.resetBox(this.getPosition());
	}
	
	private void jump()
	{
		if(!airborne)
		{
			jumpSpeed = UP_FORCE;
			airborne = true;
		}
	}
	
	private void checkUserInput()
	{
		/* Check whether a key has been pressed */
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_W))
		{
			this.currentSpeed = ground_speed;
			if(prev_W_KEY_state == GLFW.GLFW_RELEASE)
			{
				/* Send packet to server - client pressed w key */
				String msg = "w key pressed by client " + Client.ID;
				Client.send(msg.getBytes());
			}
			prev_W_KEY_state = GLFW.GLFW_PRESS;
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S))
		{
			this.currentSpeed = -ground_speed;
			if(prev_S_KEY_state == GLFW.GLFW_RELEASE)
			{
				/* Send packet to server - client pressed w key */
				String msg = "s key pressed by client " + Client.ID;
				Client.send(msg.getBytes());
			}
			prev_S_KEY_state = GLFW.GLFW_PRESS;
		}
		
		else
		{
			this.currentSpeed = 0;
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D))
		{
			this.currentTurnSpeed = -TURN_SPEED;
			if(prev_D_KEY_state == GLFW.GLFW_RELEASE)
			{
				/* Send packet to server - client pressed w key */
				String msg = "d key pressed by client " + Client.ID;
				Client.send(msg.getBytes());
			}
			prev_D_KEY_state = GLFW.GLFW_PRESS;
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A))
		{
			this.currentTurnSpeed = TURN_SPEED;
			if(prev_A_KEY_state == GLFW.GLFW_RELEASE)
			{
				/* Send packet to server - client pressed w key */
				String msg = "a key pressed by client " + Client.ID;
				Client.send(msg.getBytes());
			}
			prev_A_KEY_state = GLFW.GLFW_PRESS;
		}
		else
		{
			this.currentTurnSpeed = 0;
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE)){
			jump();
			if(prev_SPACE_KEY_state == GLFW.GLFW_RELEASE)
			{
				/* Send packet to server - client pressed w key */
				String msg = " key pressed by client " + Client.ID;
				Client.send(msg.getBytes());
			}
			prev_SPACE_KEY_state = GLFW.GLFW_PRESS;
		}
		
		/* Check whether a key has been released */
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_W) && prev_W_KEY_state == GLFW.GLFW_PRESS)
		{
			/* Send packet to server - client released w key */
			String msg = "w key released by client " + Client.ID;
			Client.send(msg.getBytes());
			/* Reset previous state to Key Release */
			prev_W_KEY_state = GLFW.GLFW_RELEASE;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_S) && prev_S_KEY_state == GLFW.GLFW_PRESS)
		{
			/* Send packet from client to server */
			String msg = "s key released by client " + Client.ID;
			Client.send(msg.getBytes());
			/* Reset previous state to Key Release */
			prev_S_KEY_state = GLFW.GLFW_RELEASE;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_D) && prev_D_KEY_state == GLFW.GLFW_PRESS)
		{
			/* Send packet from client to server */
			String msg = "d key released by client " + Client.ID;
			Client.send(msg.getBytes());
			/* Reset previous state to Key Release */
			prev_D_KEY_state = GLFW.GLFW_RELEASE;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_A) && prev_A_KEY_state == GLFW.GLFW_PRESS)
		{
			/* Send packet from client to server */
			String msg = "a key released by client " + Client.ID;
			Client.send(msg.getBytes());
			/* Reset previous state to Key Release */
			prev_A_KEY_state = GLFW.GLFW_RELEASE;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_SPACE) && prev_SPACE_KEY_state == GLFW.GLFW_PRESS)
		{
			/* Send packet from client to server */
			String msg = "space key released by client " + Client.ID;
			Client.send(msg.getBytes());
			/* Reset previous state to Key Release */
			prev_SPACE_KEY_state = GLFW.GLFW_RELEASE;
		}
		
	}
	
	public AABB getAABB()
	{
		return aabb;
	}
	
	public Ellipsoid getEllipsoid()
	{
		return ellipse;
	}
	
	public float getVelocity()
	{
		return currentSpeed;
	}
	
	public Vector3f getVelocityVector()
	{
		return velocityVector;
	}
	
	public void collideAndSlide(Vector3f velocity)
	{
		collTest.eRadius = this.getEllipsoid().getRadius();
		collTest.R3Position = super.getPosition();
		collTest.R3Position.set(collTest.R3Position.x, collTest.R3Position.y + collTest.eRadius.y, collTest.R3Position.z);
		collTest.R3Velocity = velocity;
		
		// calculate position and velocity in eSpace
		Vector3f eSpacePosition = new Vector3f(collTest.R3Position.x/collTest.eRadius.x,
				collTest.R3Position.y/collTest.eRadius.y,
				collTest.R3Position.z/collTest.eRadius.z);
		Vector3f eSpaceVelocity = new Vector3f(collTest.R3Velocity.x/collTest.eRadius.x,
				collTest.R3Velocity.y/collTest.eRadius.y,
				collTest.R3Velocity.z/collTest.eRadius.z);
		
		// Iterate until we have our final position
		collisionRecursionDepth = 0;
		
		Vector3f finalPosition = collideWithWorld(eSpacePosition, eSpaceVelocity);
		finalPosition.set(finalPosition.x *collTest.eRadius.x, 
				(finalPosition.y *collTest.eRadius.y) - collTest.eRadius.y, 
				finalPosition.z*collTest.eRadius.z);
		super.setPosition(finalPosition);
	}
	
	public Vector3f collideWithWorld(Vector3f eSpacePos, Vector3f eSpaceVel)
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
		
		// Collision Occurred
		airborne = false;
		
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
		return collideWithWorld(newBasePoint,newVelocityVector);
	}
	
	public void checkCollision()
	{
		for(int i = 0; i < SAP.activeList.size(); i++)
		{
			PairManager current = SAP.activeList.get(i);
			Entity first = current.getFirst();
			Entity second = current.getSecond();
			Entity entity = null;
			if(first instanceof Player)
			{
				entity = second;
			}
			else
			{
				entity = first;
			}
			
			for(int j = 0; j < entity.getTriangles().length; j++)
			{
				Vector3f p1 = entity.getTriangles()[j].getPoints()[0];
				p1 = new Vector3f(p1.x/collTest.eRadius.x, p1.y/collTest.eRadius.y, p1.z/collTest.eRadius.z);
				Vector3f p2 = entity.getTriangles()[j].getPoints()[1];
				p2 = new Vector3f(p2.x/collTest.eRadius.x, p2.y/collTest.eRadius.y, p2.z/collTest.eRadius.z);
				Vector3f p3 = entity.getTriangles()[j].getPoints()[2];
				p3 = new Vector3f(p3.x/collTest.eRadius.x, p3.y/collTest.eRadius.y, p3.z/collTest.eRadius.z);
				Utils.checkTriangle(collTest, p1, p2, p3);
			}
		}
	}
	
	// Useful methods
	public String positionToString()
	{
		String result = "";
		
		result = this.getPosition().x + ", " + this.getPosition().y + ", " + this.getPosition().z
				+ ", "  + this.getRotX() + ", " + this.getRotY() + ", " + this.getRotZ();
		
		return result;
	}
	
	/*
	 *  Converting necessary data to bytes to prepare
	 *  for sending across the network to the server
	 *  via datagram packets (UDP Protocol)
	 */
	
	public byte[] getPlayerData()
	{
		byte[] data = null;
		
		List<byte[]> byteArrays = new ArrayList<byte[]>();
		
		/*
		 * Convert all positional and rotational data using ByteBuffer class
		 */
		byte[] header = "PlayerData".getBytes();
		byte[] xPos = DataTransfer.floatToBytes(this.getPosition().x);
		byte[] yPos = DataTransfer.floatToBytes(this.getPosition().y);
		byte[] zPos = DataTransfer.floatToBytes(this.getPosition().z);
		byte[] xRot = DataTransfer.floatToBytes(this.getRotX());
		byte[] yRot = DataTransfer.floatToBytes(this.getRotY());
		byte[] zRot = DataTransfer.floatToBytes(this.getRotZ());
		
		// Converting model data
		byte[] modelData = DataTransfer.integerToBytes(this.getModel().getBaseModel().getVaoID());
		
		byteArrays.add(header);
		byteArrays.add(xPos);
		byteArrays.add(yPos);
		byteArrays.add(zPos);
		byteArrays.add(xRot);
		byteArrays.add(yRot);
		byteArrays.add(zRot);
		byteArrays.add(modelData);
		
		int length = header.length + xPos.length + yPos.length + zPos.length + 
				xRot.length + yRot.length + zRot.length + modelData.length;
		
		// finalise data
		data = new byte[length];
		int index = 0;
		for(int i = 0; i < byteArrays.size(); i++)
		{
			for(int j = 0; j < byteArrays.get(i).length; j++)
			{
				data[index] = byteArrays.get(i)[j];
				index++;
			}
		}
		
		return data;
	}

}
