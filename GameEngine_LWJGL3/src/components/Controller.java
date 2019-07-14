package components;

import org.lwjgl.glfw.GLFW;

import inputs.Input;
import inputs.KeyboardHandler;
import networking.Client;
import rendering.Window;

public class Controller extends Component{
	
	private Client client; // Required to send inputs
	
	public Controller(String name, Client client)
	{
		super(name);
		this.client = client;
		init();
	}

	@Override
	protected void init() {
		
	}

	@Override
	public void update() {
		if(entity != null)
		{
			Motion motion = entity.getComponentByType(Motion.class);
			if(motion != null)
			{
				checkInputs(motion);
			}
		}
	}

	@Override
	public void start() {
		
	}

	@Override
	public void cleanUp() {
		
	}
	
	private void checkInputs(Motion motion)
	{
		/* Check whether a key has been pressed */
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_W))
		{
			Input input = new Input("w key pressed",Window.getFrameTime());
			client.processInput(input);
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S))
		{
			Input input = new Input("s key pressed",Window.getFrameTime());
			client.processInput(input);
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D))
		{
			Input input = new Input("d key pressed",Window.getFrameTime());
			client.processInput(input);
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A))
		{
			Input input = new Input("a key pressed",Window.getFrameTime());
			client.processInput(input);
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE)){
			Input input = new Input("space key pressed",Window.getFrameTime());
			client.processInput(input);
		}
	}

}
