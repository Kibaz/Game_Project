package rendering;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import inputs.MouseButton;
import inputs.MouseCursor;
import networking.Client;

public class Window {
	
	private GLFWErrorCallback errorCallback;
	private static GLFWCursorPosCallback cursorCallback;
	private static GLFWMouseButtonCallback mouseButtonCallback;
	
	private static long windowID;
	private static int height = 900;
	private static int width = 1600;
	
	private static double lastFrameInterval;
	private static float delta;
	
	private static float lastCursorPosX;
	private static float lastCursorPosY;
	
	private static float mouseDX;
	private static float mouseDY;
	
	public static void init()
	{
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Init GLFW
		if(!GLFW.glfwInit())
		{
			throw new IllegalStateException("Unable to intialise GLFW");
		}
		
		// Configure
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		
		windowID = GLFW.glfwCreateWindow(width, height, "GAME ENGINE", MemoryUtil.NULL, MemoryUtil.NULL);
		
		if(windowID == MemoryUtil.NULL)
		{
			throw new RuntimeException("Failed to create the GLFW window");
		}
		
		// Set up key callback and mouse callbacks
		cursorCallback = new MouseCursor();
		GLFW.glfwSetCursorPosCallback(windowID, cursorCallback);
		
		mouseButtonCallback = new MouseButton();
		GLFW.glfwSetMouseButtonCallback(windowID, mouseButtonCallback);
		
		// Set up window resize callback
		GLFW.glfwSetWindowSizeCallback(windowID, new GLFWWindowSizeCallback(){

			@Override
			public void invoke(long window, int width, int height) {
				Window.width = width;
				Window.height = height;
				GL11.glViewport(0, 0, width, height);
			}	
			
		});
		
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer bufferWidth = stack.mallocInt(1);
			IntBuffer bufferHeight = stack.mallocInt(1);
			
			GLFW.glfwGetWindowSize(windowID, bufferWidth, bufferHeight);
			
			GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			
			GLFW.glfwSetWindowPos(windowID,
					((vidMode.width() - bufferWidth.get(0))/2), 
					((vidMode.height() - bufferHeight.get(0))/2));
			
			GLFW.glfwMakeContextCurrent(windowID);
			GL.createCapabilities();
			
			GLFW.glfwSwapInterval(1);
			
			GLFW.glfwShowWindow(windowID);
			
			lastFrameInterval = GLFW.glfwGetTime();
			lastCursorPosX = MouseCursor.getXPos();
			lastCursorPosY = MouseCursor.getYPos();
		}
	}
	
	public static void clear()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public static void update()
	{
		GLFW.glfwSwapBuffers(windowID);
		GLFW.glfwPollEvents();
		double currentFrameTime = GLFW.glfwGetTime();
		delta = (float) (currentFrameTime - lastFrameInterval);
		lastFrameInterval = currentFrameTime;
		
		/* Calculate delta mouse cursor position for camera control */
		float currentMousePosX = MouseCursor.getXPos();
		float currentMousePosY = MouseCursor.getYPos();
		mouseDX = currentMousePosX - lastCursorPosX;
		mouseDY = currentMousePosY - lastCursorPosY;
		lastCursorPosX = currentMousePosX;
		lastCursorPosY = currentMousePosY;
	}
	
	public static void destroy()
	{
		GLFW.glfwTerminate();
		mouseButtonCallback.free();
		cursorCallback.free();
	}
	
	public static boolean closed()
	{
		return GLFW.glfwWindowShouldClose(windowID);
	}
	
	public static long getWindowID()
	{
		return windowID;
	}
	public static int getHeight()
	{
		return height;
	}
	public static int getWidth()
	{
		return width;
	}
	
	public static float getFrameTime()
	{
		return delta;
	}
	
	public static float getMouseDX()
	{
		return mouseDX;
	}
	
	public static float getMouseDY()
	{
		return mouseDY;
	}
	

}
