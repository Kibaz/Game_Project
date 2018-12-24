package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import physics.AABB;

public class Mob extends Entity{
	
	// Fields
	private float ground_speed; // Regular movement speed
	private float turn_speed; // Speed at which the NPC will turn
	
	private AABB aabb;
	
	// Control attackable/non-attackable NPCs
	private boolean isFriendly = false;

	// Constructor
	public Mob(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		// set class defaults
		this.ground_speed = 20;
		this.turn_speed = 160;
		this.aabb = new AABB(this, position);
	}
	
	/* 
	 * Method to invoke the movement of a Mob
	 * I.E a 3D mobile non-player character
	 */
	private void move()
	{
		
	}
	
	private boolean isPlayerInAggroRange()
	{
		return false;
	}

}
