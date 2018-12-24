package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import rendering.Window;

public class Particle {
	
	private static final float GRAVITY = -50;
	
	private Vector3f position;
	private Vector3f velocity;
	
	private float gravityInfluence;
	private float lifeSpan;
	private float rotation;
	private float scale;
	
	private float timeElapsed = 0;
	
	private ParticleTexture texture;
	
	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	
	private float distance;
	
	private float blend;

	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityInfluence, float lifeSpan, float rotation,
			float scale) {
		this.position = position;
		this.velocity = velocity;
		this.gravityInfluence = gravityInfluence;
		this.lifeSpan = lifeSpan;
		this.rotation = rotation;
		this.scale = scale;
		this.texture = texture;
		ParticleManager.addParticle(this);
	}
	
	public float getDistance() {
		return distance;
	}

	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public float getBlend() {
		return blend;
	}

	public Vector3f getPosition() {
		return position;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}
	
	public boolean update(Camera camera)
	{
		velocity.y += GRAVITY * gravityInfluence * Window.getFrameTime();
		Vector3f change = new Vector3f(velocity);
		change.scale(Window.getFrameTime());
		Vector3f.add(change, position, position);
		distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		updateTextureCoordInfo();
		timeElapsed += Window.getFrameTime();
		return timeElapsed < lifeSpan;
	}
	
	private void updateTextureCoordInfo()
	{
		float lifeFactor = timeElapsed / lifeSpan;
		int stageCount = texture.getNumRows() * texture.getNumRows();
		float atlasProgress = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgress);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		this.blend = atlasProgress % 1;
		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
	}
	
	private void setTextureOffset(Vector2f offset, int index)
	{
		int column = index % texture.getNumRows();
		int row = index / texture.getNumRows();
		offset.x = (float) column / texture.getNumRows();
		offset.y = (float) row / texture.getNumRows();
	}
	
	
	
	

}
