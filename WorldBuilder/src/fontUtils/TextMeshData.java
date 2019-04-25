package fontUtils;

public class TextMeshData {
	
	private float[] vertexPositions;
	private float[] texCoords;
	
	public TextMeshData(float[] vertPositions, float[] texCoords)
	{
		this.vertexPositions = vertPositions;
		this.texCoords = texCoords;
	}

	public float[] getVertexPositions() {
		return vertexPositions;
	}

	public float[] getTexCoords() {
		return texCoords;
	}
	
	public int getVertCount()
	{
		return vertexPositions.length / 2;
	}

}
