package combat;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import guis.GUI;
import rendering.Loader;
import rendering.Window;
import terrains.Terrain;

public class Ability{
	
	private String name;
	private String description;
	
	private GUI gui;
	
	private DamageIndicator damageIndicator;
	
	public static enum Type {
		INSTANT,
		CHARGED,
		INTERACTIVE,
	}
	
	private Type type;
	
	private int minDamage;
	private int maxDamage;
	
	private float cooldown;
	protected boolean onCooldown = false;
	
	private float cooldownElapse;
	
	private Effect effect;
	
	public Ability(GUI gui,String name, String description,DamageIndicator damageIndicator, Effect effect,float cooldown, Type type)
	{
		this.gui = gui;
		this.name = name;
		this.description = description;
		this.cooldown = cooldown;
		this.cooldownElapse = cooldown;
		this.type = type;
		this.damageIndicator = damageIndicator;
		this.effect = effect;
	}
	
	public boolean inRange(Entity entity)
	{
		return this.damageIndicator.intersectsEntity(entity);
	}
	
	public void doEffect(List<Entity> entities)
	{
		if(onCooldown) // Exit if on cool-down
		{
			// Report ability on cool-down
			return;
		}
		
		// If instant cast
		if(type == Type.INSTANT)
		{
			for(Entity entity: entities)
			{
				if(!entity.isStaticModel())
				{
					if(damageIndicator.intersectsEntity(entity))
					{
						effect.apply(entity);
					}
				}
			}
			
			onCooldown = true;
		}
		
		
	}
	
	
	public void update(Loader loader, List<Terrain> terrains)
	{
		if(!onCooldown)
		{
			return;
		}
		cooldownElapse -= Window.getFrameTime();
		
		if(cooldownElapse < 0)
		{
			cooldownElapse = cooldown;
			onCooldown = false;
		}
		
		this.damageIndicator.updateIndicator(loader,terrains);
	}
	
	public void setIndicatorPosition(Vector3f position)
	{
		damageIndicator.setPosition(position);
	}
	
	public void setIndicatorRotation(float rotY)
	{
		damageIndicator.setRotY(rotY);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getMinDamage() {
		return minDamage;
	}
	
	public Type getType()
	{
		return type;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public float getCooldown() {
		return cooldown;
	}
	
	public boolean isOnCooldown() {
		return onCooldown;
	}

	public DamageIndicator getDamageIndicator()
	{
		return damageIndicator;
	}

	public GUI getGui() {
		return gui;
	}

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}
	
	
	
	
	
	
	
	

}
