package networking;

import java.util.UUID;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import rendering.Window;

public class PeerClient {
	
	private UUID id;
	
	private Entity entity;
	
	private Vector3f previousPosition;
	private Vector3f nextPosition;
	
	private float prevRotX, prevRotY, prevRotZ;
	private float nextRotX, nextRotY, nextRotZ;
	
	private long prevTimeStamp;
	private long nextTimeStamp;
	
	public PeerClient(UUID id, Entity entity)
	{
		this.id = id;
		this.entity = entity;
		this.previousPosition = new Vector3f(0,0,0);
		this.nextPosition = new Vector3f(0,0,0);
	}
	
	public void update()
	{
		float timeDelta = ((nextTimeStamp - prevTimeStamp) / 1000f) + 1/8f;
		interpolatePosition(timeDelta);
		interpolateRotation(timeDelta);
	}
	
	private void interpolatePosition(float time)
	{
		Vector3f diffPos = Vector3f.sub(nextPosition, previousPosition, null);
		float velX = diffPos.x / time;
		float velY = diffPos.y / time;
		float velZ = diffPos.z / time;
		
		float dx = velX * Window.getFrameTime();
		float dy = velY * Window.getFrameTime();
		float dz = velZ * Window.getFrameTime();
		
		entity.increasePosition(dx,dy,dz);
	}
	
	private void interpolateRotation(float time)
	{
		float diffX = nextRotX - entity.getRotX();
		float diffY = nextRotY - entity.getRotY();
		float diffZ = nextRotZ - entity.getRotZ();
		
		float rx = (diffX / time) * Window.getFrameTime();
		float ry = (diffY / time) * Window.getFrameTime();
		float rz = (diffZ / time) * Window.getFrameTime();
		
		entity.increaseRotation(rx,ry,rz);
	}
	
	public UUID getID() {
		return id;
	}

	public void setID(UUID id) {
		this.id = id;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Vector3f getPreviousPosition() {
		return previousPosition;
	}

	public void setPreviousPosition(Vector3f previousPosition) {
		this.previousPosition = previousPosition;
	}

	public Vector3f getNextPosition() {
		return nextPosition;
	}

	public void setNextPosition(Vector3f nextPosition) {
		this.nextPosition = nextPosition;
	}

	public float getPrevRotX() {
		return prevRotX;
	}

	public void setPrevRotX(float prevRotX) {
		this.prevRotX = prevRotX;
	}

	public float getPrevRotY() {
		return prevRotY;
	}

	public void setPrevRotY(float prevRotY) {
		this.prevRotY = prevRotY;
	}

	public float getPrevRotZ() {
		return prevRotZ;
	}

	public void setPrevRotZ(float prevRotZ) {
		this.prevRotZ = prevRotZ;
	}

	public float getNextRotX() {
		return nextRotX;
	}

	public void setNextRotX(float nextRotX) {
		this.nextRotX = nextRotX;
	}

	public float getNextRotY() {
		return nextRotY;
	}

	public void setNextRotY(float nextRotY) {
		this.nextRotY = nextRotY;
	}

	public float getNextRotZ() {
		return nextRotZ;
	}

	public void setNextRotZ(float nextRotZ) {
		this.nextRotZ = nextRotZ;
	}

	public long getPrevTimeStamp() {
		return prevTimeStamp;
	}

	public void setPrevTimeStamp(long prevTimeStamp) {
		this.prevTimeStamp = prevTimeStamp;
	}

	public long getNextTimeStamp() {
		return nextTimeStamp;
	}

	public void setNextTimeStamp(long nextTimeStamp) {
		this.nextTimeStamp = nextTimeStamp;
	}
	
	
	
	
	
	
	
	
	
	

}
