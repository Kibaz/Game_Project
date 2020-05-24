package equip;

import components.EntityInformation;
import entities.Entity;

public abstract class ItemStat {
	
	/*
	 * Describes a property which can
	 * be added to an equip item to
	 * modify damage dealt and damage
	 * taken. Could also affect healing
	 * properties as well.
	 */
	
	private String name; // Name of the stat
	private String description; // Description of the stat's effect
	
	// Constructor
	public ItemStat(String name,String description)
	{
		this.name = name;
		this.description = description;
	}
	
	// Abstract method to determine how the modifier is applied to an entity
	protected abstract void apply(EntityInformation info);
	
	// Abstract method to remove the modifier from the entity
	protected abstract void remove(EntityInformation info);
	
	public abstract String display();

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	} 
	
	
	
	

}
