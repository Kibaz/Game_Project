package guis;

import org.lwjgl.util.vector.Vector3f;

public class HUD {
	
	private final float MAX_SCALE_X;
	
	private Vector3f position;
	
	private Vector3f scale;
	
	private int texture;
	
	private boolean healthPool;
	
	private float rotation;
	
	public HUD(int texture, Vector3f position, Vector3f scale, float rotation)
	{
		this.texture = texture;
		this.position = position;
		this.scale = scale;
		this.rotation = rotation;
		MAX_SCALE_X = scale.x;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}

	public int getTexture() {
		return texture;
	}

	public Vector3f getScale() {
		return scale;
	}
	
	public void setScale(Vector3f scale)
	{
		this.scale = scale;
	}
	

	public float getRotation() {
		return rotation;
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	public boolean isHealthPool()
	{
		return healthPool;
	}
	
	public void setAsHealthPool()
	{
		this.healthPool = true;
	}
	
	public float getMaxScaleX()
	{
		return MAX_SCALE_X;
	}
	

}
