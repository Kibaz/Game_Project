package components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import combat.Ability;
import combat.Effect;

public class CombatManager extends Component {
	
	private Map<Ability,Effect> effectQueue;
	
	public CombatManager(String name)
	{
		super(name);
		init();
	}

	@Override
	protected void init() {
		effectQueue = new HashMap<>();
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
	protected void cleanUp() {
		loader.cleanUp();	
	}
	
	public void submitEffect(Effect effect)
	{
		effectQueue.put(effect.getAbility(), effect);
	}

}
