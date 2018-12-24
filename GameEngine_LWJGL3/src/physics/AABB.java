package physics;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.BaseModel;

public class AABB {

	// Axis Aligned Bounding Box Class
	
	private Vector3f minPoint, maxPoint, centre;
	private float minX, minY, minZ, maxX, maxY, maxZ, width, height, zWidth;
	
	// Keep track of end points
	private EndPoint[] maxPoints;
	private EndPoint[] minPoints;
	
	private Entity entity;
	
 
	public AABB(Entity entity, Vector3f pos)
	{
		this.entity = entity;
		this.minPoint = entity.getModel().getBaseModel().findMinVertex();
		this.maxPoint = entity.getModel().getBaseModel().findMaxVertex();
		this.width = entity.getModel().getBaseModel().getModelWidth();
		this.height = entity.getModel().getBaseModel().getModelHeight();
		this.zWidth = entity.getModel().getBaseModel().getModelZWidth();
		this.minX = (minPoint.x * entity.getScale() + pos.x);
		this.minY = (minPoint.y * entity.getScale() + pos.y);
		this.minZ = (minPoint.z * entity.getScale() + pos.z);
		this.maxX = (maxPoint.x * entity.getScale() + pos.x);
		this.maxY = (maxPoint.y * entity.getScale() + pos.y);
		this.maxZ = (maxPoint.z * entity.getScale() + pos.z);
		this.minPoint = new Vector3f(minX,minY,minZ);
		this.maxPoint = new Vector3f(maxX,maxY,maxZ);
		this.centre = entity.getModel().getBaseModel().calculateCentre();
		float centreX = centre.x + pos.x;
		float centreY = centre.y + pos.y;
		float centreZ = centre.z + pos.z;
		this.centre = new Vector3f(centreX, centreY, centreZ);
		this.minPoints = new EndPoint[] {new EndPoint(this, minPoint.x, true),
				new EndPoint(this, minPoint.y, true),new EndPoint(this, minPoint.z, true)};
		this.maxPoints = new EndPoint[] {new EndPoint(this, maxPoint.x, false),
				new EndPoint(this, maxPoint.y, false),new EndPoint(this, maxPoint.z, false)};
	}

	public Vector3f getMinPoint() {
		return minPoint;
	}

	public Vector3f getMaxPoint() {
		return maxPoint;
	}
	
	public Vector3f getCentre()
	{
		return centre;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public float getZWidth()
	{
		return zWidth;
	}
	
	public boolean intersectsAABB(AABB other)
	{
		float distX = this.minPoint.x - other.maxPoint.x;
		float distY = this.minPoint.y - other.maxPoint.y;
		float distZ = this.minPoint.z - other.maxPoint.z;
		float distX2 = other.minPoint.x - this.maxPoint.x;
		float distY2 = other.minPoint.y - this.maxPoint.y;
		float distZ2 = other.minPoint.z - this.maxPoint.z;
		
		if(distX > 0 || distY > 0 || distZ > 0 || distX2 > 0 || distY2 > 0 || distZ2 > 0)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean intersectOnXAxis(AABB other)
	{
		float distA = this.minPoint.x - other.maxPoint.x;
		float distB = other.minPoint.x - this.maxPoint.x;
		if(distA > 0 || distB > 0)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean intersectOnYAxis(AABB other)
	{
		float distA = this.minPoint.y - other.maxPoint.y;
		float distB = other.minPoint.y - this.maxPoint.y;
		if(distA > 0 || distB > 0)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean intersectOnZAxis(AABB other)
	{
		float distA = this.minPoint.z - other.maxPoint.z;
		float distB = other.minPoint.z - this.maxPoint.z;
		if(distA > 0 || distB > 0)
		{
			return false;
		}
		
		return true;
	}
	
	public void resetBox(Vector3f position)
	{
		BaseModel model = this.entity.getModel().getBaseModel();
		this.minPoint = model.findMinVertex();
		this.maxPoint = model.findMaxVertex();
		this.minX = (minPoint.x + position.x);
		this.minY = (minPoint.y + position.y);
		this.minZ = (minPoint.z + position.z);
		this.maxX = (maxPoint.x + position.x);
		this.maxY = (maxPoint.y + position.y);
		this.maxZ = (maxPoint.z + position.z);
		this.minPoint = new Vector3f(minX,minY,minZ);
		this.maxPoint = new Vector3f(maxX,maxY,maxZ);
	}
	
	// For entities which move position
	public void moveAABB(float dx, float dy, float dz)
	{
		this.minPoint.x += dx;
		this.minPoint.y += dy;
		this.minPoint.z += dz;
		this.maxPoint.x += dx;
		this.maxPoint.y += dy;
		this.maxPoint.z += dz;
		this.centre.x += dx;
		this.centre.y += dy;
		this.centre.z += dz;
		updateEndPoints();
	}
	
	private void updateEndPoints()
	{
		maxPoints[0].setValue(maxPoint.x);
		maxPoints[1].setValue(maxPoint.y);
		maxPoints[2].setValue(maxPoint.z);
		
		minPoints[0].setValue(minPoint.x);
		minPoints[1].setValue(minPoint.y);
		minPoints[2].setValue(minPoint.z);
	}
	
	public void setY(BaseModel model, Vector3f pos)
	{
		this.minPoint.y = model.findMinVertex().y + pos.y; 
		this.maxPoint.y = model.findMaxVertex().y + pos.y;
	}

	public EndPoint[] getMax() {
		return maxPoints;
	}

	public EndPoint[] getMin() {
		return minPoints;
	}
	
	public Entity getEntity()
	{
		return entity;
	}
	
	public boolean isEqual(AABB other)
	{
		BaseModel thisModel = this.entity.getModel().getBaseModel();
		BaseModel otherModel = this.entity.getModel().getBaseModel();
		if(thisModel.getVaoID() == otherModel.getVaoID() && checkEqualCentre(other.centre))
		{
			return true;
		}
		
		return false;
	}
	
	private boolean checkEqualCentre(Vector3f other)
	{
		if(this.centre.x == other.x && 
				this.centre.y == other.y && 
				this.centre.z == other.z)
		{
			return true;
		}
		
		return false;
	}
	
	
	
	
	
}
