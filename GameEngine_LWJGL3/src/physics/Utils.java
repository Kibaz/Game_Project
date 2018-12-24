package physics;

import org.lwjgl.util.vector.Vector3f;

public class Utils {
	
	public static boolean getLowestRoot(float a, float b, float c, float maxR, float root)
	{
		// Check if a solution exists
		float determinant = b*b - 4.0f*a*c;
		// If determinant is negative - no solutions
		if(determinant < 0.0f)
		{
			return false;
		}
		
		// calculate the roots
		float sqrtD = (float) Math.sqrt(determinant);
		float r1 = (-b - sqrtD) / (2*a);
		float r2 = (-b + sqrtD) / (2*a);
		// Sort so x1 <= x2
		if(r1 > r2)
		{
			float temp = r2;
			r2 = r1;
			r1 = temp;
		}
		
		// Get lowest root
		if(r1 > 0 && r1 < maxR)
		{
			root = r1;
			return true;
		}
		
		if(r2 > 0 && r2 < maxR)
		{	
			root = r2;
			return true;
		}
		
		// No solutions
		return false;
	}
	
	// Points 1,2 and 3 are given in e-space (ellipsoid space)
	public static void checkTriangle(CollisionTest colTest, Vector3f p1, Vector3f p2, Vector3f p3)
	{
		// Create plane using triangle points
		Plane triPlane = new Plane(p1,p2,p3);
		
		// Is triangle front-facing to the velocity vector
		// Check only front-facing triangles
		if(triPlane.isFrontFacingTo(colTest.normalizedVel))
		{
			
			// get interval of the intersection with the plane
			double t0, t1;
			boolean isInPlane = false;
			
			// Calculate signed distance from sphere
			// position to tri-plane
			
			double signedDistToTriPlane = triPlane.signedDistTo(colTest.basePoint);
			
			// cache this value - use it recurrently
			float normalDotVel = Vector3f.dot(triPlane.normal, colTest.velocity);
			
			//if sphere is travelling parallel to plane
			if(normalDotVel == 0.0f)
			{
				
				if(Math.abs(signedDistToTriPlane) >= 1.0f)
				{
					// Sphere is not embedded in plane
					// No collision possible
					return;
				}
				else
				{
					// Sphere is embedded in plane
					// It intersects in the whole range [0..1]
					isInPlane = true;
					t0 = 0.0;
					t1 = 1.0;
				}
			}
			else
			{
				// N dot D is not 0 - Calculate intersection interval
				
				t0 = (-1.0-signedDistToTriPlane)/normalDotVel;
				t1 = (1.0-signedDistToTriPlane)/normalDotVel;
				// Swap so t0 < t1
				if(t0 > t1)
				{
					double temp = t1;
					t1 = t0;
					t0 = temp;
				}
				
				// Check that at least one result is within range
				if(t0 > 1.0f || t1 < 0.0f)
				{
					// Both t values are outside values [0,1]
					// No collision possible
					return;
				}
				
				// Clamp to [0,1]
				if(t0 < 0.0) t0 = 0.0;
				if(t1 < 0.0) t1 = 0.0;
				if(t0 > 1.0) t0 = 1.0;
				if(t1 > 1.0) t1 = 1.0;
			}
			
			Vector3f collPoint = null;
			boolean foundColl = false;
			float t = 1.0f;
			
			if(!isInPlane)
			{
				Vector3f temp = new Vector3f(colTest.velocity);
				Vector3f planeIntersectionPoint = Vector3f.add(Vector3f.sub(colTest.basePoint, triPlane.normal, null), 
						(Vector3f) temp.scale((float)t0), null);
				if(checkPointInTriangle(planeIntersectionPoint, p1,p2,p3))
				{
					foundColl = true;
					t = (float) t0;
					collPoint = planeIntersectionPoint;
				}
			}
			
			if(!foundColl)
			{
				Vector3f velocity = new Vector3f(colTest.velocity);
				Vector3f base = colTest.basePoint;
				float velSqrdLength = velocity.lengthSquared();
				float a,b,c;
				float newT =0;
				
				// For each vertex or edge - solve a quadratic equation
				// Parameterise equation as a*t^2 + b*t + c = 0
				// Below calculate the parameters a,b and c for each test
				// Check against points
				a = velSqrdLength;
				//P1
				b =  (float) (2.0*(Vector3f.dot(velocity, Vector3f.sub(base, p1, null))));
				c = (float) (Vector3f.sub(p1, base, null).lengthSquared() - 1.0);
				
				if(getLowestRoot(a,b,c, t, newT))
				{
					t = newT;
					foundColl = true;
					collPoint = p1;
				}
				
				//P2
				b =  (float) (2.0*(Vector3f.dot(velocity, Vector3f.sub(base, p2, null))));
				c = (float) (Vector3f.sub(p2, base, new Vector3f()).lengthSquared() - 1.0);
				if(getLowestRoot(a,b,c, t, newT))
				{
					t = newT;
					foundColl = true;
					collPoint = p2;
				}
				
				//P3
				b =  (float) (2.0*(Vector3f.dot(velocity, Vector3f.sub(base, p3, null))));
				c = (float) (Vector3f.sub(p3, base, new Vector3f()).lengthSquared() - 1.0);
				if(getLowestRoot(a,b,c, t, newT))
				{
					t = newT;
					foundColl = true;
					collPoint = p3;
				}
				
				// check against edges
				// p1 -> p2
				Vector3f edge = Vector3f.sub(p2, p1, null);
				Vector3f baseToVertex = Vector3f.sub(p1, base, null);
				float edgeSqrdLength = edge.lengthSquared();
				float edgeDotVel = Vector3f.dot(edge, velocity);
				float edgeDotBaseToVert = Vector3f.dot(edge, baseToVertex);
				
				// Calculate parameters for equation
				
				a= edgeSqrdLength*-velSqrdLength + edgeDotVel*edgeDotVel;
				b=edgeSqrdLength*(Vector3f.dot((Vector3f) velocity.scale(2), baseToVertex))-2.0f*edgeDotVel*edgeDotBaseToVert;
				c = edgeSqrdLength*(1-baseToVertex.lengthSquared())+edgeDotBaseToVert*edgeDotBaseToVert;
				
				// Does the swept sphere collide against the infinite edge?
				
				if(getLowestRoot(a,b,c, t, newT))
				{
					float f =(edgeDotVel*newT-edgeDotBaseToVert)/edgeSqrdLength;
					if(f >= 0.0f && f <= 1.0f)
					{
						// intersection took place within segment
						t = newT;
						foundColl = true;
						Vector3f f_edge = new Vector3f(f*edge.x, f*edge.y, f*edge.z);
						Vector3f p1_plus_f_edge = new Vector3f(p1.x + f_edge.x, p1.y + f_edge.y, p1.z + f_edge.z);
						collPoint = p1_plus_f_edge;
					}
				}
				
				// p2 -> p3
				edge = Vector3f.sub(p3, p2, null);
				baseToVertex = Vector3f.sub(p2, base, null);
				edgeSqrdLength = edge.lengthSquared();
				edgeDotVel = Vector3f.dot(edge, velocity);
				edgeDotBaseToVert = Vector3f.dot(edge, baseToVertex);
				
				// Calculate parameters for equation
				
				a= edgeSqrdLength*-velSqrdLength + edgeDotVel*edgeDotVel;
				b=edgeSqrdLength*(Vector3f.dot((Vector3f) velocity.scale(2), baseToVertex))-2.0f*edgeDotVel*edgeDotBaseToVert;
				c = edgeSqrdLength*(1-baseToVertex.lengthSquared())+edgeDotBaseToVert*edgeDotBaseToVert;
				
				// Does the swept sphere collide against the infinite edge?
				
				if(getLowestRoot(a,b,c, t, newT))
				{
					float f =(edgeDotVel*newT-edgeDotBaseToVert)/edgeSqrdLength;
					if(f >= 0.0f && f <= 1.0f)
					{
						// intersection took place within segment
						t = newT;
						foundColl = true;
						Vector3f f_edge = new Vector3f(f*edge.x, f*edge.y, f*edge.z);
						Vector3f p2_plus_f_edge = new Vector3f(p2.x + f_edge.x, p2.y + f_edge.y, p2.z + f_edge.z);
						collPoint = p2_plus_f_edge;
					}
				}
				
				// p3 -> p1
				edge = Vector3f.sub(p1, p3, null);
				baseToVertex = Vector3f.sub(p3, base, null);
				edgeSqrdLength = edge.lengthSquared();
				edgeDotVel = Vector3f.dot(edge, velocity);
				edgeDotBaseToVert = Vector3f.dot(edge, baseToVertex);
				
				// Calculate parameters for equation
				
				a= edgeSqrdLength*-velSqrdLength + edgeDotVel*edgeDotVel;
				b=edgeSqrdLength*(Vector3f.dot((Vector3f) velocity.scale(2), baseToVertex))-2.0f*edgeDotVel*edgeDotBaseToVert;
				c = edgeSqrdLength*(1-baseToVertex.lengthSquared())+edgeDotBaseToVert*edgeDotBaseToVert;
				
				// Does the swept sphere collide against the infinite edge?
				
				if(getLowestRoot(a,b,c, t, newT))
				{
					float f =(edgeDotVel*newT-edgeDotBaseToVert)/edgeSqrdLength;
					if(f >= 0.0f && f <= 1.0f)
					{
						// intersection took place within segment
						t = newT;
						foundColl = true;
						Vector3f f_edge = new Vector3f(f*edge.x, f*edge.y, f*edge.z);
						Vector3f p3_plus_f_edge = new Vector3f(p3.x + f_edge.x, p3.y + f_edge.y, p3.z + f_edge.z);
						collPoint = p3_plus_f_edge;
					}
				}
				
			}
			
			// Set result
			if(foundColl)
			{
				// distance to collision: 't' is time of collision
				float distToColl = t*colTest.velocity.length();
				
				// Does this triangle qualify for the closest hit?
				//it does if it's the first hit or the closest
				if(colTest.foundCollision == false ||
						distToColl < colTest.nearestDistance)
				{
					// Collision info necessary for sliding
					colTest.nearestDistance = distToColl;
					colTest.intersectionPoint = collPoint;
					colTest.foundCollision = true;
				}
			}
		}
	}
	
	public static boolean checkPointInTriangle(Vector3f point, Vector3f p1, Vector3f p2, Vector3f p3)
	{
		Vector3f e10=Vector3f.sub(p2, p1, null);
		Vector3f e20=Vector3f.sub(p3, p1, null);
		
		float a = Vector3f.dot(e10, e10);
		float b = Vector3f.dot(e10, e20);
		float c = Vector3f.dot(e20, e20);
		float ac_bb =(a*c)-(b*b);
		Vector3f vp = new Vector3f(point.x-p1.x, point.y-p1.y, point.z-p1.z);
		
		float d = Vector3f.dot(vp, e10);
		float e = Vector3f.dot(vp, e20);
		float x = (d*c)-(e*b);
		float y = (e*a)-(d*b);
		float z = x+y-ac_bb;
		
		return z < 0 && x>= 0 && y>= 0;
	}

}
