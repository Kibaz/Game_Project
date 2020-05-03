package combat;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.BaseModel;
import rendering.Loader;
import terrains.Terrain;

public abstract class DamageIndicator {
	
	protected Vector3f position;
	protected float rotY;
	protected float[] vertices;
	protected BaseModel model;
	
	protected int vertexBufferID;
	
	private boolean enemyIndicator;
	
	public DamageIndicator(Vector3f position, float rotY)
	{
		this.position = position;
		this.rotY = rotY;
		this.enemyIndicator = false;
	}
	
	public abstract void buildIndicator(Loader loader,List<Terrain> terrains);
	
	public abstract void updateIndicator(Loader loader,List<Terrain> terrains);
	
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
	
	public void setEnemyIndicator(boolean isEnemy)
	{
		this.enemyIndicator = isEnemy;
	}
	
	public boolean isEnemyIndicator()
	{
		return this.enemyIndicator;
	}
	
	public int getVertexBufferID()
	{
		return vertexBufferID;
	}
	
	public void setVertexBufferID(int vbo)
	{
		this.vertexBufferID = vbo;
	}
	
	
	

}
