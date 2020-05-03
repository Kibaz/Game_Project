package equip;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import components.Component;
import entities.Entity;
import guis.GUI;
import guis.GUITexture;

public class EquipItem extends Component {
	
	/*
	 * Store a 2D graphic to display in inventory
	 * or equip inventory
	 */
	
	private GUITexture iconTexture;
	private GUI icon;
	
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
	
	private int durability;
	
	private String itemName;
	
	private String attachPoint; // Name of bone where item will be attached
	
	private Entity parent; // Entity to attach to
	
	private List<ItemStat> stats;
	
	// Mark the slot which the item occupies in equip inventory
	private EquipSlot equipSlot;
	
	public EquipItem(String itemName,int maxDurability, int durability,
			String attachPoint,EquipSlot equipSlot,int iconTexture, int durabilityTexture)
	{
		super("equipItem");
		this.equipSlot = equipSlot;
		this.stats = new ArrayList<>();
		this.itemName = itemName;
		this.attachPoint = attachPoint;
		this.maxDurability = maxDurability;
		this.durability = durability;
		this.iconTexture = new GUITexture(iconTexture,new Vector2f(0,0),new Vector2f(1,1));
		this.icon = new GUI(this.iconTexture);
		icon.setVisible(false); // Not visible by default
		this.durabilityIndicatorTexture = new GUITexture(durabilityTexture,new Vector2f(0,0),new Vector2f(1,1));
		this.durabilityIndicator = new GUI(this.durabilityIndicatorTexture);
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
	
	public void setIconPosition(Vector2f position)
	{
		this.iconTexture.setPosition(position);
	}
	
	public void setIconScale(Vector2f scale)
	{
		this.iconTexture.setScale(scale);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	public String getItemName() {
		return itemName;
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

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
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

	public GUITexture getIconTexture() {
		return iconTexture;
	}

	public void setIconTexture(GUITexture iconTexture) {
		this.iconTexture = iconTexture;
	}

	public GUI getIcon() {
		return icon;
	}

	public void setIcon(GUI icon) {
		this.icon = icon;
	}
	
	
	
	
	
	
	
	
	
	

}
