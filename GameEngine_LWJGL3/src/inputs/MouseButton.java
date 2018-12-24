package inputs;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseButton extends GLFWMouseButtonCallback{
	
	private static boolean[] buttons = new boolean[1024];

	@Override
	public void invoke(long window, int button, int action, int mods) {
		buttons[button] = action != GLFW.GLFW_RELEASE;
	}
	
	public static boolean isButtonDown(int button)
	{
		return buttons[button];
	}

}
