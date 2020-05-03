package physics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import entities.Entity;
import models.BaseModel;

public class AABB {
	
	// Axis Aligned Bounding Box Class
	
	private static final float BOX_OFFSET = 0.5f;
	
	private Vector3f minPoint, maxPoint, centre, rotation;
	private float minX, minY, minZ, maxX, maxY, maxZ, width, height, zWidth;
	
	// Keep track of end points
	private EndPoint[] maxPoints;
	private EndPoint[] minPoints;
	
	private Entity entity;
	
 
	public AABB(Entity entity, Vector3f pos)
	{
		this.entity = entity;
		this.minPoint = entity.findMinVertex();
		this.maxPoint = entity.findMaxVertex();
		this.width = entity.getModelWidth();
		this.height = entity.getModelHeight();
		this.zWidth = entity.getModelZWidth();
		float scale = entity.getScale();
		this.minX = (minPoint.x * scale + pos.x - BOX_OFFSET);
		this.minY = (minPoint.y * scale + pos.y - BOX_OFFSET) ;
		this.minZ = (minPoint.z * scale + pos.z - BOX_OFFSET) ;
		this.maxX = (maxPoint.x * scale + pos.x + BOX_OFFSET) ;
		this.maxY = (maxPoint.y * scale + pos.y + BOX_OFFSET) ;
		this.maxZ = (maxPoint.z * scale + pos.z + BOX_OFFSET) ;
		this.minPoint = new Vector3f(minX,minY,minZ);
		this.maxPoint = new Vector3f(maxX,maxY,maxZ);
		this.centre = entity.calculateCentre();
		float centreX = centre.x + pos.x;
		float centreY = centre.y + pos.y;
		float centreZ = centre.z + pos.z;
		this.centre = new Vector3f(centreX, centreY, centreZ);
		this.minPoints = new EndPoint[] {new EndPoint(this, minPoint.x, true),
				new EndPoint(this, minPoint.y, true),new EndPoint(this, minPoint.z, true)};
		this.maxPoints = new EndPoint[] {new EndPoint(this, maxPoint.x, false),
				new EndPoint(this, maxPoint.y, false),new EndPoint(this, maxPoint.z, false)};
		this.rotation = new Vector3f(0,0,0);
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
	
	public void setRotation(float x, float y, float z)
	{
		this.rotation = new Vector3f(x,y,z);
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
		//System.out.println("A: " + distA);
		//System.out.println("B: " + distB);
		if(distA > 0 || distB > 0)
		{
			return false;
		}
		
		return true;
	}
	
	public void resetBox(Vector3f position)
	{
		Matrix4f rotMatrix = new Matrix4f();
		Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0,0,1), rotMatrix, rotMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0,1,0), rotMatrix, rotMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1,0,0), rotMatrix, rotMatrix);
		this.minPoint = entity.findMinVertex();
		this.maxPoint = entity.findMaxVertex();
		Vector4f tempMin = Matrix4f.transform(rotMatrix, new Vector4f(minPoint.x,minPoint.y,minPoint.z,1.0f), null);
		Vector4f tempMax = Matrix4f.transform(rotMatrix, new Vector4f(maxPoint.x,maxPoint.y,maxPoint.z,1.0f), null);
		this.minPoint = new Vector3f(tempMin.x,tempMin.y,tempMin.z);
		this.maxPoint = new Vector3f(tempMax.x,tempMax.y,tempMax.z);
		this.minX = (minPoint.x + position.x);
		this.minY = (minPoint.y + position.y);
		this.minZ = (minPoint.z + position.z);
		this.maxX = (maxPoint.x + position.x);
		this.maxY = (maxPoint.y + position.y);
		this.maxZ = (maxPoint.z + position.z);
		this.minPoint = new Vector3f(minX,minY,minZ);
		this.maxPoint = new Vector3f(maxX,maxY,maxZ);
		float maxX = maxPoint.x > minPoint.x ? maxPoint.x : minPoint.x;
		float maxY = maxPoint.y > minPoint.y ? maxPoint.y : minPoint.y;
		float maxZ = maxPoint.z > minPoint.z ? maxPoint.z : minPoint.z;
		float minX = maxPoint.x < minPoint.x ? maxPoint.x : minPoint.x;
		float minY = maxPoint.y < minPoint.y ? maxPoint.y : minPoint.y;
		float minZ = maxPoint.z < minPoint.z ? maxPoint.z : minPoint.z;
		this.minPoint = new Vector3f(minX,minY,minZ);
		this.maxPoint = new Vector3f(maxX,maxY,maxZ);
		updateEndPoints();
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
	
	public void setY(Vector3f pos)
	{
		this.minPoint.y = this.entity.findMinVertex().y + pos.y; 
		this.maxPoint.y = this.entity.findMaxVertex().y + pos.y;
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
		BaseModel otherModel = other.getEntity().getModel().getBaseModel();
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
