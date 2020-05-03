package physics;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import utils.Maths;

public class Ellipsoid {
	private Vector3f centre;
	private Vector3f radius; // Store radius X, Y & Z
	private Entity entity;
	
	private Vector3f rotation;
	
	public Ellipsoid(Entity entity)
	{
		this.entity = entity;
		this.centre = this.entity.calculateCentre();
		float xRadius = this.entity.getModelWidth() / 2;
		float yRadius = this.entity.getModelHeight() / 2;
		float zRadius = this.entity.getModelZWidth() / 2;
		this.radius = new Vector3f(xRadius, yRadius, zRadius);
		this.rotation = new Vector3f(0,0,0);
	}
	
	public Vector3f getRadius()
	{
		return radius;
	}
	
	public Vector3f getCentre()
	{
		return centre;
	}
	
	public Vector3f getRotation()
	{
		return rotation;
	}
	
	public void setRotation(float x, float y, float z)
	{
		this.rotation = new Vector3f(x,y,z);
	}
	
	public void setY()
	{
		centre.y = entity.getPosition().y + radius.y;
	}
	
	public void reset(Vector3f position)
	{
		calculateEllipsoid();
		Vector3f.add(centre, position, centre);
	}
	
	private void calculateEllipsoid()
	{
		Vector3f minVert = entity.findMinVertex();
		Vector3f maxVert = entity.findMaxVertex();
		minVert = Maths.rotate3DVector(minVert, rotation);
		maxVert = Maths.rotate3DVector(maxVert, rotation);
		float maxX = maxVert.x > minVert.x ? maxVert.x : minVert.x;
		float maxY = maxVert.y > minVert.y ? maxVert.y : minVert.y;
		float maxZ = maxVert.z > minVert.z ? maxVert.z : minVert.z;
		float minX = maxVert.x < minVert.x ? maxVert.x : minVert.x;
		float minY = maxVert.y < minVert.y ? maxVert.y : minVert.y;
		float minZ = maxVert.z < minVert.z ? maxVert.z : minVert.z;
		minVert = new Vector3f(minX,minY,minZ);
		maxVert = new Vector3f(maxX,maxY,maxZ);
		float radiusX = (maxVert.x - minVert.x)/2f;
		float radiusY = (maxVert.y - minVert.y)/2f;
		float radiusZ = (maxVert.z - minVert.z)/2f;
		float midPointX = minVert.x + radiusX;
		float midPointY = minVert.y + radiusY;
		float midPointZ = minVert.z + radiusZ;
		centre = new Vector3f(midPointX, midPointY, midPointZ);
		radius = new Vector3f(radiusX,radiusY,radiusZ);
	}
	
	

}
