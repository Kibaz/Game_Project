package equip;

import components.EntityInformation;

public class Armour extends ItemStat{
	
	private int value; // Apply flat amount to entity's armour value

	public Armour(int value) {
		super("Armour", "Reduces physical damage taken");
		this.value = value;
	}

	@Override
	protected void apply(EntityInformation info) {
		info.modifyArmour(value);
	}
	
	@Override
	protected void remove(EntityInformation info) {
		info.modifyArmour(-value);
	}
	
	@Override
	public String display() {
		return ((value > 0) ? "+" : "-") + value + " Armour";
	}

	public int getValue() {
		return value;
	}



}
