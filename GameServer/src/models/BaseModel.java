package models;

import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.util.vector.Vector3f;

public class BaseModel {
	
	/*
	 * Subject to change once a database is implemented
	 * ID's and model data will be stored in the database
	 * For testing purposes, simply use auto increment ID
	 */
	
	private AtomicInteger ID = new AtomicInteger(0);
	private int vertCount;
	
	private float[] vertices;
	private int[] indices;
	
	public BaseModel(int vertCount, float[] vertices, int[] indices)
	{
		ID.incrementAndGet();
		this.vertCount = vertCount;
		this.vertices = vertices;
		this.indices = indices;
	}
	
	public BaseModel(int vertCount)
	{
		ID.incrementAndGet();
		this.vertCount = vertCount;
	}
	
	public AtomicInteger getID() {
		return ID;
	}

	public int getVertCount() {
		return vertCount;
	}
	
	public float[] getVertices()
	{
		return vertices;
	}
	
	public void setVertices(float[] verts)
	{
		this.vertices = verts;
	}

	public int[] getIndices()
	{
		return indices;
	}
	
	public Vector3f findMinVertex()
	{
		Vector3f minVert = null;
		float xMin = this.vertices[0];
		float yMin = this.vertices[1];
		float zMin = this.vertices[2];
		int vertCounter = 0;
		for(int i = 0; i < this.vertices.length/3; i++)
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
		
		minVert = new Vector3f(xMin, yMin, zMin);
		
		return minVert;
	}
	
	public Vector3f findMaxVertex()
	{
		Vector3f maxVert = null;
		float xMax = this.vertices[0];
		float yMax = this.vertices[1];
		float zMax = this.vertices[2];
		int vertCounter = 0;
		for(int i = 0; i < this.vertices.length/3; i++)
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
		maxVert = new Vector3f(xMax, yMax, zMax);
		
		return maxVert;
	}
	
	public Vector3f calculateCentre()
	{
		Vector3f centre;
		float midPointX = findMinVertex().x + ((findMaxVertex().x - findMinVertex().x)/2);
		float midPointY =findMinVertex().y + ((findMaxVertex().y - findMinVertex().y)/2);
		float midPointZ = findMinVertex().z + ((findMaxVertex().z - findMinVertex().z)/2);
		centre = new Vector3f(midPointX, midPointY, midPointZ);
		return centre;
	}
	
	public float getModelHeight()
	{
		float height = 0;
		Vector3f min = this.findMinVertex();
		Vector3f max = this.findMaxVertex();
		
		height = max.y - min.y;
		
		return height;
	}
	
	public float getModelWidth()
	{
		float width = 0;
		Vector3f min = this.findMinVertex();
		Vector3f max = this.findMaxVertex();
		
		width = max.x - min.x;
		
		return width;
	}
	
	public float getModelZWidth()
	{
		float length = 0;
		Vector3f min = this.findMinVertex();
		Vector3f max = this.findMaxVertex();
		
		length = max.z - min.z;
		
		return length;
	}
	
	public int compareTo(BaseModel other)
	{
		return this.getID().get() - other.getID().get();
	}
	
	
	
}
