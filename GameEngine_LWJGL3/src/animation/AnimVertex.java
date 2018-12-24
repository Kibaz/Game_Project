package animation;


import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class AnimVertex {
	
	private Vector3f position;
	
	private Vector2f texCoords;
	
	private Vector3f normal;
	
	private float[] weights;
	
	private int[] jointIndices;
	
	public AnimVertex()
	{
		normal = new Vector3f(0,0,0);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector2f getTexCoords() {
		return texCoords;
	}

	public void setTexCoords(Vector2f texCoords) {
		this.texCoords = texCoords;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public float[] getWeights() {
		return weights;
	}

	public void setWeights(float[] weights) {
		this.weights = weights;
	}

	public int[] getJointIndices() {
		return jointIndices;
	}

	public void setJointIndices(int[] jointIndices) {
		this.jointIndices = jointIndices;
	}
	
	

}
