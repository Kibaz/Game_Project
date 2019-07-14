package models;

import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.util.vector.Vector3f;

public class Model {
	
	private static final AtomicInteger idCounter = new AtomicInteger(0);
	
	private int id;
	
	private float[] vertices;
	
	public Model(float[] vertices)
	{
		this.vertices = vertices;
		this.id = idCounter.incrementAndGet();
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

	public int getID() {
		return id;
	}

	public float[] getVertices() {
		return vertices;
	}
	
	

}
