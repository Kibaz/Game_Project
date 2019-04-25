package animation;

public class VertexWeight {
	
	private int boneId;
	
	private int vertexId;
	
	private float weight;
	
	public VertexWeight(int boneId, int vertexId, float weight)
	{
		this.setBoneId(boneId);
		this.setVertexId(vertexId);
		this.setWeight(weight);
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public int getVertexId() {
		return vertexId;
	}

	public void setVertexId(int vertexId) {
		this.vertexId = vertexId;
	}

	public int getBoneId() {
		return boneId;
	}

	public void setBoneId(int boneId) {
		this.boneId = boneId;
	}

}
