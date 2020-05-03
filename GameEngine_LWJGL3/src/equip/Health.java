package equip;

import components.EntityInformation;

public class Health extends ItemStat {
	
	private int value;
	
	public Health(int value)
	{
		super("Health","Increases maximum health pool by a flat amount");
		this.value = value;
	}

	@Override
	protected void apply(EntityInformation info) {
		info.modifyMaxHealth(value);
	}
	
	@Override
	protected void remove(EntityInformation info) {
		info.modifyMaxHealth(-value);
	}

	public int getValue() {
		return value;
	}



	
}
