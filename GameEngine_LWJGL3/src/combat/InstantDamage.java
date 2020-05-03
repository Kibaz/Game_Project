package combat;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import components.CombatManager;
import components.EntityInformation;
import entities.Entity;
import fontUtils.GUIText;
import runtime.Main;

public class InstantDamage extends Effect {

	private int maxDamage;
	private int minDamage;
	
	private Random damageCalculator;
	
	public InstantDamage(int maxDamage, int minDamage)
	{
		super("Instant");
		this.maxDamage = maxDamage;
		this.minDamage = minDamage;
		this.damageCalculator = new Random();
	}
	
	public InstantDamage(InstantDamage other) {
		super(other);
		this.maxDamage = other.maxDamage;
		this.minDamage = other.minDamage;
		this.damageCalculator = new Random();
	}

	@Override
	public void apply(Entity entity) {
		this.ended = false;
		CombatManager combatManager = entity.getComponentByType(CombatManager.class);
		if(combatManager != null)
		{
			combatManager.submitEffect(new InstantDamage(this));
		}
	}

	@Override
	public void execute(Entity entity) {
		EntityInformation info = entity.getComponentByType(EntityInformation.class);
		
		int damage = damageCalculator.nextInt(maxDamage - minDamage + 1) + minDamage;
		
		// Modify damage using entity's info
		float totalDamage = damage * ((float) damage / (damage + info.getArmour()));
		damage = (int) Math.ceil(totalDamage);
		
		info.setHealth(info.getHealth() - damage);
		
		this.generateCombatText(entity, damage);
		this.ended = true;
		
	}

	@Override
	protected Effect clone() {
		return new InstantDamage(this);
	}

}
