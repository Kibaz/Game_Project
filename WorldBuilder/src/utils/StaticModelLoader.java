package utils;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVector3D.Buffer;
import org.lwjgl.assimp.Assimp;

import models.BaseModel;
import rendering.Loader;

public class StaticModelLoader {
	
	public static BaseModel[] load(String filepath, Loader loader) throws Exception
	{
		AIScene scene = Assimp.aiImportFile(filepath, Assimp.aiProcess_Triangulate | 
				Assimp.aiProcess_GenSmoothNormals |
				Assimp.aiProcess_JoinIdenticalVertices |
				Assimp.aiProcess_FixInfacingNormals);
		
		if(scene == null)
		{
			throw new Exception("Assimp Error: Error loading scene!");
		}
		
		int numMeshes = scene.mNumMeshes();
		PointerBuffer meshes = scene.mMeshes();
		BaseModel[] models = new BaseModel[numMeshes];
		
		for(int i = 0; i < numMeshes; i++)
		{
			AIMesh mesh = AIMesh.create(meshes.get(i));
			BaseModel model = processMesh(mesh, loader);
			models[i] = model;
		}
		
		return models;
		
		
	}
	
	private static BaseModel processMesh(AIMesh mesh, Loader loader)
	{
		List<Float> verts = new ArrayList<>();
		List<Float> uvs = new ArrayList<>();
		List<Float> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		storeVertices(mesh, verts);
		storeNormals(mesh, normals);
		storeUVs(mesh, uvs);
		storeIndices(mesh, indices);
		
		float[] vertArray = Utils.floatListToArray(verts);
		float[] textArray = Utils.floatListToArray(uvs);
		float[] normalArray = Utils.floatListToArray(normals);
		int[] indexArray = Utils.intListToArray(indices);
		
		return loader.loadToVAO(vertArray, textArray, normalArray, indexArray);
	}
	
	private static void storeVertices(AIMesh mesh, List<Float> vertices)
	{
		AIVector3D.Buffer verts = mesh.mVertices();
		
		while(verts.remaining() > 0)
		{
			AIVector3D vertex = verts.get();
			vertices.add(vertex.x());
			vertices.add(vertex.y());
			vertices.add(vertex.z());
		}
	}
	
	private static void storeNormals(AIMesh mesh, List<Float> normals)
	{
		for(int i = 0; i < mesh.mNumVertices(); i++)
		{
			AIVector3D normal = mesh.mNormals().get(i);
			normals.add(normal.x());
			normals.add(normal.y());
			normals.add(normal.z());
		}
		
	}
	
	private static void storeUVs(AIMesh mesh, List<Float> uvs)
	{
		AIVector3D.Buffer aiUVs = mesh.mTextureCoords(0);
		
		for(int i = 0; i < mesh.mNumVertices(); i++)
		{
			AIVector3D uv = aiUVs.get(i);
			uvs.add(uv.x());
			uvs.add(uv.y());
		}
	}
	
	private static void storeIndices(AIMesh mesh, List<Integer> indices)
	{
		for(int i = 0; i < mesh.mNumFaces(); i++)
		{
			AIFace face = mesh.mFaces().get(i);
			for(int j = 0; j < face.mNumIndices(); j++)
			{
				indices.add(face.mIndices().get(j));
			}
		}
	}
	
	

}
