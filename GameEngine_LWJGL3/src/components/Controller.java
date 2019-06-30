package components;

import org.lwjgl.glfw.GLFW;
import inputs.KeyboardHandler;

public class Controller extends Component{
	
	public Controller(String name)
	{
		super(name);
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
			motion.setCurrentSpeed(motion.getRunSpeed());
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S))
		{
			motion.setCurrentSpeed(-motion.getWalkSpeed());
		}
		
		else
		{
			motion.setCurrentSpeed(0);
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D))
		{
			motion.setCurrentTurnSpeed(-Motion.getTurnSpeed());
		}
		
		else if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A))
		{
			motion.setCurrentTurnSpeed(Motion.getTurnSpeed());
		}
		else
		{
			motion.setCurrentTurnSpeed(0);
		}
		
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE)){
			jump(motion);
		}
	}
	
	private void jump(Motion motion)
	{
		if(!motion.isAirborne())
		{
			motion.setJumpSpeed(Motion.getUpForce());
			motion.setAirborne(true);
		}
	}

}
