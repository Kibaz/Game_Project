package components;

import org.lwjgl.util.vector.Vector3f;

import guis.HUD;

public class FloatingHealthBar extends Component {
	
	private HUD healthFrame;
	private HUD healthPool;
	
	public FloatingHealthBar(String name)
	{
		super(name);
		init();
	}

	@Override
	protected void init() {
		this.healthFrame = new HUD(loader.loadTexture("res/basic_health_bar_frame.png"), new Vector3f(0,0,0), new Vector3f(4,4,4),0);
		this.healthPool = new HUD(loader.loadTexture("res/basic_health_pool.png"), new Vector3f(0,0,0), new Vector3f(4,4,4), 0);
		this.healthPool.setAsHealthPool();
	}

	@Override
	public void update() {
		// Check if this component has been assigned to an entity
		if(entity != null)
		{
			// Update position of HUDs based on entity
			healthPool.setPosition(new Vector3f(entity.getPosition().x,entity.getPosition().y + 
					entity.getModel().getBaseModel().getModelZWidth() + 1, entity.getPosition().z));
			healthFrame.setPosition(new Vector3f(entity.getPosition().x,entity.getPosition().y + 
					entity.getModel().getBaseModel().getModelZWidth() + 1, entity.getPosition().z));
			
			EntityInformation info = entity.getComponentByType(EntityInformation.class);
			if(info != null)
			{
				float scaleFactor = info.getHealth() / (float) info.getMaxHealth();
				healthPool.getScale().x = scaleFactor * healthPool.getMaxScaleX();
			}
		}
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		loader.cleanUp();
		
	}

	public HUD getHealthFrame() {
		return healthFrame;
	}

	public HUD getHealthPool() {
		return healthPool;
	}
	
	

}
