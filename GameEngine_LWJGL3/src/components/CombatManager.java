package components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import combat.Ability;
import combat.Effect;

public class CombatManager extends Component {
	
	private Map<Ability,Effect> effectQueue;
	
	private boolean inCombat;
	
	public CombatManager(String name)
	{
		super(name);
		init();
	}

	@Override
	protected void init() {
		effectQueue = new HashMap<>();
		inCombat = false;
	}

	@Override
	public void update() {
		Iterator<Entry<Ability,Effect>> effectIterator = effectQueue.entrySet().iterator();
		while(effectIterator.hasNext())
		{
			Entry<Ability, Effect> current = effectIterator.next();
			Effect effect = current.getValue();
			if(!effect.hasEnded())
			{
				effect.execute(entity);
			}
			else
			{
				effectIterator.remove();
			}
		}
	}

	@Override
	public void start() {
		
	}

	@Override
	public void cleanUp() {
		loader.cleanUp();	
	}
	
	public void submitEffect(Effect effect)
	{
		effectQueue.put(effect.getAbility(), effect);
	}

	public boolean isInCombat() {
		return inCombat;
	}

	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}
	
	

}
