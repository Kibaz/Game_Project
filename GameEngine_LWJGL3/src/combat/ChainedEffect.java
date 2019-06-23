package combat;

import java.util.ArrayList;
import java.util.List;

import components.CombatManager;
import entities.Entity;

public class ChainedEffect extends Effect {
	
	private List<Effect> chainedEffects;
	
	private int effectIndex = 0;
	
	public ChainedEffect(List<Effect> chainedEffects) {
		super("Chained");
		this.chainedEffects = chainedEffects;
	}
	
	public ChainedEffect(ChainedEffect other)
	{
		super(other);
		this.chainedEffects = new ArrayList<>(other.chainedEffects.size());
		for(Effect effect: other.chainedEffects)
		{
			this.chainedEffects.add(effect.clone());
		}
	}

	

	@Override
	public void execute(Entity entity) {
		/*
		 * Execution for a chained effect by default
		 * will execute each effect in the order
		 * of the list. This can be altered by altering
		 * the indexes of the list, however execution will
		 * always execute effects in the specified order.
		 */
		
		if(chainedEffects.size() == 0 && chainedEffects.isEmpty())
		{
			return;
		}
		
		if(effectIndex == chainedEffects.size())
		{
			this.ended = true;
			return;
		}
		
		// Check if the current effect has already been executed
		// If not, execute the effect
		// Otherwise, check for another effect and execute it
		if(!chainedEffects.get(effectIndex).ended)
		{
			chainedEffects.get(effectIndex).execute(entity);
		}
		else
		{
			effectIndex++;
			if(effectIndex < chainedEffects.size())
			{
				chainedEffects.get(effectIndex).execute(entity);
			}
		}
		

	}
	
	@Override
	public void apply(Entity entity) {
		this.ended = false;
		CombatManager combatManager = entity.getComponentByType(CombatManager.class);
		if(combatManager != null)
		{
			combatManager.submitEffect(new ChainedEffect(this));
		}
	}

	public void addEffect(Effect effect)
	{
		this.chainedEffects.add(effect);
	}

	public void removeEffect(Effect effect)
	{
		this.chainedEffects.remove(effect);
	}

	public List<Effect> getChainedEffects() {
		return chainedEffects;
	}

	@Override
	protected Effect clone() {
		return new ChainedEffect(this);
	}
	
	

}
