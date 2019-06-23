package animation;

import org.lwjgl.util.vector.Vector3f;

public class ScaleTransform {
	
	private Vector3f scale;
	private double time;
	
	public ScaleTransform(Vector3f scale, double time)
	{
		this.scale = scale;
		this.time = time;
	}

	public Vector3f getScale() {
		return scale;
	}

	public double getTime() {
		return time;
	}
	
	

}
