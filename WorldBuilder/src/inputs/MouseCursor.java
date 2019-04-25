package inputs;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MouseCursor extends GLFWCursorPosCallback{
	
	private static float xPos;
	private static float yPos;
	
	@Override
	public void invoke(long window, double xPos, double yPos) {
		this.xPos = (float) xPos;
		this.yPos = (float) yPos;
	}
	
	public static float getYPos()
	{
		return yPos;
	}
	
	public static float getXPos()
	{
		return xPos;
	}

}
