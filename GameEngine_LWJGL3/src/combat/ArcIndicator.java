package combat;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import rendering.Loader;
import utils.Maths;
import utils.Utils;

public class ArcIndicator extends DamageIndicator{
	
	private float radius;
	private int segments;
	private float startAngle;
	private float arcAngle;

	public ArcIndicator(Vector3f position, float rotY, float radius, int segments, float startAngle, float arcAngle) {
		super(position,rotY);
		this.radius = radius;
		this.segments = segments;
		this.startAngle = startAngle;
		this.arcAngle = arcAngle;
	}

	@Override
	public void buildIndicator(Loader loader) {
		List<Float> vertexList = new ArrayList<>();
		
		float theta = arcAngle / (float) (segments - 1);
		
		float tagentialFactor = (float) Math.tan(Math.toRadians(theta));
		
		float radialFactor = (float) Math.cos(Math.toRadians(theta));
		
		float x = (float) (radius * Math.cos(Math.toRadians(startAngle)));
		float y = (float) (radius * Math.sin(Math.toRadians(startAngle)));
		
		vertexList.add(0.5f);
		vertexList.add(0.5f);
		vertexList.add(0f);
		
		for(int i = 0; i < segments; i++)
		{
			float tx = -y;
			float ty = x;
			
			x += tx * tagentialFactor;
			y += ty * tagentialFactor;
			
			x *= radialFactor;
			y *= radialFactor;
			vertexList.add(x);
			vertexList.add(y);
			vertexList.add(0f);
		}
		
		vertexList.add(0.5f);
		vertexList.add(0.5f);
		vertexList.add(0f);
		
		super.vertices = Utils.floatListToArray(vertexList);
		
		super.model = loader.loadToVAO(super.vertices, 3);
	}

	@Override
	public boolean intersectsEntity(Entity entity) {
		/*
		 * For an arc to be intersecting an entity,
		 * the distance between the arc's center (i.e. position)
		 * and the entity's hit box must be less than the radius (rule of a circle)
		 * 
		 * Additionally, the angle between the entity and the arc's center should
		 * fall between the start and end angle of the arc.
		 */
		
		Vector3f dirToEntity = Vector3f.sub(entity.getPosition(), this.getPosition(), null);
		
		float distance = dirToEntity.length();
		
		if(distance < radius)
		{
			Vector2f facingVector = new Vector2f((float) Math.sin(Math.toRadians(rotY)),(float) Math.cos(Math.toRadians(rotY)));
			
			float dot = Vector2f.dot(new Vector2f(dirToEntity.x,dirToEntity.z), facingVector);
			float magDiv = dirToEntity.length() * facingVector.length();
			float angle = (float) Math.toDegrees(Math.acos(dot/magDiv));
			if(angle < arcAngle /2f)
			{
				return true;
			}
			
		}
		
		return false;
	}

}
