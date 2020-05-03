package animation;

import java.util.List;

import utils.Utils;

public class Mesh {
	
	/*
	 * To store animation vertices, bones, indices
	 * uvs (texture coords), normals and weights
	 */
	
	private List<Float> vertices;
	private List<Float> uvs;
	private List<Float> normals;
	private List<Integer> indices;
	private List<Integer> boneIds;
	private List<Float> weights;
	
	public Mesh(List<Float> vertices,List<Float> uvs, List<Float> normals, 
			List<Integer> indices,List<Integer> boneIds,List<Float>weights)
	{
		this.vertices = vertices;
		this.uvs = uvs;
		this.normals = normals;
		this.indices = indices;
		this.boneIds = boneIds;
		this.weights = weights;
	}

	public float[] getVertices() {
		return Utils.floatListToArray(vertices);
	}

	public float[] getUvs() {
		return Utils.floatListToArray(uvs);
	}

	public float[] getNormals() {
		return Utils.floatListToArray(normals);
	}

	public int[] getIndices() {
		return Utils.intListToArray(indices);
	}

	public int[] getBoneIds() {
		return Utils.intListToArray(boneIds);
	}

	public float[] getWeights() {
		return Utils.floatListToArray(weights);
	}
	
	

}
