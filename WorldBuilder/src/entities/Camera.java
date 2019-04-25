package entities;

import java.awt.event.MouseEvent;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.util.vector.Vector3f;

import inputs.MouseButton;
import rendering.Window;

public class Camera {
	
	private Vector3f position = new Vector3f(100,1,100);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;
	
	private float orbit = 0;
	
	private float zoom = 0;
	
	public Camera()
	{

	}
	
	public void move()
	{
		
		float horizontalDist = calcHorizontalDist();
		float verticalDist = calcVerticalDist();
		calcCameraPosition(horizontalDist,verticalDist);
	}
	
	public void calcPitch(float pitchChange)
	{
		pitch -= pitchChange;
	}
	
	public void calcOrbit(float orbitChange)
	{
		orbit -= orbitChange;
	}
	
	public void calcZoom(float zoomChange)
	{
		zoom += zoomChange * 5f;
	}
	
	private float calcHorizontalDist()
	{
		return (float) (zoom * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calcVerticalDist()
	{
		return (float) (zoom * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calcCameraPosition(float horizontal, float vertical)
	{
		float angle = orbit;
		float xOffset = (float) (horizontal * Math.sin(Math.toRadians(angle)));
		float zOffset = (float) (horizontal * Math.cos(Math.toRadians(angle)));
		position.x = xOffset;
		position.z = zOffset;
		position.y = vertical;
		
	}
	
	public Vector3f getPosition()
	{
		return this.position;
	}

	public float getPitch() 
	{
		return pitch;
	}

	public float getYaw() {
		
		return yaw;
	}

	public float getRoll() 
	{
		return roll;
	}
	
	public void invertPitch()
	{
		this.pitch = -pitch;
	}
	
	
}
