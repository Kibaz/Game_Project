package inputs;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import rendering.Window;

public class KeyboardHandler{
	
	
	public static boolean isKeyDown(int keycode)
	{
		return GLFW.glfwGetKey(Window.getWindowID(), keycode) == GLFW.GLFW_PRESS;
	}
	
	public static boolean isKeyUp(int keycode)
	{
		return GLFW.glfwGetKey(Window.getWindowID(), keycode) == GLFW.GLFW_RELEASE;
	}

}
