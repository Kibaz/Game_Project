package animation;

import org.lwjgl.util.vector.Vector3f;

public class PositionTransform {
	
	private Vector3f position;
	private double time;
	
	public PositionTransform(Vector3f position, double time)
	{
		this.position = position;
		this.time = time;
	}

	public Vector3f getPosition() {
		return position;
	}

	public double getTime() {
		return time;
	}

}
