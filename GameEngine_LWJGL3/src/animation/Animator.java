package animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import physics.Ellipsoid;
import rendering.Window;
import utils.Maths;
import utils.Utils;

public class Animator {
	
	private final Matrix4f IDENTITY_MATRIX = new Matrix4f();
	
	private AnimatedCharacter animChar;
	
	private Animation currentAnimation;
	private float animationTime;
	
	int frameIndex;
	
	
	public Animator(AnimatedCharacter animChar)
	{
		this.animChar = animChar;
	}
	
	public void doAnimation(Animation animation)
	{
		this.animationTime = 0;
		frameIndex = 0;
		this.currentAnimation = animation;
	}
	
	public void update()
	{
		if(currentAnimation == null)
		{
			return;
		}
		increaseAnimationTime();
		List<Matrix4f> currentPose = calculateCurrentPose();
		List<Matrix4f> finalTransforms = new ArrayList<>();
		Matrix4f temp = new Matrix4f();
		
		for(int i = 0; i < currentPose.size(); i++)
		{
			animChar.setAnimationTransforms(i, currentPose.get(i));
		}
		
		Ellipsoid.update(animChar.getAnimationTransforms());
	}
	
	private void increaseAnimationTime()
	{
		animationTime += Window.getFrameTime();
		if(animationTime > currentAnimation.getDuration())
		{
			this.animationTime %= currentAnimation.getDuration();
		}
	}
	
	private float calculateProgression(AnimatedFrame previousFrame, AnimatedFrame nextFrame)
	{
		float totalTime = (float) (nextFrame.getTimeStamp() - previousFrame.getTimeStamp());
		float currentTime = (float) (animationTime - previousFrame.getTimeStamp());
		return currentTime / totalTime;
	}
	
	private List<Matrix4f> calculateCurrentPose()
	{
		AnimatedFrame[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0],frames[1]);
		List<Matrix4f> result = new ArrayList<>();
		for(int i = 0; i < AnimatedFrame.MAX_JOINTS; i++)
		{
			// Store first and second frame matrices
			Matrix4f first = new Matrix4f(frames[0].getJointMatrices()[i]);
			Matrix4f second = new Matrix4f(frames[1].getJointMatrices()[i]);
			// Extract translation and rotation from Matrices
			Quaternion rotationA = Maths.fromMatrix4f(first);
			Vector3f translationA = Maths.getTranslationFromMat4(first);
			
			Quaternion rotationB = Maths.fromMatrix4f(second);
			Vector3f translationB = Maths.getTranslationFromMat4(second);
			
			// Construct joint transforms data structure to calculate interpolation
			JointTransform a = new JointTransform(translationA, rotationA);
			JointTransform b = new JointTransform(translationB, rotationB);
			
			// Calculate interpolation from Joint Transforms
			Matrix4f interpolatedMatrix = Maths.interpolate(a, b, progression);
			result.add(interpolatedMatrix);
		}
		
		return result;
	}
	
	private AnimatedFrame[] getPreviousAndNextFrames()
	{
		AnimatedFrame[] allFrames = new AnimatedFrame[currentAnimation.getFrames().size()];
		for(int i = 0; i < allFrames.length; i++)
		{
			allFrames[i] = currentAnimation.getFrames().get(i);
		}
		
		AnimatedFrame previousFrame = allFrames[0];
		AnimatedFrame nextFrame = allFrames[0];
		for(int i = 1; i < allFrames.length; i++)
		{
			nextFrame = allFrames[i];
			if(nextFrame.getTimeStamp() > animationTime)
			{
				break;
			}
			previousFrame = allFrames[i];
		}
		
		return new AnimatedFrame[] { previousFrame, nextFrame };
	}
	
	
	
	
	
	
}
