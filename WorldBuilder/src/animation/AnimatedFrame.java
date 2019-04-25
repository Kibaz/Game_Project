package animation;

import java.util.Arrays;

import org.lwjgl.util.vector.Matrix4f;

public class AnimatedFrame {
	
	public static final int MAX_JOINTS = 150;
	
	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
	
	private final Matrix4f[] jointMatrices;
	
	private double timeStamp;
	
	public AnimatedFrame()
	{	
		jointMatrices = new Matrix4f[MAX_JOINTS];
		Arrays.fill(jointMatrices, IDENTITY_MATRIX);
		timeStamp = 0;
	}
	
	public Matrix4f[] getJointMatrices()
	{
		return jointMatrices;
	}
	
	public void setMatrix(int pos, Matrix4f jointMatrix)
	{	
		jointMatrices[pos] = jointMatrix;
	}

	public double getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(double time) {
		this.timeStamp = time;
	}
	
	

}
