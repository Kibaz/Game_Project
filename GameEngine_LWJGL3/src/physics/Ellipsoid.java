package physics;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import utils.Maths;

public class Ellipsoid {
	private Vector3f centre;
	private Vector3f radius; // Store radius X, Y & Z
	private Entity entity;
	
	private Vector3f rotation;
	
	// Transformed Model information
	private float[] vertices;
	
	public Ellipsoid(Entity entity)
	{
		this.entity = entity;
		this.centre = this.entity.getModel().getBaseModel().calculateCentre();
		float xRadius = this.entity.getModel().getBaseModel().getModelWidth() / 2;
		float yRadius = this.entity.getModel().getBaseModel().getModelHeight() / 2;
		float zRadius = (this.entity.getModel().getBaseModel().getModelZWidth())/ 2;
		this.radius = new Vector3f(xRadius, yRadius, zRadius);
		this.vertices = this.entity.getModel().getBaseModel().getVertices();
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
	
	private Vector3f findMinVertex()
	{
		Vector3f minVert = null;
		float xMin = vertices[0];
		float yMin = vertices[1];
		float zMin = vertices[2];
		int vertCounter = 0;
		for(int i = 0; i < vertices.length/3; i++)
		{
			if(vertices[vertCounter] < xMin)
			{
				xMin = vertices[vertCounter];
			}
			vertCounter++;
			if(vertices[vertCounter] < yMin)
			{
				yMin = vertices[vertCounter];
				
			}
			vertCounter++;
			if(vertices[vertCounter] < zMin)
			{
				zMin = vertices[vertCounter];
				
			}
			vertCounter++;
		}
		minVert = Maths.rotate3DVector(new Vector3f(xMin, yMin, zMin), rotation);
		
		return minVert;
	}
	
	private Vector3f findMaxVertex()
	{
		Vector3f maxVert = null;
		float xMax = vertices[0];
		float yMax = vertices[1];
		float zMax = vertices[2];
		int vertCounter = 0;
		for(int i = 0; i < vertices.length/3; i++)
		{
			if(vertices[vertCounter] > xMax)
			{
				xMax = vertices[vertCounter];
			}
			vertCounter++;
			if(vertices[vertCounter] > yMax)
			{
				yMax = vertices[vertCounter];
			}
			vertCounter++;
			if(vertices[vertCounter] > zMax)
			{
				zMax = vertices[vertCounter];
			}
			vertCounter++;
		}
		maxVert = Maths.rotate3DVector(new Vector3f(xMax, yMax, zMax), rotation);
		
		return maxVert;
	}
	
	private void calculateEllipsoid()
	{
		Vector3f minVert = findMinVertex();
		Vector3f maxVert = findMaxVertex();
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
