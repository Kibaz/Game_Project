package physics;

import org.lwjgl.util.vector.Vector3f;

public class Plane {

	public float[] equation = new float[4];
	public Vector3f origin;
	public Vector3f normal;
	
	public Plane(Vector3f origin, Vector3f normal)
	{
		this.normal = normal;
		this.origin = origin;
		equation[0] = normal.x;
		equation[1] = normal.y;
		equation[2] = normal.z;
		equation[3] = -(normal.x*origin.x + normal.y*origin.y + normal.z*origin.z);
	}
	
	public Plane(Vector3f point1, Vector3f point2, Vector3f point3)
	{
		normal = Vector3f.cross(Vector3f.sub(point2, point1, null), Vector3f.sub(point3, point1, null), null);
		normal.normalise();
		origin = point1;
		equation[0] = normal.x;
		equation[1] = normal.y;
		equation[2] = normal.z;
		equation[3] = -(normal.x*origin.x + normal.y*origin.y + normal.z*origin.z);
	}
	
	public boolean isFrontFacingTo(Vector3f dir)
	{
		double dot = Vector3f.dot(normal, dir);
		return (dot <= 0);
	}
	
	public double signedDistTo(Vector3f point)
	{
		return (Vector3f.dot(point, normal)) + equation[3];
	}
	
	
}
