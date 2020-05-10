package combat;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import components.CombatManager;
import components.EntityInformation;
import entities.Entity;
import equip.EquipInventory;
import equip.EquipItem;
import equip.EquipSlot;
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
		
		
		// Modify durability of damaged enemy's armour items
		if(entity.hasComponent(EquipInventory.class))
		{
			EquipInventory inventory = entity.getComponentByType(EquipInventory.class);
			Map<EquipSlot,Entity> items = inventory.getInventory();
			for(Entry<EquipSlot,Entity> entry: items.entrySet())
			{
				Entity item = entry.getValue();
				if(item != null)
				{
					EquipItem equipItem = item.getComponentByType(EquipItem.class);
					equipItem.setDurability(equipItem.getDurability()-1);
				}
			}
		}
		
		info.setHealth(info.getHealth() - damage);
		
		this.generateCombatText(entity, damage);
		this.ended = true;
		
	}

	@Override
	protected Effect clone() {
		return new InstantDamage(this);
	}

}
