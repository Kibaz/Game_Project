package components;

import entities.Entity;

public abstract class Component {
	
	protected Entity entity;
	protected String name;
	
	public Component(String name)
	{
		this.name = name;
	}
	
	protected abstract void init();
	
	public abstract void udpate();
	
	public abstract void start();

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public String getName() {
		return name;
	}
	
	

}
