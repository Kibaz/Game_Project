package combat;

import components.CombatManager;
import entities.Entity;

public abstract class Effect {
	
	protected Ability ability;
	protected String description;
	protected String type;
	
	protected boolean ended;
	
	public Effect(String type)
	{
		this.type = type;
		this.ended = false;
	}
	
	public Effect(Effect effect)
	{
		this.type = effect.type;
		this.ended = effect.ended;
	}
	
	public abstract void apply(Entity entity);
	
	public abstract void execute(Entity entity);
	
	public boolean hasEnded()
	{
		return ended;
	}

	public Ability getAbility() {
		return ability;
	}
	
	
	
}
