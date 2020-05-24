package professions;

import org.lwjgl.glfw.GLFW;

import components.Component;
import inputs.Input;
import inputs.KeyboardHandler;
import rendering.Window;

public class InteractiveNode extends Component {
	
	private final float INTERACTION_TIME = 2;
	
	protected String title;
	
	protected String nodeName;
	
	protected int stages; // Determines the number of attempts to gather the full resource
	
	protected int count; // Number of resources remaining
	
	protected boolean locked;
	
	protected boolean empty; // Marks the node as empty - can be disposed of
	
	protected float lockTime;
	
	public InteractiveNode(String title,String nodeName,int stages)
	{
		super("interactive_node");
		this.title = title;
		this.stages = stages;
		this.count = stages;
		this.locked = false;
		this.lockTime = 0;
		this.empty = false;
		this.nodeName = nodeName;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		if(!entity.isClickable() && !empty) entity.setClickable(true);
		if(!empty)
		{
			handleInteractEvent();
			checkStage();
		}
		else
		{
			this.entity.setClickable(false);
			this.entity.setHovered(false);
		}
	}
	
	private void checkStage()
	{
		if(count == 0)
		{
			// Mark the node for removal
			empty = true;
		}
	}
	
	private void handleInteractEvent()
	{
		if(entity.isClicked() && !locked)
		{
			locked = true;
		}
		
		if(locked)
		{
			lockTime += Window.getFrameTime();
			if(lockTime > INTERACTION_TIME)
			{
				unlock();
				count--;
			}
			
			checkInputs(); // Player could move and cancel the interaction
		}
		
		
	}
	
	private void unlock()
	{
		locked = false;
		lockTime = 0;
	}
	
	private void checkInputs()
	{
		/* Check whether a key has been pressed */
		// If moved
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_W) || 
		   KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S) ||
		   KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D) ||
		   KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A) ||
		   KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE))
		{
			unlock();
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	// Getters and Setters
	public int getStages() {
		return stages;
	}

	public String getTitle() {
		return title;
	}

	public int getCount() {
		return count;
	}

	public boolean isLocked() {
		return locked;
	}

	public float getLockTime() {
		return lockTime;
	}

	public String getNodeName() {
		return nodeName;
	}
	
	
	
	
	

}
