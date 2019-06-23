package models;

import texturing.Material;

public class Mesh {
	
	private float[] vertices;
	private int[] indices;
	private float[] uvs;
	private float[] normals;
	private float[] weights;
	private int[] boneIndices;
	
	private Material material;
	
	public Mesh(float[] vertices, int[] indices, float[] uvs, float[] normals, float[] weights, int[] boneIndices)
	{
		this.vertices = vertices;
		this.indices = indices;
		this.uvs = uvs;
		this.normals = normals;
		this.weights = weights;
		this.boneIndices = boneIndices;
	}

	public float[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public float[] getUvs() {
		return uvs;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getWeights() {
		return weights;
	}

	public int[] getBoneIndices() {
		return boneIndices;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	
	
	
	

}
