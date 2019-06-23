package combat;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.BaseModel;
import rendering.Loader;

public abstract class DamageIndicator {
	
	protected Vector3f position;
	protected float rotY;
	protected float[] vertices;
	protected BaseModel model;
	
	public DamageIndicator(Vector3f position, float rotY)
	{
		this.position = position;
		this.rotY = rotY;
	}
	
	public abstract void buildIndicator(Loader loader);
	
	public abstract boolean intersectsEntity(Entity entity);

	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}

	public float[] getVertices() {
		return vertices;
	}

	public BaseModel getModel() {
		return model;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}
	
	
	

}
