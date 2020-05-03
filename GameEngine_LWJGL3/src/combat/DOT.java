package combat;

import java.util.Random;

import components.CombatManager;
import components.EntityInformation;
import entities.Entity;
import rendering.Window;

public class DOT extends Effect{
	
	private int minDamagePerTick;
	private int maxDamagePerTick;
	
	private float tickRate;
	
	private int numTicks;
	
	private int tickCount;
	
	private float duration;
	
	private float timeElapsed;
	
	private Random damageCalculator;

	public DOT(int maxDamage, int minDamage, float tickRate, int numTicks) {
		super("DOT");
		this.maxDamagePerTick = maxDamage;
		this.minDamagePerTick = minDamage;
		this.tickRate = tickRate;
		this.numTicks = numTicks;
		this.duration = tickRate * numTicks;
		this.timeElapsed = 0;
		this.tickCount = 0;
		this.damageCalculator = new Random();
	}
	
	public DOT(DOT other)
	{
		super(other);
		this.maxDamagePerTick = other.maxDamagePerTick;
		this.minDamagePerTick = other.minDamagePerTick;
		this.tickRate = other.tickRate;
		this.numTicks = other.numTicks;
		this.duration = other.duration;
		this.timeElapsed = 0;
		this.tickCount = 0;
		this.damageCalculator = new Random();
	}

	@Override
	public void execute(Entity entity) {
		
		timeElapsed += Window.getFrameTime();
		
		if(tickCount == numTicks)
		{
			ended = true; // Set effect as ended
			timeElapsed = 0; // Reset timer
			tickCount = 0; // Reset tick count
			return; // Do not carry out rest of effect
		}

		// Check if the timeElapsed exceeds the tick rate
		if(timeElapsed > tickRate)
		{
			EntityInformation info = entity.getComponentByType(EntityInformation.class);
			int damageOnTick = damageCalculator.nextInt((maxDamagePerTick - minDamagePerTick) + 1) + minDamagePerTick;
			info.setHealth(info.getHealth() - damageOnTick);
			
			this.generateCombatText(entity, damageOnTick);
			
			tickCount++; // Increment tick count
			timeElapsed = 0; // Reset timer
		}
		
	}
	
	@Override
	public void apply(Entity entity) {
		this.ended = false;
		CombatManager combatManager = entity.getComponentByType(CombatManager.class);
		if(combatManager != null)
		{
			combatManager.submitEffect(new DOT(this));
		}
	}
	
	public float getDuration()
	{
		return duration;
	}

	@Override
	protected Effect clone() {
		return new DOT(this);
	}

}
