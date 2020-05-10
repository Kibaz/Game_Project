package equip;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import components.Component;
import entities.Entity;
import guis.GUI;
import guis.GUITexture;
import inventory.Inventory;
import inventory.Item;
import rendering.Window;

public class EquipItem extends Item {
	
	// Display GUI for durability
	private GUITexture durabilityIndicatorTexture;
	private GUI durabilityIndicator;
	
	/*
	 * Store item statistics - e.g. health, armour,
	 * resistance, damage modifiers, etc...
	 */
	
	/*
	 * Durability - not a modifier added to an entity
	 * when durability is low, all stats on this item
	 * will be removed from the entity
	 */
	private final int maxDurability;
	private final float maxDurabilityScale;
	
	private int durability;
	
	private String attachPoint; // Name of bone where item will be attached
	
	private List<ItemStat> stats;
	
	// Mark the slot which the item occupies in equip inventory
	private EquipSlot equipSlot;
	
	public EquipItem(String itemName,int maxDurability, int durability,
			String attachPoint,EquipSlot equipSlot,int iconTexture, int durabilityTexture)
	{
		super(itemName,iconTexture);
		this.equipSlot = equipSlot;
		this.stats = new ArrayList<>();
		this.attachPoint = attachPoint;
		this.maxDurability = maxDurability;
		this.durability = durability;
		this.maxDurabilityScale = 237f/Window.getWidth();
		this.durabilityIndicatorTexture = new GUITexture(durabilityTexture,new Vector2f(0,0),new Vector2f(maxDurabilityScale,0.2f));
		this.durabilityIndicator = new GUI(this.durabilityIndicatorTexture);
		durabilityIndicator.setVisible(false);
	}
	
	public void addStat(ItemStat stat)
	{
		this.stats.add(stat);
	}
	
	public void removeStat(ItemStat stat)
	{
		this.stats.remove(stat);
	}
	
	public void setDurabilityPosition(Vector2f position)
	{
		this.durabilityIndicatorTexture.setPosition(position);
	}
	
	public void setDurabilityScale(Vector2f scale)
	{
		this.durabilityIndicatorTexture.setScale(scale);
	}
	
	public ItemStat getByName(String statName)
	{
		for(ItemStat stat: stats)
		{
			if(stat.getName().equals(statName))
			{
				return stat;
			}
		}
		
		return null;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		
		super.update();
		// Reduce durability
		float scaleFactor = durability / (float) maxDurability;
		float prevScale = durabilityIndicatorTexture.getScale().x;
		durabilityIndicatorTexture.getScale().x = scaleFactor * maxDurabilityScale;
		durabilityIndicatorTexture.getPosition().x -= (prevScale - durabilityIndicatorTexture.getScale().x) * 0.725f; 
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	// Getters and Setters
	public List<ItemStat> getStats() {
		return stats;
	}

	public EquipSlot getEquipSlot() {
		return equipSlot;
	}

	public String getAttachPoint() {
		return attachPoint;
	}
	
	public void setAttachPoint(String boneName)
	{
		this.attachPoint = boneName;
	}

	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		this.durability = durability;
	}

	public int getMaxDurability() {
		return maxDurability;
	}

	public GUITexture getDurabilityIndicatorTexture() {
		return durabilityIndicatorTexture;
	}

	public void setDurabilityIndicatorTexture(GUITexture durabilityIndicatorTexture) {
		this.durabilityIndicatorTexture = durabilityIndicatorTexture;
	}

	public GUI getDurabilityIndicator() {
		return durabilityIndicator;
	}

	public void setDurabilityIndicator(GUI durabilityIndicator) {
		this.durabilityIndicator = durabilityIndicator;
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
