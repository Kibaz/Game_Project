package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.BaseModel;
import rendering.Loader;

public class OBJLoader {
	
	public static BaseModel loadObj(String filepath, Loader loader)
	{
		FileReader fileReader = null;
		try
		{
			fileReader = new FileReader(new File("res/" +filepath+".obj"));
		}catch (FileNotFoundException e)
		{
			System.err.println("Couldn't load file");
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fileReader);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Vector2f> UVs = new ArrayList<Vector2f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] uvArray = null;
		int[] indicesArray = null;
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
				}else if(line.startsWith("vt "))
				{
					Vector2f uv = new Vector2f(Float.parseFloat(currLine[1]), Float.parseFloat(currLine[2]));
					UVs.add(uv);
				}else if(line.startsWith("vn "))
				{
					Vector3f normal = new Vector3f(Float.parseFloat(currLine[1]), 
							Float.parseFloat(currLine[2]), Float.parseFloat(currLine[3]));
					normals.add(normal);	
				}else if(line.startsWith("f "))
				{
					uvArray = new float[vertices.size()*2];
					normalsArray = new float[vertices.size()*3];
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
				
				processVertex(vertex1, indices, UVs,normals,uvArray,normalsArray);
				processVertex(vertex2, indices, UVs,normals,uvArray,normalsArray);
				processVertex(vertex3, indices, UVs,normals,uvArray,normalsArray);
				
				line = reader.readLine();
				
			}
			reader.close();
			
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		
		int vertCounter = 0;
		
		for(Vector3f vertex:vertices)
		{
			verticesArray[vertCounter++] = vertex.x;
			verticesArray[vertCounter++] = vertex.y;
			verticesArray[vertCounter++] = vertex.z;
		}
		
		for(int i = 0; i < indices.size(); i++)
		{
			indicesArray[i] = indices.get(i);
		}
		
		return loader.loadToVAO(verticesArray, uvArray, normalsArray, indicesArray);
		
		
	}
	
	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> uvs, List<Vector3f> normals, float[] textureArray, float[] normalsArray)
	{
		int currentVertPointer = Integer.parseInt(vertexData[0]) -1;
		indices.add(currentVertPointer);
		Vector2f currUv = uvs.get(Integer.parseInt(vertexData[1])-1);
		textureArray[currentVertPointer*2] = currUv.x;
		textureArray[currentVertPointer*2+1] = 1 - currUv.y;
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
		normalsArray[currentVertPointer*3] = currentNorm.x;
		normalsArray[currentVertPointer*3+1] = currentNorm.y;
		normalsArray[currentVertPointer*3+2] = currentNorm.z;
	}

}
