package entityData;

import org.lwjgl.util.vector.Vector3f;

public class PlayerData {
	
	// Static constants
	public static final float GROUND_SPEED = 20;
	public static final float TURN_SPEED = 160;
	public static final float UP_FORCE = 27;
	public static final float GRAVITY = -50;
	
	// Movement variables
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float jumpSpeed = 0;
	private boolean airborne = false;
	
	private Vector3f position; // Store a client's character's position
	// Store rotational data
	private float rotX;
	private float rotY;
	private float rotZ;
	
	// Just in case...
	private float scale;
	
	// Constructor
	public PlayerData(Vector3f position, float rotX, float rotY, float rotZ, float scale)
	{
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}
	
	// Player Movement
	public void increasePosition(float dx, float dy, float dz)
	{
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void increaseRotation(float rx, float ry, float rz)
	{
		this.rotX += rx;
		this.rotY += ry;
		this.rotZ += rz;
	}

	// Getters and Setters
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(float currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public float getJumpSpeed() {
		return jumpSpeed;
	}

	public void setJumpSpeed(float jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}

	public boolean isAirborne() {
		return airborne;
	}

	public void setAirborne(boolean airborne) {
		this.airborne = airborne;
	}

	public float getCurrentTurnSpeed() {
		return currentTurnSpeed;
	}

	public void setCurrentTurnSpeed(float currentTurnSpeed) {
		this.currentTurnSpeed = currentTurnSpeed;
	}
	
	
	
	
	
	

}
