package inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector2f;

import components.Component;
import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUI;
import guis.GUITexture;
import inputs.MouseCursor;
import rendering.Window;
import utils.Maths;

public class Inventory extends Component {
	
	// CONSTANTS
	private final int SHORT_KEY = GLFW.GLFW_KEY_B;
	private final int INVENTORY_WIDTH = 400;
	private final int INVENTORY_HEIGHT = 400;
	
	private final int SLOT_WIDTH = INVENTORY_WIDTH / 4;
	private final int SLOT_HEIGHT = INVENTORY_HEIGHT / 4;
	
	// Setup GUIs
	private GUITexture baseTexture;
	private GUI base;
	
	private GUI[][] slotGUIs;
	private GUIText[][] slotTexts;
	private GUIText[][] stackTexts;
	
	// Divide inventory into rows and columns (grid)
	private int rows; 
	private int columns;
	
	private int capacity;
	
	private ItemStack[][] slots; // 2D-Array of Item stacks (non stackable items will be a stack of 1)
	
	private List<GUI> guis;
	
	private boolean visible;
	
	private int lastKeyState = GLFW.GLFW_RELEASE;
	
	private List<GUIText> texts;
	
	private ItemStack selectedStack;
	private GUIText selectedStackText;
	
	public Inventory(int rows, int columns)
	{
		super("inventory");
		this.capacity = rows * columns;
		// Initialise at capacity
		this.slots = new ItemStack[rows][columns]; 
		this.slotGUIs = new GUI[rows][columns];
		this.slotTexts = new GUIText[rows][columns];
		this.stackTexts = new GUIText[rows][columns];
		this.guis = new ArrayList<>();
		this.texts = new ArrayList<>();
		this.visible = false;
		this.init();
	}
	
	// Attempts to add an item into the inventory
	public void addItem(Item item)
	{
		int[] pos = findItemStackPosition(item);
		if(pos.length > 0)
		{
			ItemStack stack = slots[pos[0]][pos[1]];
			stack.addItem(item);
			item.getEntity().setClickable(false); // As entity has been placed in inventory
			item.getEntity().setHovered(false);
			item.setParent(entity);
			// Update stack text status
			GUIText stackText = stackTexts[pos[0]][pos[1]];
			stackText.setContent(String.valueOf(stack.getCount()));
			if(visible) { 
				TextController.loadText(stackText);
			}
			else
			{
				TextController.removeText(stackText);
			}
			return;
		}
		
		pos = findEmptySlot(); // Find first available slot
		if(pos.length > 0)
		{
			// Copy the items icon and display in inventory stack
			GUITexture slotTexture = this.slotGUIs[pos[0]][pos[1]].getGUITexture();
			Vector2f iconPosition = new Vector2f(slotTexture.getPosition());
			Vector2f iconScale = new Vector2f((float) SLOT_WIDTH/Window.getWidth(),
					(float) SLOT_HEIGHT/Window.getHeight());
			GUITexture iconTexture = new GUITexture(item.getIcon().getGUITexture().getTexture(),iconPosition,iconScale);
			GUI icon = new GUI(iconTexture);
			guis.add(icon);
			// Create a new stack of the item
			ItemStack stack = new ItemStack(item.getItemName(),icon,item.getStackLimit());
			// Add item to new stack
			stack.addItem(item);
			// Set empty slot with new stack
			slots[pos[0]][pos[1]] = stack;
			item.getEntity().setClickable(false); // As entity has been placed in inventory
			item.getEntity().setHovered(false);
			item.setParent(entity);
			GUIText slotText = this.slotTexts[pos[0]][pos[1]];
			TextController.removeText(slotText); // Hide text as no longer empty slot
			return;
		}
		
		// Inventory is full!
		System.out.println("Inventory is full!");
	}
	
	public void removeItem(Item item)
	{
		// Find item and remove from stack
		for(int i = 0; i < slots.length; i++)
		{
			for(int j = 0; j < slots[i].length; j++)
			{
				ItemStack stack = slots[i][j];
				if(stack != null && stack.getItemName().equals(item.getItemName()))
				{
					stack.removeItem(item);
					
					if(stack.getCount() > 1)
					{
						stackTexts[i][j].setContent(String.valueOf(stack.getCount()));
					}
					else
					{
						stackTexts[i][j].setContent("");
					}
					
					// Check if stack is empty
					if(stack.getCount() == 0)
					{
						slots[i][j] = null; // Remove the stack
						guis.remove(stack.getItemIcon()); // Remove Icon
					}
						
				}
			}
		}
	}
	
	private void placeStack(int row,int column)
	{
		ItemStack comp = slots[row][column];
		if(comp == null)
		{
			// Slot is empty, allow for placement of stack
			int[] prevPos = findStackPosition(selectedStack);
			Vector2f position = new Vector2f(slotGUIs[row][column].getGUITexture().getPosition());
			selectedStack.getItemIcon().getGUITexture().setPosition(position);
			
			// Set text position
			calculateTextPosition(selectedStackText,row,column);
			swapStackText(prevPos,new int[] {row,column});
			// Set slot to selected stack
			slots[row][column] = selectedStack;
			slots[prevPos[0]][prevPos[1]] = null; // Set previous spot empty
			GUIText text = slotTexts[row][column];
			TextController.removeText(text);
		}
		else if(comp.equals(selectedStack))
		{
			Vector2f position = new Vector2f(slotGUIs[row][column].getGUITexture().getPosition());
			selectedStack.getItemIcon().getGUITexture().setPosition(position);
			calculateTextPosition(selectedStackText,row,column);
		}
		else if(!comp.getItemName().equals(selectedStack.getItemName()))
		{
			// Carry out swap
			swap(selectedStack,comp);
		}
		else {
			// Stacks contain the same item, check if full stacks
			
			if(comp.hasSpace() && selectedStack.hasSpace())
			{
				// Add whilst comparator has space
				while(comp.hasSpace() && selectedStack.getCount() > 0)
				{
					comp.addItem(selectedStack.removeTop());
				}
				return; // We do not want to re-position the selected stack
			}
			else
			{
				// Just swap
				swap(selectedStack,comp);
			}

		}
		selectedStack.getItemIcon().setSelected(false);
		selectedStack = null; // De-select stack
		selectedStackText = null; // De-select stack text
	}
	
	private void calculateTextPosition(GUIText text,int row, int column)
	{
		float baseWidth = (float) INVENTORY_WIDTH / Window.getWidth();
		float baseHeight = (float) INVENTORY_HEIGHT / Window.getHeight();
		float slotWidth = (float) SLOT_WIDTH / Window.getWidth();
		float slotHeight = (float) SLOT_HEIGHT / Window.getHeight();
		float textX = 1 - baseWidth + (slotWidth/8.75f) + (column * slotWidth);
		float textY = (1 - baseHeight) + (slotHeight/2.75f) + (row * slotHeight);
		text.setPosition(new Vector2f(textX+0.021f,textY+0.035f));
	}
	
	private int[] findItemPosition(Item item)
	{
		for(int i = 0; i < slots.length; i++)
		{
			for(int j = 0; j < slots[i].length; j++)
			{
				ItemStack stack = slots[i][j];
				if(stack != null && stack.getItemName().equals(item.getItemName()))
				{
					return new int[] {i,j};
				}
			}
		}
		
		return new int[0];
	}
	
	private int[] findStackPosition(ItemStack stack)
	{
		for(int i = 0; i < slots.length; i++)
		{
			for(int j = 0; j < slots[i].length; j++)
			{
				ItemStack comp = slots[i][j];
				if(comp != null && stack.equals(comp))
				{
					return new int[] {i,j};
				}
			}
		}
		
		return new int[0];
	}
	
	
	// Find the position of the first item stack which has enough space
	private int[] findItemStackPosition(Item item)
	{
		for(int i = 0; i < slots.length; i++)
		{
			for(int j = 0; j < slots[i].length; j++)
			{
				ItemStack stack = slots[i][j];
				if(stack != null && stack.getItemName().equals(item.getItemName()) && stack.hasSpace())
				{
					return new int[] {i,j};
				}
			}
		}
		
		return new int[0];
	}
	
	private int[] findEmptySlot()
	{
		for(int i = 0; i < slots.length; i++)
		{
			for(int j = 0; j < slots[i].length; j++)
			{
				if(slots[i][j] == null)
				{
					return new int[] {i,j};
				}
			}
		}
		
		return new int[0];
	}
	
	private int[] findNextAvailableSlot(int row,int column)
	{
		// Check forwards for available slot
		for(int i = row; i < slots.length; i++)
		{
			for(int j = column+1; j < slots[i].length; j++)
			{
				if(slots[i][j] == null) return new int[] {i,j};
			}
		}
		
		// Check for first available slot in entire inventory
		return findEmptySlot();
	}
	
	private void swap(ItemStack stack1,ItemStack stack2)
	{
		int[] gridPos1 = findStackPosition(stack1);
		int[] gridPos2 = findStackPosition(stack2);
		Vector2f pos1 = new Vector2f(slotGUIs[gridPos1[0]][gridPos1[1]].getGUITexture().getPosition());
		Vector2f pos2 = new Vector2f(slotGUIs[gridPos2[0]][gridPos2[1]].getGUITexture().getPosition());
		this.slots[gridPos1[0]][gridPos1[1]].getItemIcon().getGUITexture().setPosition(pos2);
		this.slots[gridPos2[0]][gridPos2[1]].getItemIcon().getGUITexture().setPosition(pos1);
		ItemStack temp = this.slots[gridPos1[0]][gridPos1[1]];
		this.slots[gridPos1[0]][gridPos1[1]] = this.slots[gridPos2[0]][gridPos2[1]];
		this.slots[gridPos2[0]][gridPos2[1]] = temp;

	}
	
	private void swapStackText(int[] pos1, int[] pos2)
	{
		GUIText temp = stackTexts[pos1[0]][pos1[1]];
		stackTexts[pos1[0]][pos1[1]] = stackTexts[pos2[0]][pos2[1]];
		stackTexts[pos2[0]][pos2[1]] = temp;
	}

	@Override
	protected void init() {
		
		float fontSize = 0.6f;
		FontStyle font = new FontStyle(loader.loadFontTexture("res/arial.png"),new File("res/arial.fnt"));
		
		int baseTextureId = loader.loadTexture("res/equip_inventory_base.png");
		int slotTextureId = loader.loadTexture("res/equip_inventory_slot.png");
		float baseWidth = (float) INVENTORY_WIDTH / Window.getWidth();
		float baseHeight = (float) INVENTORY_HEIGHT / Window.getHeight();
		float posX = 1 - baseWidth;
		float posY = -1 + baseHeight;
		baseTexture = new GUITexture(baseTextureId,new Vector2f(posX,posY),
				new Vector2f(baseWidth,baseHeight));
		base = new GUI(baseTexture);
		guis.add(base);
		
		// Initialise and arrange slots
		float slotWidth = (float) SLOT_WIDTH / Window.getWidth();
		float slotHeight = (float) SLOT_HEIGHT / Window.getHeight();
		float refX = (baseWidth * 2f) - slotWidth;
		float refY = (baseHeight * 2f) - slotHeight;
		for(int i = 0; i < slots.length; i++)
		{
			
			float y = -1 +  refY - (i * slotHeight * 2f);
			float textY = (1 - baseHeight) + (slotHeight/2.75f) + (i * slotHeight);
			for(int j = 0; j < slots[i].length; j++)
			{
				float x = 1 - refX + (j * slotWidth * 2f);
				float textX = 1 - baseWidth + (slotWidth/8.75f) + (j * slotWidth);
				GUITexture slotTexture = new GUITexture(slotTextureId,new Vector2f(x,y),new Vector2f(slotWidth,slotHeight));
				GUI slotGUI = new GUI(slotTexture);
				slotGUI.setClickable(true);
				GUIText slotText = new GUIText("EMPTY",fontSize,font,new Vector2f(textX,textY),1,false);
				slotText.setColour(1f, 0.84f, 0f);
				GUIText stackText = new GUIText("",fontSize,font,new Vector2f(textX+0.021f,textY+0.035f),1,false);
				stackText.setColour(1f, 0.84f, 0f);
				slotGUIs[i][j] = slotGUI;
				slotTexts[i][j] = slotText;
				stackTexts[i][j] = stackText; 
				guis.add(slotGUI);
				texts.add(slotText);
			}
			
		}
		
		updateText();
	}

	@Override
	public void update() {
		checkKeyState();
		updateGuis();
		if(selectedStack == null) {
			checkStackClicked();
		}
		else
		{
			checkSlotClicked();
		}
		updateSelectedStack();
		
	}
	
	private void checkStackClicked()
	{
		for(int i = 0; i < slots.length; i++)
		{
			for(int j = 0; j < slots[i].length; j++)
			{
				ItemStack stack = slots[i][j];
				if(stack != null && stack.getItemIcon().isClicked())
				{
					selectedStack = stack;
					selectedStackText = stackTexts[i][j];
					stack.getItemIcon().setSelected(true);
				}
			}
		}
	}
	
	private void updateSelectedStack()
	{
		if(selectedStack != null)
		{
			Vector2f cursorPosition = Maths.getNormalizedDeviceCoords(MouseCursor.getXPos(), MouseCursor.getYPos());
			selectedStack.getItemIcon().getGUITexture().setPosition(cursorPosition);
			float x = (cursorPosition.x * 0.5f) + 0.5f;
			float y = (-0.5f * cursorPosition.y) + 0.5f;
			selectedStackText.setPosition(new Vector2f(x-0.003f,y+0.02f));
		}
	}
	
	private void checkSlotClicked()
	{
		for(int i = 0; i < slots.length; i++)
		{
			for(int j = 0; j < slots[i].length; j++)
			{
				GUI slotGui = slotGUIs[i][j];
				if(slotGui.isClicked())
				{
					// Attempt to place the item in the slot
					placeStack(i,j);
				}
			}
		}
	}
	
	private void checkKeyState()
	{
		int currentKeyState = GLFW.glfwGetKey(Window.getWindowID(), SHORT_KEY);
		if(currentKeyState == GLFW.GLFW_PRESS && lastKeyState == GLFW.GLFW_RELEASE)
		{
			this.visible = !this.visible;
			updateText();
		}
		lastKeyState = currentKeyState;
	}
	
	private void updateGuis()
	{
		for(GUI gui: guis)
		{
			gui.setVisible(visible);
		}
	}
	
	private void updateText()
	{
		for(GUIText text: texts)
		{
			if(visible)
			{
				TextController.loadText(text);
			}
			else
			{
				TextController.removeText(text);
			}
		}
		
		if(visible)
		{
			for(int i = 0; i < slotTexts.length; i++)
			{
				for(int j = 0; j < slotTexts[i].length; j++)
				{
					showHideText(slots[i][j] == null,slotTexts[i][j]);
				}
			}
		}

	}
	
	private void showHideText(boolean empty,GUIText text)
	{
		if(empty)
		{
			TextController.loadText(text);
		}
		else
		{
			TextController.removeText(text);
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean containsItem(Item item)
	{
		int[] pos = findItemPosition(item);
		return pos.length > 0;
	}
	
	public ItemStack getItemStack(int row, int column)
	{
		return this.slots[row][column];
	}
	
	public GUI getSlotGUI(int row, int column)
	{
		return this.slotGUIs[row][column];
	}
	
	public GUIText getSlotText(int row, int column)
	{
		return this.slotTexts[row][column];
	}

	public int getCapacity() {
		return capacity;
	}
	
	public void increaseCapacity(int size)
	{
		this.capacity += size;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public GUI getBase() {
		return base;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

	public List<GUI> getGuis() {
		return guis;
	}

	public ItemStack[][] getSlots() {
		return slots;
	}
	
	
	
	
	
	
	
	

}
