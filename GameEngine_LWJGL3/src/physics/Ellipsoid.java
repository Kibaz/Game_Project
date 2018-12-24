package physics;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Player;
import models.BaseModel;

public class Ellipsoid {
	
	private Vector3f position; // Store centre of Ellipsoid
	private static Vector3f centre;
	private static Vector3f radius; // Store radius X, Y & Z
	private static Player player;
	
	// Transformed Model information
	private static float[] vertices;
	private static int[] indices;
	
	public Ellipsoid(Player player)
	{
		this.player = player;
		this.centre = this.player.getModel().getBaseModel().calculateCentre();
		centre.x += player.getPosition().x;
		centre.y += player.getPosition().y;
		centre.z += player.getPosition().z;
		this.position = this.player.getPosition();
		float xRadius = this.player.getModel().getBaseModel().getModelWidth() / 2;
		float yRadius = this.player.getModel().getBaseModel().getModelHeight() / 2;
		float zRadius = (this.player.getModel().getBaseModel().getModelZWidth())/ 2;
		this.radius = new Vector3f(xRadius, yRadius, zRadius);
		this.vertices = this.player.getModel().getBaseModel().getVertices();
		this.indices = this.player.getModel().getBaseModel().getIndices();
	}
	
	public Vector3f getRadius()
	{
		return radius;
	}
	
	public Vector3f getCentre()
	{
		return centre;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public void moveCentre(float dx, float dy, float dz)
	{
		centre.x += dx;
		centre.y += dy;
		centre.z += dz;
	}
	
	public void setY(BaseModel model, Vector3f pos)
	{
		centre.y = model.findMinVertex().y + pos.y; 
		centre.y = model.findMaxVertex().y + pos.y;
	}
	
	public static void update(Matrix4f[] transforms)
	{
		calculateTransformedModel(transforms);
		centre = calculateCentre();
		radius.x = getModelWidth() / 2;
		radius.y = getModelHeight() / 2;
		radius.z = getModelZWidth() / 2;
	}
	
	private static void calculateTransformedModel(Matrix4f[] transforms)
	{
		List<Vector4f> convertedVerts = new ArrayList<>();
		int counter = 0;
		for(int v = 0; v < vertices.length/3; v++)
		{
			Vector4f vector = new Vector4f(0,0,0,0);
			vector.x = vertices[counter];
			vector.y = vertices[counter++];
			vector.z = vertices[counter++];
			vector.w = 1.0f;
			counter++;
			convertedVerts.add(vector);
		}
		
		List<Vector4f> transformedVerts = new ArrayList<>();
		for(Vector4f vector: convertedVerts)
		{
			Vector4f transformedVector = new Vector4f(0,0,0,0);
			for(int i = 0; i < transforms.length; i++)
			{
				transformedVector = Matrix4f.transform(transforms[i], vector, null);
			}
			transformedVerts.add(transformedVector);
		}
		
		
		List<Vector3f> transformed3DVerts = getTransformed3DVerts(transformedVerts);
		counter = 0;
		for(int i = 0; i < transformed3DVerts.size(); i++)
		{
			vertices[counter] = transformed3DVerts.get(i).x;
			counter++;
			vertices[counter] = transformed3DVerts.get(i).y;
			counter++;
			vertices[counter] = transformed3DVerts.get(i).z;
			counter++;
		}
		
		
	}
	
	private static List<Vector3f> getTransformed3DVerts(List<Vector4f> vectors)
	{
		List<Vector3f> results = new ArrayList<>();
		for(int i = 0; i < vectors.size(); i++)
		{
			results.add(convert4DTo3DVector(vectors.get(i)));
		}
		
		return results;
	}
	
	private static Vector3f convert4DTo3DVector(Vector4f vector)
	{
		Vector3f result = new Vector3f(0,0,0);
		result.x = vector.x / vector.w;
		result.y = vector.y / vector.w;
		result.z = vector.z / vector.w;
		return result;
	}
	
	private static Vector3f findMinVertex()
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
		
		minVert = new Vector3f(xMin, yMin, zMin);
		
		return minVert;
	}
	
	private static Vector3f findMaxVertex()
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
		maxVert = new Vector3f(xMax, yMax, zMax);
		
		return maxVert;
	}
	
	private static Vector3f calculateCentre()
	{
		Vector3f centre;
		float midPointX = findMinVertex().x + ((findMaxVertex().x - findMinVertex().x)/2);
		float midPointY =findMinVertex().y + ((findMaxVertex().y - findMinVertex().y)/2);
		float midPointZ = findMinVertex().z + ((findMaxVertex().z - findMinVertex().z)/2);
		centre = new Vector3f(midPointX, midPointY, midPointZ);
		return centre;
	}
	
	private static float getModelHeight()
	{
		float height = 0;
		Vector3f min = findMinVertex();
		Vector3f max = findMaxVertex();
		
		height = max.y - min.y;
		
		return height;
	}
	
	private static float getModelWidth()
	{
		float width = 0;
		Vector3f min = findMinVertex();
		Vector3f max = findMaxVertex();
		
		width = max.x - min.x;
		
		return width;
	}
	
	private static float getModelZWidth()
	{
		float length = 0;
		Vector3f min = findMinVertex();
		Vector3f max = findMaxVertex();
		
		length = max.z - min.z;
		
		return length;
	}
	
	

}
