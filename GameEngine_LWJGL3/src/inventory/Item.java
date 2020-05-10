package inventory;

import org.lwjgl.util.vector.Vector2f;

import components.Component;
import entities.Entity;
import equip.EquipInventory;
import equip.EquipItem;
import guis.GUI;
import guis.GUITexture;

public class Item extends Component {
	
	private GUITexture iconTexture;
	private GUI icon;
	
	private String itemName;
	
	private int stackLimit = 1; // By default - cannot stack items
	
	private Entity parent; // Entity item is bound to
	
	public Item(String itemName,int iconTexture)
	{
		super("item-"+itemName);
		this.itemName = itemName;
		this.iconTexture = new GUITexture(iconTexture,new Vector2f(0,0),new Vector2f(1,1));
		this.icon = new GUI(this.iconTexture);
		icon.setVisible(false);
		icon.setClickable(true);
		icon.setFbo(true);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
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

}
