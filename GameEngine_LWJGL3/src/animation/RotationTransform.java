package animation;

import org.lwjgl.util.vector.Quaternion;

public class RotationTransform {
	
	private Quaternion rotation;
	private double time;
	
	public RotationTransform(Quaternion rotation, double time)
	{
		this.rotation = rotation;
		this.time = time;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public double getTime() {
		return time;
	}

}
