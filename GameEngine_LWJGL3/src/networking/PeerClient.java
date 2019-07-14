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
	
	public PeerClient(UUID id, Entity entity)
	{
		this.id = id;
		this.entity = entity;
	}
	
	public void update()
	{
		interpolatePosition();
		interpolateRotation();
	}
	
	public void interpolatePosition()
	{
		if(previousPosition == null || nextPosition == null)
		{
			return;
		}
		
		Vector3f diffPos = Vector3f.sub(nextPosition, previousPosition, null);
		float velX = diffPos.x / (1/8f);
		float velY = diffPos.y / (1/8f);
		float velZ = diffPos.z / (1/8f);
		
		float dx = velX * Window.getFrameTime();
		float dy = velY * Window.getFrameTime();
		float dz = velZ * Window.getFrameTime();
		
		entity.increasePosition(dx, dy, dz);
	}
	
	public void interpolateRotation()
	{
		float diffX = nextRotX - prevRotX;
		float diffY = nextRotY - prevRotY;
		float diffZ = nextRotZ - prevRotZ;
		
		float rx = (diffX / (1/8f)) * Window.getFrameTime();
		float ry = (diffY / (1/8f)) * Window.getFrameTime();
		float rz = (diffZ / (1/8f)) * Window.getFrameTime();
		
		entity.increaseRotation(rx, ry, rz);
		prevRotX = entity.getRotX();
		prevRotY = entity.getRotY();
		prevRotZ = entity.getRotZ();
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
	
	
	
	
	
	
	
	

}
