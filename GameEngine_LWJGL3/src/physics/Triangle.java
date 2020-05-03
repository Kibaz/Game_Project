package physics;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class Triangle {
	
	private Vector3f[] points = new Vector3f[3];
	
	private Vector3f normal;
	
	public Triangle(Vector3f p1, Vector3f p2, Vector3f p3)
	{
		points[0] = p1;
		points[1] = p2;
		points[2] = p3;
	}
	
	public float calculateSteepness(Vector3f forward)
	{
		forward.normalise();
		normal = Vector3f.cross(Vector3f.sub(points[1], points[0], null), Vector3f.sub(points[2], points[0], null), null);
		normal.normalise();
		
		return Vector3f.dot(normal, forward);
	}
	
	public Vector3f[] getPoints()
	{
		return points;
	}
	
	public Vector3f getNormal()
	{
		return normal;
	}
	
	

}
