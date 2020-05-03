package combat;

import org.lwjgl.util.vector.Vector3f;

import components.CombatManager;
import components.EntityInformation;
import entities.Entity;
import fontUtils.GUIText;
import runtime.Main;

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
	
	protected abstract Effect clone();
	
	public abstract void execute(Entity entity);
	
	protected void generateCombatText(Entity assocEntity, int damage)
	{
		Vector3f combatTextPos = new Vector3f(assocEntity.getPosition().x,assocEntity.getPosition().y +
				assocEntity.getModelHeight(), assocEntity.getPosition().z);
		GUIText floatingCombatText = new GUIText(String.valueOf(damage),3f,Main.testFontStyle,combatTextPos,1,true);
		EntityInformation info = assocEntity.getComponentByType(EntityInformation.class);
		if(info != null && info.isHostile())
		{
			floatingCombatText.setColour(1f, 0.84f, 0f);
		}
		else
		{
			floatingCombatText.setColour(1f, 0f, 0f);
		}
		
		floatingCombatText.setAssocEntity(assocEntity);
	}
	
	public boolean hasEnded()
	{
		return ended;
	}

	public Ability getAbility() {
		return ability;
	}
	
	
	
}
