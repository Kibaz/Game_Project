package physics;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class EndPoint {
	
	private float value;
	private boolean isMin; // flag min or max value
	private AABB box;
	
	public EndPoint(AABB box, float value, boolean isMin)
	{
		this.box = box;
		this.value = value;
		this.isMin = isMin;
		
	}

	public float getValue() {
		return value;
	}

	public AABB getBox() {
		return box;
	}
	
	public boolean isMinValue()
	{
		return isMin;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	
	
	

}
