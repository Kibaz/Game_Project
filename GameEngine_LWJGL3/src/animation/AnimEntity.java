package animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.BaseModel;
import models.TexturedModel;

public class AnimEntity{
	
	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
	
	private Map<String, Animation> animations;
	
	private Animation currAnimation;
	
	private BaseModel[] models;
	
	private Matrix4f[] animationTransforms = new Matrix4f[AnimatedFrame.MAX_JOINTS];
	
	private List<Entity> entities;

	public AnimEntity(BaseModel[] models, Map<String, Animation> animations) {
		this.animations = animations;
		Optional<Map.Entry<String,Animation>> entry = animations.entrySet().stream().findFirst();
		currAnimation = entry.isPresent() ? entry.get().getValue() : null;
		this.models = models;
		entities = new ArrayList<>();
		Arrays.fill(animationTransforms, IDENTITY_MATRIX);
	}
	
	public Animation getAnimation(String name)
	{
		return animations.get(name);
	}
	
	public Animation getCurrentAnimation()
	{
		return currAnimation;
	}
	
	public Map<String, Animation> getAnimations()
	{
		return animations;
	}
	
	public void setCurrentAnimation(Animation currentAnimation)
	{
		this.currAnimation = currentAnimation;
	}

	public BaseModel[] getModels() {
		return models;
	}
	
	public void addEntity(Entity entity)
	{
		entities.add(entity);
	}
	
	public List<Entity> getEntities()
	{
		return this.entities;
	}
	
	public Matrix4f[] getAnimationTransforms()
	{
		return animationTransforms;
	}
	
	public void setAnimationTransforms(int index, Matrix4f matrix)
	{
		animationTransforms[index] = matrix;
	}

	
	
	
	

}
