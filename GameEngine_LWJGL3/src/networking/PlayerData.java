package networking;

import org.lwjgl.util.vector.Vector3f;

import animation.AnimatedCharacter;
import entities.Entity;

public class PlayerData {
	
	private Vector3f previousPosition;
	private Vector3f nextPosition;
	private Entity entity;
	private AnimatedCharacter animChar;
	
	public PlayerData(Entity entity, AnimatedCharacter animChar)
	{
		this.entity = entity;
		this.animChar = animChar;
	}

	public Entity getEntity() {
		return entity;
	}

	public AnimatedCharacter getAnimChar() {
		return animChar;
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
	
	
	
	
	

}