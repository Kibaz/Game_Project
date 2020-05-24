package components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import animation.Animation;
import animation.AnimationType;
import animation.Bone;
import animation.Node;
import animation.PositionTransform;
import animation.RotationTransform;
import animation.ScaleTransform;
import rendering.Window;
import utils.Maths;

public class AnimationComponent extends Component{
	
	public static final int MAX_WEIGHTS = 4;
	
	private Matrix4f globalTransformationMatrix;
	
	private Matrix4f[] jointTransforms;
	private Bone[] bones;
	
	private Map<String,Animation> animations;
	
	private Animation currentAnimation;
	
	private float time;
	
	private float timeInTicks;
	
	public AnimationComponent(String name)
	{
		super(name);
		animations = new HashMap<>();
		currentAnimation = null;
	}

	@Override
	public void init() {

	}

	@Override
	public void update() {
		float frameTime = Window.getFrameTime();
		time += frameTime; // Increment time each frame
		
		float maxTime = calculateMaxTime();
		if(currentAnimation.getType() == AnimationType.DEATH && time > maxTime)
		{
			time = maxTime - 0.0000001f;
		}
		
		// Calculate join transforms based on current interval of time
		calculateJointTransforms();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		
	}
	
	private float calculateMaxTime()
	{
		float ticksPerSecond = (float) (currentAnimation.getTicksPerSecond() != 0 ? currentAnimation.getTicksPerSecond() : 25.0f);
		return (float) currentAnimation.getDuration() / ticksPerSecond;
	}
	
	private void calculateJointTransforms()
	{
		Matrix4f identity = new Matrix4f();
		float ticksPerSecond = (float) (currentAnimation.getTicksPerSecond() != 0 ? currentAnimation.getTicksPerSecond() : 25.0f);
		timeInTicks = time * ticksPerSecond;
		if(timeInTicks > currentAnimation.getDuration()) {
			time = 0;
		}
		float animationTime = (timeInTicks % (float) currentAnimation.getDuration());
		readNodeHierarchy(animationTime,currentAnimation.getRootNode(),identity);
		
		for(short i = 0; i < bones.length; i++) 
		{
			if(bones[i].getFinalTransform() != null) 
			{
				jointTransforms[i] = bones[i].getFinalTransform();
			}
		}
	}
	
	private void readNodeHierarchy(float animationTime, Node node, Matrix4f parentTransform)
	{
		Matrix4f nodeTransform = node.getTransformation();
		if(node != null && node.isAnimationNode())
		{	
			Vector3f position = calculateInterpolatedPosition(animationTime,node);
			Matrix4f translation = new Matrix4f();
			Matrix4f.translate(position, translation, translation);
			
			Quaternion rotation = calculateInterpolatedRotation(animationTime,node);
			Matrix4f rotationMatrix = Maths.quatToMatrix4f(rotation);
			rotationMatrix.transpose();
			
			Vector3f scale = calculateInterpolatedScaling(animationTime,node);
			Matrix4f scaleMatrix = new Matrix4f();
			Matrix4f.scale(scale, scaleMatrix, scaleMatrix);
			
			nodeTransform = Matrix4f.mul(rotationMatrix, scaleMatrix,null);
			nodeTransform = Matrix4f.mul(translation, nodeTransform, null);
			
		}
		
		Matrix4f globalTransform = Matrix4f.mul(parentTransform, nodeTransform, null);
		
		Bone bone = null;
		
		if((bone = findBone(node.getName()))!= null)
		{
			Matrix4f finalTransform = Matrix4f.mul(globalTransform, bone.getOffsetMatrix(), null);
			bone.setFinalTransform(finalTransform);
		}
		
		for(int i = 0; i < node.getChildren().size(); i++)
		{
			Node child = node.getChildren().get(i);
			readNodeHierarchy(animationTime,child,globalTransform);
		}
	}
	
	private Vector3f calculateInterpolatedPosition(float animationTime, Node node)
	{
		Vector3f interpolatedPosition = new Vector3f(0,0,0);
		
		List<PositionTransform> positions = node.getPositions().get(currentAnimation.getName());
		
		if(positions.size() == 1)
		{
			return positions.get(0).getPosition();
		}
		
		int index = findPosition(animationTime,node);
		int nextIndex = index + 1;
		assert(nextIndex < node.getPositions().size());
		float deltaTime = (float) (node.getPositions().get(currentAnimation.getName()).get(nextIndex).getTime() - positions.get(index).getTime());
		float factor = (animationTime - (float) positions.get(index).getTime()) / deltaTime;
		assert(factor >= 0.0f && factor <= 1.0f);
		Vector3f start = positions.get(index).getPosition();
		Vector3f end = positions.get(nextIndex).getPosition();
		Vector3f delta = new Vector3f();
		Vector3f.sub(end, start, delta);
				
		Vector3f deltaFactor = new Vector3f(delta.x * factor, delta.y * factor, delta.z * factor);
				
		Vector3f.add(start, deltaFactor, interpolatedPosition);
		
		return interpolatedPosition;
	}
	
	private Quaternion calculateInterpolatedRotation(float animationTime, Node node)
	{
		Quaternion interpolatedRotation = new Quaternion(0,0,0,0);
		
		List<RotationTransform> rotations = node.getRotations().get(currentAnimation.getName());
		
		if(rotations.size() == 1)
		{
			return rotations.get(0).getRotation();
		}
		
		int index = findRotation(animationTime,node);
		int nextIndex = index + 1;
		assert(nextIndex < node.getRotations().size());
		float deltaTime = (float) (rotations.get(nextIndex).getTime() - rotations.get(index).getTime());
		float factor = (animationTime - (float) rotations.get(index).getTime()) / deltaTime;
		assert(factor >= 0.0f && factor <= 1.0f);
		Quaternion start = rotations.get(index).getRotation();
		Quaternion end = rotations.get(nextIndex).getRotation();
		interpolatedRotation = Maths.slerp(start,end,factor);
		return interpolatedRotation;
	}
	
	private Vector3f calculateInterpolatedScaling(float animationTime, Node node)
	{
		List<ScaleTransform> scalings = node.getScalings().get(currentAnimation.getName());
		
		if(scalings.size() == 1)
		{
			return scalings.get(0).getScale();
		}
		
		int index = findScale(animationTime,node);
		int nextIndex = index + 1;
		assert(nextIndex < node.getScalings().size());
		float deltaTime = (float) (scalings.get(nextIndex).getTime() - scalings.get(index).getTime());
		float factor = (animationTime - (float) scalings.get(index).getTime()) / deltaTime;
		assert(factor >= 0.0f && factor <= 1.0f);			
		Vector3f start = scalings.get(index).getScale();
		Vector3f end = scalings.get(nextIndex).getScale();
		Vector3f delta = Vector3f.sub(end, start, null);
		Vector3f deltaFactor = new Vector3f(delta.x * factor, delta.y * factor, delta.z * factor);
		return Vector3f.add(start,deltaFactor ,null);

	}
	
	private int findRotation(float animationTime, Node node)
	{
		List<RotationTransform> rotations = node.getRotations().get(currentAnimation.getName());
		
		assert(rotations.size() > 0);
		
		for(int i = 0; i < rotations.size() - 1; i++)
		{
			if(animationTime < (float) rotations.get(i+1).getTime())
			{
				return i;
			}
		}
		
		return 0;
	}
	
	private int findPosition(float animationTime, Node node)
	{
		List<PositionTransform> positions = node.getPositions().get(currentAnimation.getName());
		
		for(int i = 0; i < positions.size() - 1; i++)
		{
			if(animationTime < (float) positions.get(i+1).getTime())
			{
				return i;
			}
		}
		
		return 0;
	}
	
	private int findScale(float animationTime, Node node)
	{
		List<ScaleTransform> scalings = node.getScalings().get(currentAnimation.getName());
		
		assert(scalings.size() > 0);
		
		for(int i = 0; i < scalings.size() - 1; i++)
		{
			if(animationTime < (float) scalings.get(i+1).getTime())
			{
				return i;
			}
		}
		
		return 0;
	}
	
	public Bone findBone(String boneName)
	{
		for(Bone bone: bones) if (bone.getBoneName().equals(boneName)) return bone;
		
		return null;
	}
	
	

	public Matrix4f getGlobalTransformationMatrix() {
		return globalTransformationMatrix;
	}
	
	public void setGlobalTransformationMatrix(Matrix4f globalTransformationMatrix)
	{
		this.globalTransformationMatrix = globalTransformationMatrix;
	}

	public Matrix4f[] getJointTransforms() {
		return jointTransforms;
	}
	
	public void setJointTransforms(Matrix4f[] jointTransforms)
	{
		this.jointTransforms = jointTransforms;
	}

	public Bone[] getBones() {
		return bones;
	}
	
	public void setBones(Bone[] bones)
	{
		this.bones = bones;
	}

	public Map<String, Animation> getAnimations() {
		return animations;
	}

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	
	public void setCurrentAnimation(String name)
	{
		if(currentAnimation != null && !currentAnimation.getName().equals(name) && 
				animations.get(name).getType() == AnimationType.DEATH)
		{
			time = 0;
		}
		currentAnimation = animations.get(name);
	}

	public float getTime() {
		return time;
	}
	
	public void setTime(float time)
	{
		this.time = time;
	}
	
	public float getTimeInTicks() {
		return timeInTicks;
	}

	public void submitAnimation(String name, Animation animation)
	{
		animations.put(name, animation);
	}
	

}
