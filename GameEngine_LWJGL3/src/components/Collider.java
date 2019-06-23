package components;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import physics.CollisionTest;
import physics.Ellipsoid;
import physics.PairManager;
import physics.Plane;
import physics.SAP;
import physics.Utils;
import rendering.Window;
import terrains.Terrain;

public class Collider extends Component{
	
	private List<Terrain> terrains;
	
	private CollisionTest collTest;
	
	private Ellipsoid ellipse;
	
	private float collisionRecursionDepth;
	
	public Collider(String name, List<Terrain> terrains)
	{
		super(name);
		this.terrains = terrains;
		init();
	}

	@Override
	protected void init() {
		collTest = new CollisionTest();
	}

	@Override
	public void update() {
		if(entity != null)
		{
			Motion motion = entity.getComponentByType(Motion.class);
			if(motion != null)
			{
				entity.increaseRotation(0, motion.getCurrentTurnSpeed() * Window.getFrameTime(), 0);
				float distance = motion.getCurrentSpeed() * Window.getFrameTime();
				float distX = (float)(distance * Math.sin(Math.toRadians(entity.getRotY())));
				float distZ = (float)(distance * Math.cos(Math.toRadians(entity.getRotY())));
				motion.setJumpSpeed(motion.getJumpSpeed() + Motion.getGravity() * Window.getFrameTime()); 
				float distY = motion.getJumpSpeed() * Window.getFrameTime();
				motion.setCurrentVelocity(distX, distY, distZ);
				terrainCollision(motion);
				entityCollision(motion);
				ellipse.reset(entity.getPosition());
				entity.getAABB().resetBox(entity.getPosition());
			}
		}
		
	}
	
	private void terrainCollision(Motion motion)
	{
		float distY = motion.getCurrentVelocity().y;
		for(Terrain terrain: terrains)
		{
			if(terrain.isEntityOnTerrain(entity))
			{
				float terrainHeight = terrain.getTerrainHeight(entity.getPosition().x, entity.getPosition().z);
				if(entity.getPosition().y < terrainHeight)
				{
					motion.setJumpSpeed(0);
					distY = terrainHeight - entity.getPosition().y;
					motion.setAirborne(false);
				}
			}
		}
		motion.setCurrentVelocity(motion.getCurrentVelocity().x, distY, motion.getCurrentVelocity().z);
	}
	
	private void entityCollision(Motion motion)
	{
		if(motion.getCurrentVelocity().length() > 0.00000001f)
		{
			collideAndSlide(motion.getCurrentVelocity(),motion);
		}
	}
	
	private void collideAndSlide(Vector3f velocity, Motion motion)
	{
		collTest.eRadius = ellipse.getRadius();
		collTest.R3Position = entity.getPosition();
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
		
		Vector3f finalPosition = collideWithWorld(eSpacePosition, eSpaceVelocity, motion);
		finalPosition.set(finalPosition.x *collTest.eRadius.x, 
				(finalPosition.y *collTest.eRadius.y) - collTest.eRadius.y, 
				finalPosition.z*collTest.eRadius.z);
		entity.setPosition(finalPosition);
	}
	
	private Vector3f collideWithWorld(Vector3f eSpacePos, Vector3f eSpaceVel, Motion motion)
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
		motion.setAirborne(false);
		
		
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
		return collideWithWorld(newBasePoint,newVelocityVector,motion);
	}
	
	private void checkCollision()
	{
		for(int i = 0; i < SAP.activeList.size(); i++)
		{
			PairManager current = SAP.activeList.get(i);
			Entity first = current.getFirst();
			Entity second = current.getSecond();
			Entity collider = null;
			
			if(first.equals(this.entity))
			{
				collider = second;
			}
			
			if(second.equals(this.entity))
			{
				collider = first;
			}
			
			if(collider != null)
			{
				for(int j = 0; j < collider.getTriangles().length; j++)
				{
					Vector3f p1 = collider.getTriangles()[j].getPoints()[0];
					p1 = new Vector3f(p1.x/collTest.eRadius.x, p1.y/collTest.eRadius.y, p1.z/collTest.eRadius.z);
					Vector3f p2 = collider.getTriangles()[j].getPoints()[1];
					p2 = new Vector3f(p2.x/collTest.eRadius.x, p2.y/collTest.eRadius.y, p2.z/collTest.eRadius.z);
					Vector3f p3 = collider.getTriangles()[j].getPoints()[2];
					p3 = new Vector3f(p3.x/collTest.eRadius.x, p3.y/collTest.eRadius.y, p3.z/collTest.eRadius.z);
					Utils.checkTriangle(collTest, p1, p2, p3);
				}
			}
		}
	}

	@Override
	public void start() {
		ellipse = new Ellipsoid(entity);
		
	}

	@Override
	protected void cleanUp() {
		loader.cleanUp();	
	}

}
