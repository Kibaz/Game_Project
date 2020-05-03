package entities;

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
	
	private Entity player;
	
	private float distFromPlayer = 50;
	private float playerOrbit = 0;
	
	private float zoom = 20;
	
	
	public Camera(Entity player,boolean defaultCam)
	{
		this.player = player;
		if(defaultCam)
		{
			GLFW.glfwSetScrollCallback(Window.getWindowID(), new GLFWScrollCallback() {

				@Override
				public void invoke(long window, double xOffset, double yOffset) {
					float zoomLevel = 0;
					zoomLevel = (float) (yOffset * 5f);
					distFromPlayer -= zoomLevel;
				}
				
			});
		}
	}
	
	public void move()
	{
		calcPitch();
		calcOrbit();
		float horizontalDist = calcHorizontalDist();
		float verticalDist = calcVerticalDist();
		calcCameraPosition(horizontalDist, verticalDist);
		this.yaw = 180 - (player.getRotY() + playerOrbit);
	}
	
	public Vector3f getPosition()
	{
		return this.position;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}

	public float getPitch() 
	{
		return pitch;
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = pitch;
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
	
	private float calcHorizontalDist()
	{
		return (float) (distFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calcVerticalDist()
	{
		return (float) (distFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calcPitch()
	{
		if(MouseButton.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_2))
		{
			float pitchChange = Window.getMouseDY() * 0.1f;
			pitch -= pitchChange;
		}
	}
	
	private void calcOrbit()
	{
		if(MouseButton.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_1))
		{
			float orbitChange = Window.getMouseDX() * 0.3f;
			playerOrbit -= orbitChange;
		}
	}
	
	private void calcCameraPosition(float horizontal, float vertical)
	{
		float angle = player.getRotY() + playerOrbit;
		float xOffset = (float) (horizontal * Math.sin(Math.toRadians(angle)));
		float zOffset = (float) (horizontal * Math.cos(Math.toRadians(angle)));
		position.x = player.getPosition().x - xOffset;
		position.z = player.getPosition().z - zOffset;
		position.y = player.getPosition().y + vertical;
		
	}
	
	
}
