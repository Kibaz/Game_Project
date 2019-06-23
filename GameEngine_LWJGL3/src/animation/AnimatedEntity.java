package animation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.BaseModel;
import models.TexturedModel;
import rendering.Loader;
import rendering.Window;
import utils.Maths;

public class AnimatedEntity extends Entity{
	
	public static final int MAX_WEIGHTS = 4;
	
	private Matrix4f globalTransformationMatrix;
	
	private Matrix4f[] jointTransforms;
	private Bone[] bones;
	
	private Map<String,Animation> animations;
	
	private Animation currentAnimation;
	
	private float time;
	
	public AnimatedEntity(TexturedModel model, Vector3f position, 
			float rotX, float rotY, float rotZ, float scale)
	{
		super(model,position,rotX,rotY,rotZ,scale);
		this.currentAnimation = null;
		animations = new HashMap<>();
	}
	
	public void update()
	{
		time += Window.getFrameTime();
		calculateJointTransforms();
	}
	
	private void calculateJointTransforms()
	{
		Matrix4f identity = new Matrix4f();
		
		float ticksPerSecond = (float) (currentAnimation.getTicksPerSecond() != 0 ? currentAnimation.getTicksPerSecond() : 25.0f);
		float timeInTicks = time * ticksPerSecond;
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
			Matrix4f translation = new Matrix4f().translate(position);
			
			Quaternion rotation = calculateInterpolatedRotation(animationTime,node);
			Matrix4f rotationMatrix = Maths.quatToMatrix4f(rotation);
			rotationMatrix.transpose(); // This is required for ASSIMP for some fucking reason...!
			
			Vector3f scale = calculateInterpolatedScaling(animationTime,node);
			Matrix4f scaleMatrix = new Matrix4f().scale(scale);
			
			nodeTransform = Matrix4f.mul(Matrix4f.mul(translation, rotationMatrix, null), scaleMatrix, null);
			
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
		
		if(node.getPositions().size() == 1)
		{
			return node.getPositions().get(0).getPosition();
		}
		
		int index = findPosition(animationTime,node);
		int nextIndex = index + 1;
		if(nextIndex < node.getPositions().size())
		{
			float deltaTime = (float) (node.getPositions().get(nextIndex).getTime() - node.getPositions().get(index).getTime());
			float factor = (animationTime - (float) node.getPositions().get(index).getTime()) / deltaTime;
			if(factor >= 0.0f && factor <= 1.0f)
			{
				Vector3f start = node.getPositions().get(index).getPosition();
				Vector3f end = node.getPositions().get(nextIndex).getPosition();
				Vector3f delta = new Vector3f();
				Vector3f.sub(end, start, delta);
				
				Vector3f deltaFactor = new Vector3f(delta.x * factor, delta.y * factor, delta.z * factor);
				
				Vector3f.add(start, deltaFactor, interpolatedPosition);
			}
		}
		
		return interpolatedPosition;
	}
	
	private Quaternion calculateInterpolatedRotation(float animationTime, Node node)
	{
		Quaternion interpolatedRotation = new Quaternion(0,0,0,0);
		
		if(node.getRotations().size() == 1)
		{
			return node.getRotations().get(0).getRotation();
		}
		
		int index = findRotation(animationTime,node);
		int nextIndex = index + 1;
		if(nextIndex < node.getRotations().size())
		{
			float deltaTime = (float) (node.getRotations().get(nextIndex).getTime() - node.getRotations().get(index).getTime());
			float factor = (animationTime - (float) node.getRotations().get(index).getTime()) / deltaTime;
			if(factor >= 0.0f && factor <= 1.0f)
			{
				Quaternion start = node.getRotations().get(index).getRotation();
				Quaternion end = node.getRotations().get(nextIndex).getRotation();
				interpolatedRotation = Maths.slerp(start,end,factor);
			}
		}
		return interpolatedRotation;
	}
	
	private Vector3f calculateInterpolatedScaling(float animationTime, Node node)
	{
		Vector3f interpolatedScaling = new Vector3f(1,1,1);
		
		if(node.getScalings().size() == 1)
		{
			return node.getScalings().get(0).getScale();
		}
		
		int index = findScale(animationTime,node);
		int nextIndex = index + 1;
		if(nextIndex < node.getScalings().size())
		{
			float deltaTime = (float) (node.getScalings().get(nextIndex).getTime() - node.getScalings().get(index).getTime());
			float factor = (animationTime - (float) node.getScalings().get(index).getTime()) / deltaTime;
			if(factor >= 0.0f && factor <= 1.0f)
			{
				Vector3f start = node.getScalings().get(index).getScale();
				Vector3f end = node.getScalings().get(nextIndex).getScale();
				Vector3f delta = Vector3f.sub(end, start, null);
				Vector3f deltaFactor = new Vector3f(delta.x * factor, delta.y * factor, delta.z * factor);
				interpolatedScaling = Vector3f.add(start,deltaFactor ,null);
			}
		}
		
		return interpolatedScaling;
	}
	
	private int findRotation(float animationTime, Node node)
	{
		assert(node.getRotations().size() > 0);
		
		for(int i = 0; i < node.getRotations().size() - 1; i++)
		{
			if(animationTime < (float) node.getRotations().get(i+1).getTime())
			{
				return i;
			}
		}
		
		return 0;
	}
	
	private int findPosition(float animationTime, Node node)
	{	
		for(int i = 0; i < node.getPositions().size() - 1; i++)
		{
			if(animationTime < (float) node.getPositions().get(i+1).getTime())
			{
				return i;
			}
		}
		
		return 0;
	}
	
	private int findScale(float animationTime, Node node)
	{
		assert(node.getScalings().size() > 0);
		
		for(int i = 0; i < node.getScalings().size() - 1; i++)
		{
			if(animationTime < (float) node.getScalings().get(i+1).getTime())
			{
				return i;
			}
		}
		
		return 0;
	}
	
	private Bone findBone(String boneName)
	{
		for(Bone bone: bones) if(bone.getBoneName().equals(boneName)) return bone;
		
		return null;
	}

	public Bone[] getBones() {
		return bones;
	}
	
	public void setBones(Bone[] bones)
	{
		this.bones = bones;
	}

	public Matrix4f getGlobalTransformationMatrix() {
		return globalTransformationMatrix;
	}

	public void setGlobalTransformationMatrix(Matrix4f globalTransformationMatrix) {
		this.globalTransformationMatrix = globalTransformationMatrix;
	}

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	public void setCurrentAnimation(String animationName) {
		this.currentAnimation = animations.get(animationName);
	}
	
	public Matrix4f[] getJointTransforms() {
		return jointTransforms;
	}
	
	public void setJointTransforms(Matrix4f[] jointTransforms)
	{
		this.jointTransforms = jointTransforms;
	}
	
	public Map<String,Animation> getAnimations()
	{
		return animations;
	}
	
	public void setAnimations(Map<String,Animation> animations)
	{
		this.animations = animations;
	}

	public void submitAnimation(String name, Animation animation)
	{
		animations.put(name, animation);
	}
	
	
	
	
	
	

}
