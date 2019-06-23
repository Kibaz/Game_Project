package combat;

import java.util.Random;

import components.CombatManager;
import components.HealthBarFrame;
import entities.Entity;

public class InstantDamage extends Effect {

	private int maxDamage;
	private int minDamage;
	
	private Random damageCalculator;
	
	public InstantDamage(int maxDamage, int minDamage)
	{
		super("Instant");
		this.maxDamage = maxDamage;
		this.minDamage = minDamage;
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
		HealthBarFrame healthBar = entity.getComponentByType(HealthBarFrame.class);
		
		int damage = damageCalculator.nextInt(maxDamage - minDamage + 1) + minDamage;
		
		healthBar.setHealth(healthBar.getHealth() - damage);
		
		this.ended = true;
		
	}

}
