package equip;

import components.EntityInformation;

public class Damage extends ItemStat {
	
	private int min;
	
	private int max;

	public Damage(int min, int max) {
		super("Damage", "A fixed range value which determines damage dealt");
		this.min = min;
		this.max = max;
	}

	@Override
	protected void apply(EntityInformation info) {
		info.setMinDamage(min);
		info.setMaxDamage(max);	
	}

	@Override
	protected void remove(EntityInformation info) {
		info.setMinDamage(1); // Reset to default damage values
		info.setMaxDamage(2); // Reset to default damage values
	}

	@Override
	public String display() {
		return min + " - " + max + " Damage";
	}
	

}
