package inventory;

import java.util.ArrayList;
import java.util.List;

import guis.GUI;
import guis.GUITexture;

public class ItemStack {
	
	private String itemName; // Name of the item being stacked
	
	// UI Components
	private GUI itemIcon; // Icon to display for stack
	
	private int limit;
	
	private List<Item> stack; // List of items in stack
	
	public ItemStack(String itemName,GUI itemIcon,int limit)
	{
		this.limit = limit;
		this.itemName = itemName;
		this.itemIcon = itemIcon;
		itemIcon.setClickable(true);
		this.stack = new ArrayList<>();
	}
	
	public void addItem(Item item)
	{
		stack.add(item);
	}
	
	public void removeItem(Item item)
	{
		stack.remove(item);
	}
	
	public Item removeTop()
	{
		return stack.remove(stack.size()-1);
	}
	
	public boolean hasSpace()
	{
		return stack.size() < limit;
	}

	public int getCount() {
		return stack.size();
	}

	public int getLimit() {
		return limit;
	}

	public List<Item> getStack() {
		return stack;
	}

	public String getItemName() {
		return itemName;
	}

	public GUI getItemIcon() {
		return itemIcon;
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
