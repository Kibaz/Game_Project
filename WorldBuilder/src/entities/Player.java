package entities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUITexture;
import inputs.Input;
import inputs.KeyInput;
import inputs.KeyboardHandler;
import inputs.MouseButton;
import models.TexturedModel;
import pathfinding.GridSquare;
import physics.AABB;
import physics.CollisionTest;
import physics.Ellipsoid;
import physics.PairManager;
import physics.Plane;
import physics.SAP;
import physics.Utils;
import rendering.Loader;
import rendering.Window;
import terrains.Terrain;
import utils.DataTransfer;
import water.WaterPlane;

public class Player extends Entity{
	
	// Constants
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float UP_FORCE = 27;
	
	// Player combat variables
	private float maxHealth = 100;
	private float currentHealth = maxHealth;
	private GUITexture healthBar;
	private FontStyle healthTextStyle;
	private GUIText healthInfo;
	
	
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
	
	// Combat variables

	public Player(Loader loader,TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		aabb = new AABB(this, super.getPosition());
		ellipse = new Ellipsoid(this);
		collTest = new CollisionTest();
		super.setStaticModel(false);
	}
	
	public void movePlayer(List<Terrain> terrains, List<WaterPlane> water, List<Entity> entities) 
	{
		checkUserInput(entities);
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
			if(t.isEntityOnTerrain(this))
			{
				float terrainHeight = t.getTerrainHeight(super.getPosition().x, super.getPosition().z);
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
		
		// Ensure bounding box is always reset post collision calculations
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
	
	private void checkUserInput(List<Entity> entities)
	{
		/* Check whether a key has been pressed */
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_W))
		{
			this.currentSpeed = ground_speed;
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S))
		{
			this.currentSpeed = -ground_speed;
		}
		
		else
		{
			this.currentSpeed = 0;
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D))
		{
			this.currentTurnSpeed = -TURN_SPEED;
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A))
		{
			this.currentTurnSpeed = TURN_SPEED;
		}
		else
		{
			this.currentTurnSpeed = 0;
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE)){
			jump();
		}
		
	}
	
	public float getMaxHealth()
	{
		return maxHealth;
	}
	
	public float getHealth()
	{
		return currentHealth;
	}
	
	public void setHealth(float health)
	{
		this.currentHealth = health;
	}
	
	public GUITexture getHealthBar()
	{
		return healthBar;
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
}
