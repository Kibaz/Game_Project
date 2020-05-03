package physics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class CollisionBox {
	
	/*
	 * Similar in concept to an Axis Aligned Bounding Box
	 * Used to determine the actual collision between entities
	 * Used to create a response to the collision
	 * More tailored to the precise shape of the model
	 */
	
	private float[] vertices;
	private int[] indices;
	private Triangle[] triangles; // Triangles to collide with
	private Entity entity; // Hold reference to parent model
	
	public CollisionBox(float[] vertices, int[] indices)
	{
		this.vertices = vertices;
		this.indices = indices;
		calculateTriangles();
	}
	
	private void calculateTriangles()
	{
		int vertCounter = 0;
		// Convert floating points into Vector3f vertices
		Vector3f[] verts = new Vector3f[vertices.length/3];
		for(int i = 0; i < verts.length; i++)
		{
			float value1 = vertices[vertCounter];
			vertCounter++;
			float value2 = vertices[vertCounter];
			vertCounter++;
			float value3 = vertices[vertCounter];
			vertCounter++;
			verts[i] = new Vector3f(value1, value2, value3);
		}
		
		int indexCounter = 0;
		for(int i = 0; i < triangles.length; i++)
		{
			int index1 = indices[indexCounter];
			indexCounter++;
			int index2 = indices[indexCounter];
			indexCounter++;
			int index3 = indices[indexCounter];
			indexCounter++;
			
			triangles[i] = new Triangle(verts[index1], verts[index2], verts[index3]);
		}
	}
	
	/*
	 * Short-hand version of the OBJ Loader in order to
	 * load custom made bounding boxes from .obj files.
	 * Only requires vertex and index data to create
	 * the collision box representation
	 */
	public static CollisionBox loadBox(String filepath)
	{
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File("res/" + filepath+".obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(fileReader);
		
		String line;
		
		List<Vector3f> vertices = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		float[] vertexData = null;
		int[] indexData = null;
		
		try
		{
			while(true)
			{
				line = reader.readLine();
				String[] currLine = line.split(" ");
				if(line.startsWith("v "))
				{
					Vector3f vertex = new Vector3f(Float.parseFloat(currLine[1]), 
							Float.parseFloat(currLine[2]), Float.parseFloat(currLine[3]));
					vertices.add(vertex);
				}
				else if(line.startsWith("f "))
				{
					break;
				}
			}
			
			while(line!=null)
			{
				if(!line.startsWith("f "))
				{
					line = reader.readLine();
					continue;
				}
				
				String[] currLine = line.split(" ");
				String[] vertex1 = currLine[1].split("/");
				String[] vertex2 = currLine[2].split("/");
				String[] vertex3 = currLine[3].split("/");
				
				processVertex(vertex1,indices);
				processVertex(vertex2,indices);
				processVertex(vertex3,indices);
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		vertexData = new float[vertices.size()*3];
		indexData = new int[indices.size()];
		
		int vertCounter = 0;
		
		for(Vector3f vertex:vertices)
		{
			vertexData[vertCounter++] = vertex.x;
			vertexData[vertCounter++] = vertex.y;
			vertexData[vertCounter++] = vertex.z;
		}
		
		for(int i = 0; i < indices.size(); i++)
		{
			indexData[i] = indices.get(i);
		}
		
		return new CollisionBox(vertexData,indexData);
		
	}
	
	private static void processVertex(String[] vertexData, List<Integer> indices)
	{
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currentVertexPointer);
	}

}
