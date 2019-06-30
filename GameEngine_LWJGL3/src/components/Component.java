package components;

import entities.Entity;
import rendering.Loader;

public abstract class Component {
	
	protected Entity entity; // Reference to the game object this component is added to
	protected String name;
	protected boolean enabled;
	protected Loader loader; // For initialising certain objects which need to be loaded to VAO's, Texture units etc...
	
	public Component(String name)
	{
		this.name = name;
		this.enabled = true;
		this.loader = new Loader();
	}
	
	public Component(){}
	
	protected abstract void init();
	
	public abstract void update();
	
	public abstract void start();
	
	public abstract void cleanUp();

	public Entity getEntity() {
		return entity;
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	
	
	
	

}
