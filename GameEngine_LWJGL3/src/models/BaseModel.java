package models;

import org.lwjgl.util.vector.Vector3f;

import texturing.Material;

public class BaseModel {
	
	private int vaoID;
	private int vertCount;
	
	private float[] vertices;
	private int[] indices;
	
	private Material material;
	
	private boolean animationData;
	
	private int[] indexPositions;
	
	private String name;
	
	public BaseModel(int ID, int vertCount, float[] vertices, int[] indices)
	{
		this.vaoID = ID;
		this.vertCount = vertCount;
		this.vertices = vertices;
		this.indices = indices;
		this.animationData = false;
	}
	
	public BaseModel(int ID, int vertCount, float[] vertices, int[] indices, boolean animationData)
	{
		this.vaoID = ID;
		this.vertCount = vertCount;
		this.vertices = vertices;
		this.indices = indices;
		this.animationData = animationData;
	}
	
	public BaseModel(int ID, int vertCount)
	{
		this.vaoID = ID;
		this.vertCount = vertCount;
	}
	
	public int getVaoID() {
		return vaoID;
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

	public int[] getIndexPositions() {
		return indexPositions;
	}

	public void setIndexPositions(int[] indexPositions) {
		this.indexPositions = indexPositions;
	}

	public int[] getIndices()
	{
		return indices;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		float midPointY = findMinVertex().y + ((findMaxVertex().y - findMinVertex().y)/2);
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
	
	public Material getMaterial()
	{
		return material;
	}
	
	public void setMaterial(Material material)
	{
		this.material = material;
	}
	
	public int compareTo(BaseModel other)
	{
		return this.getVaoID() - other.getVaoID();
	}
	
	public boolean hasAnimationData()
	{
		return animationData;
	}
	
	
	
}
