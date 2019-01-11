package animation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;

public class AnimatedCharacter {
	
	public static final int MAX_WEIGHTS = 4; // Store maximum weights
	
	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f(); // Store reference to "Identity Matrix"
	
	private Map<Integer, Animation> animations; // Store list of animations to be rendered
	
	private Entity entity; // Store reference to entity being affected by animations
	
	private Animation currentAnimation; // Store current animation
	
	private Animator animator; // Designated animator for this character
	
	private Matrix4f[] animationTransforms = new Matrix4f[AnimatedFrame.MAX_JOINTS]; // Prepare to store joints
	
	/* Set accessible constants for switching/determining animations */
	public static int IDLE = 0; // Default animation
	public static int RUN = 1;
	public static int WALK = 2;
	public static int SWIM = 3;
	public static int TURN_LEFT = 4;
	public static int TURN_RIGHT = 5;
	public static int DANCE = 6;
	
	public AnimatedCharacter(Entity entity)
	{
		this.entity = entity;
		this.animations = new HashMap<>();
		this.animator = new Animator(this);
		Arrays.fill(animationTransforms, IDENTITY_MATRIX);
	}
	
	public void submitAnimation(int animType, Animation animation)
	{
		animations.put(animType, animation);
	}

	public Map<Integer, Animation> getAnimations() {
		return animations;
	}

	public Entity getEntity() {
		return entity;
	}
	
	public void setCurrentAnimation(int anim)
	{
		currentAnimation = animations.get(anim);
	}

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	public Animator getAnimator() {
		return animator;
	}
	
	public Matrix4f[] getAnimationTransforms()
	{
		return animationTransforms;
	}
	
	public void setAnimationTransforms(int index, Matrix4f matrix)
	{
		animationTransforms[index] = matrix;
	}
	
	public void playCurrentAnimation()
	{
		animator.doAnimation(currentAnimation);
	}
	
	

}
