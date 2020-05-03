package combat;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Entity;
import rendering.Loader;
import terrains.Terrain;
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
	public void buildIndicator(Loader loader,List<Terrain> terrains) {
		// Update vertices according to current position
		
		List<Float> vertexList = new ArrayList<>();
		
		// Create transformation matrix from position and rotation
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(this.getPosition(), 0, this.getRotY(), 0, 1);
		
		
		// Build vertices with no "y" value
		vertexList.add(0f);
		vertexList.add(0f);
		vertexList.add(0f);
		
		float increment = (float) (Math.toRadians(arcAngle) / segments);
		float endAngle = (float) Math.toRadians(startAngle + arcAngle);
		for(float angle = (float) Math.toRadians(startAngle); angle <= endAngle; angle+= increment)
		{
			float x = (float)(radius * Math.cos(angle));
			float z = (float)(radius * Math.sin(angle));
			vertexList.add(x);
			vertexList.add(0f);
			vertexList.add(z);
		}
		
		vertexList.add(0f);
		vertexList.add(0f);
		vertexList.add(0f);
		
		// Update 'y' values
		for(int i = 0; i < vertexList.size(); i+=3)
		{
			Vector4f vertex = new Vector4f(vertexList.get(i),vertexList.get(i+1),vertexList.get(i+2),1f);
			Vector4f transformedVertex = Matrix4f.transform(transformationMatrix, vertex, null);
			
			Terrain t = findTerrain(terrains,transformedVertex.x,transformedVertex.z);
			if(t != null)
			{
				transformedVertex.y = t.getTerrainHeight(transformedVertex.x, transformedVertex.z);
			}
			Vector4f result = Matrix4f.transform(Matrix4f.invert(transformationMatrix, null), transformedVertex, null);
			vertexList.set(i+1,result.y);
		}

		
		super.vertices = Utils.floatListToArray(vertexList);
		
		super.model = loader.loadToVAO(super.vertices, 3);
		
		int vbo = loader.getVbos().get(loader.getVbos().size()-1);
		super.setVertexBufferID(vbo);
	}
	
	private Terrain findTerrain(List<Terrain> terrains,float x,float z)
	{
		for(Terrain terrain: terrains)
		{
			if(terrain.isPointOnTerrain(new Vector3f(x,0,z)))
			{
				return terrain;
			}
		}
		
		return null;
	}
	
	@Override
	public void updateIndicator(Loader loader,List<Terrain> terrains) {
		// Update vertices according to current position
		
		List<Float> vertexList = new ArrayList<>();
		
		// Create transformation matrix from position and rotation
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(this.getPosition(), 0, this.getRotY(), 0, 1);
		
		
		// Build vertices with no "y" value
		vertexList.add(0f);
		vertexList.add(0f);
		vertexList.add(0f);
		
		float increment = (float) (Math.toRadians(arcAngle) / segments);
		float endAngle = (float) Math.toRadians(startAngle + arcAngle);
		for(float angle = (float) Math.toRadians(startAngle); angle <= endAngle; angle+= increment)
		{
			float x = (float)(radius * Math.cos(angle));
			float z = (float)(radius * Math.sin(angle));
			vertexList.add(x);
			vertexList.add(0f);
			vertexList.add(z);
		}
		
		vertexList.add(0f);
		vertexList.add(0f);
		vertexList.add(0f);
		
		// Update 'y' values
		for(int i = 0; i < vertexList.size(); i+=3)
		{
			Vector4f vertex = new Vector4f(vertexList.get(i),vertexList.get(i+1),vertexList.get(i+2),1f);
			Vector4f transformedVertex = Matrix4f.transform(transformationMatrix, vertex, null);
			Terrain t = findTerrain(terrains,transformedVertex.x,transformedVertex.z);
			if(t != null)
			{
				transformedVertex.y = t.getTerrainHeight(transformedVertex.x, transformedVertex.z);
			}
			Vector4f result = Matrix4f.transform(Matrix4f.invert(transformationMatrix, null), transformedVertex, null);
			vertexList.set(i+1,result.y);
		}

		
		super.vertices = Utils.floatListToArray(vertexList);
		
		super.model.setVertices(super.vertices);
		
		loader.updateVBO(this.vertexBufferID, vertices);
		
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
