package inventory;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import components.Component;
import entities.Entity;
import equip.ItemStat;
import guis.GUI;
import guis.GUITexture;

public class Item extends Component {
	
	// UI Components
	private GUITexture iconTexture;
	private GUI icon;
	
	private String itemName;
	
	private int stackLimit = 1; // By default - cannot stack items
	
	private Entity parent; // Entity item is bound to
	
	private List<GUI> guis;
	
	private List<ItemStat> stats; // Store item stats
	
	public Item(String itemName,int iconTexture)
	{
		super("item-"+itemName);
		this.itemName = itemName;
		this.iconTexture = new GUITexture(iconTexture,new Vector2f(0,0),new Vector2f(1,1));
		this.icon = new GUI(this.iconTexture);
		icon.setVisible(false);
		icon.setClickable(true);
		icon.setFbo(true);
		this.stats = new ArrayList<>();
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}
	
	public void addStat(ItemStat stat)
	{
		this.stats.add(stat);
	}
	
	public void removeStat(ItemStat stat)
	{
		this.stats.remove(stat);
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
	
	// Getters and Setters
	public List<ItemStat> getStats() {
		return stats;
	}
	
	public void setIconPosition(Vector2f position)
	{
		this.iconTexture.setPosition(position);
	}
	
	public void setIconScale(Vector2f scale)
	{
		this.iconTexture.setScale(scale);
	}
	
	public String getItemName() {
		return itemName;
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
	
	public int getStackLimit() {
		return stackLimit;
	}

	public void setStackLimit(int limit)
	{
		this.stackLimit = limit;
	}
	
	public Entity getParent() {
		return parent;
	}
	
	public void setParent(Entity parent) {
		this.parent = parent;
	}
	
	public List<GUI> getGuis()
	{
		return guis;
	}
	
}
