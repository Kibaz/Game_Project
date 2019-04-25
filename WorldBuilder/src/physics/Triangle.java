package physics;

import org.lwjgl.util.vector.Vector3f;

public class Triangle {
	
	private Vector3f[] points = new Vector3f[3];
	
	public Triangle(Vector3f p1, Vector3f p2, Vector3f p3)
	{
		points[0] = p1;
		points[1] = p2;
		points[2] = p3;
	}
	
	public Vector3f[] getPoints()
	{
		return points;
	}

}
