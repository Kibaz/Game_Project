package physics;

import org.lwjgl.util.vector.Vector3f;

public class CollisionTest {
	
	public Vector3f eRadius; // radius for ellipsoid
	
	// In R3
	public Vector3f R3Velocity;
	public Vector3f R3Position;
	
	// In E-Space
	public Vector3f velocity;
	public Vector3f normalizedVel;
	public Vector3f basePoint;
	
	// Collision info
	public boolean foundCollision;
	public double nearestDistance;
	public Vector3f intersectionPoint;
	
	public Triangle collidedTriangle;

}
