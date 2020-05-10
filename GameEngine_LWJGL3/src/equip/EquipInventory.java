package equip;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import components.Component;
import components.EntityInformation;
import entities.Entity;
import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUI;
import guis.GUITexture;
import rendering.EntityRenderer;
import rendering.Window;

public class EquipInventory extends Component {
	
	// Constants
	private final int SHORT_KEY = GLFW.GLFW_KEY_I;
	private final int INVENTORY_WIDTH = 1000;
	private final int INVENTORY_HEIGHT = 800;

	private final int SLOT_WIDTH = 170;
	private final int SLOT_HEIGHT = 170;
	
	/*
	 * Store UI for equipment inventory
	 * e.g. Helmet, Chest-piece, Legs,
	 * Boots, Weapons, Shields / Off-hand
	 * etc...
	 */
	
	// GUI Textures -----
	private GUITexture inventoryTexture;
	private GUITexture headSlotTexture;
	private GUITexture shoulderSlotTexture;
	private GUITexture chestSlotTexture;
	private GUITexture neckSlotTexture;
	private GUITexture legsSlotTexture;
	private GUITexture handsSlotTexture;
	private GUITexture ringSlotTexture;
	private GUITexture mainHandSlotTexture;
	private GUITexture offHandSlotTexture;
	private GUITexture rangedSlotTexture;
	private GUITexture feetSlotTexture;
	
	// GUIs -----
	private GUI inventoryGUI;
	private GUI headSlot;
	private GUI shoulderSlot;
	private GUI chestSlot;
	private GUI neckSlot;
	private GUI legsSlot;
	private GUI handsSlot;
	private GUI ringSlot;
	private GUI mainHandSlot;
	private GUI offHandSlot;
	private GUI rangedSlot;
	private GUI feetSlot;
	
	// Fonts
	private FontStyle font;
	
	// Text
	private GUIText mainText;
	private GUIText headText;
	private GUIText shoulderText;
	private GUIText chestText;
	private GUIText neckText;
	private GUIText legsText;
	private GUIText handsText;
	private GUIText ringText;
	private GUIText mainHandText;
	private GUIText offHandText;
	private GUIText rangedText;
	private GUIText feetText;
	
	// Buffers to add to texture
	private EntityFBO fbo;
	
	private GUITexture entityDisplayTexture;
	private GUI entityDisplay;
	
	/*
	 * Store available slots as key values
	 * and an entity with "EquipItem" component
	 * as the value for the key-value pair
	 */
	
	private Map<EquipSlot,Entity> inventory;
	
	private boolean visible; // Control view of interface objects
	
	// Track key press on short-key
	private int lastKeyState = GLFW.GLFW_RELEASE; // Default released
	
	private List<GUI> guis;
	private List<GUIText> texts;
	
	private boolean renderUI;
	
	public EquipInventory(boolean renderUI)
	{
		super("equipInventory");
		this.inventory = new EnumMap<>(EquipSlot.class);
		this.guis = new ArrayList<>();
		this.texts = new ArrayList<>();
		this.renderUI = renderUI;
		if(this.renderUI)
		{
			fbo = new EntityFBO();
		}
		this.init();
	}
	
	public void equip(Entity entity)
	{
		if(!entity.hasComponent(EquipItem.class))
		{
			// Throw cannot equip error
			return;
		}
		entity.setClickable(false); // Can't interact with entity if equipped
		entity.setHovered(false);
		EquipItem equipItem = entity.getComponentByType(EquipItem.class);
		Entity currentItem = inventory.get(equipItem.getEquipSlot());
		if(currentItem != null)
		{
			unequip(currentItem);
		}
		List<ItemStat> modifiers = equipItem.getStats();
		for(ItemStat modifier: modifiers)
		{
			if(this.entity.hasComponent(EntityInformation.class))
			{
				modifier.apply(this.entity.getComponentByType(EntityInformation.class));
			}
			
		}
		inventory.put(equipItem.getEquipSlot(), entity);
		equipItem.setParent(this.entity);
		if(renderUI) 
		{
			updateText();
			// Determine position based on equip slot
			equipItem.setIconPosition(findIconPosition(equipItem.getEquipSlot()));
			equipItem.setIconScale(new Vector2f((float)SLOT_WIDTH/Window.getWidth(),
					(float)SLOT_HEIGHT/Window.getHeight()));
			Vector2f itemPosition = equipItem.getIcon().getGUITexture().getPosition();
			equipItem.setDurabilityPosition(new Vector2f(itemPosition.x,itemPosition.y - 0.18f));
		}
	}
	
	private Vector2f findIconPosition(EquipSlot slot)
	{
		switch(slot)
		{
		case HEAD: return headSlot.getGUITexture().getPosition();
		case CHEST: return chestSlot.getGUITexture().getPosition();
		case SHOULDERS: return shoulderSlot.getGUITexture().getPosition();
		case NECK: return neckSlot.getGUITexture().getPosition(); 
		case LEGS: return legsSlot.getGUITexture().getPosition(); 
		case HANDS: return handsSlot.getGUITexture().getPosition(); 
		case MAIN_HAND: return mainHandSlot.getGUITexture().getPosition(); 
		case OFF_HAND: return offHandSlot.getGUITexture().getPosition(); 
		case RING: return ringSlot.getGUITexture().getPosition(); 
		case RANGED: return rangedSlot.getGUITexture().getPosition();
		case FEET: return feetSlot.getGUITexture().getPosition();
		default: return new Vector2f(0,0);
		}
	}
	
	public void unequip(Entity entity)
	{
		// Remove item modifiers
		EquipItem equipItem = entity.getComponentByType(EquipItem.class);
		entity.setClickable(true); // Interactive when unequipped
		List<ItemStat> modifiers = equipItem.getStats();
		for(ItemStat modifier: modifiers)
		{
			if(this.entity.hasComponent(EntityInformation.class))
			{
				modifier.remove(this.entity.getComponentByType(EntityInformation.class));
			}
			
		}
		// Remove from inventory
		inventory.remove(equipItem.getEquipSlot(), entity);
		equipItem.setParent(null);
		if(renderUI) {
			updateText();
			// Hide item that has been un-equipped
			equipItem.getIcon().setVisible(false);
			equipItem.getDurabilityIndicator().setVisible(false);
		}
	}
	
	public boolean isEquipped(Entity item) {
		EquipItem equip = item.getComponentByType(EquipItem.class);
		return (inventory.get(equip.getEquipSlot()) != null && inventory.get(equip.getEquipSlot()).equals(item));
	}
	
	public Entity getItemBySlot(EquipSlot slot)
	{
		return inventory.get(slot);
	}

	@Override
	protected void init() {
		this.visible = false;
		
		if(!renderUI) return;
		// Init font
		float fontSize = 0.6f;
		font = new FontStyle(loader.loadFontTexture("res/arial.png"),new File("res/arial.fnt"));
		
		// Init text
		headText = new GUIText("HEAD",fontSize,font,new Vector2f(0f,0.15f),1,true);
		headText.setColour(1f, 0.84f, 0);
		texts.add(headText);
		
		chestText = new GUIText("CHEST",fontSize,font,new Vector2f(0f,0.39f),1,true);
		chestText.setColour(1f, 0.84f, 0);
		texts.add(chestText);
		
		legsText = new GUIText("LEGS",fontSize,font,new Vector2f(0f,0.58f),1,true);
		legsText.setColour(1f, 0.84f, 0);
		texts.add(legsText);
		
		handsText = new GUIText("HANDS",fontSize,font,new Vector2f(-0.113f,0.355f),1,true);
		handsText.setColour(1f, 0.84f, 0);
		texts.add(handsText);
		
		shoulderText = new GUIText("SHOULDERS",fontSize,font,new Vector2f(-0.172f,0.165f),1,true);
		shoulderText.setColour(1f, 0.84f, 0);
		texts.add(shoulderText);
		
		neckText = new GUIText("NECK",fontSize,font,new Vector2f(0.166f,0.295f),1,true);
		neckText.setColour(1f, 0.84f, 0);
		texts.add(neckText);
		
		ringText = new GUIText("RING",fontSize,font,new Vector2f(-0.232f,0.355f),1,true);
		ringText.setColour(1f, 0.84f, 0);
		texts.add(ringText);
		
		offHandText = new GUIText("OFF HAND",fontSize,font,new Vector2f(0.226f,0.485f),1,true);
		offHandText.setColour(1f, 0.84f, 0);
		texts.add(offHandText);
		
		rangedText = new GUIText("RANGED",fontSize,font,new Vector2f(0.166f,0.675f),1,true);
		rangedText.setColour(1f, 0.84f, 0);
		texts.add(rangedText);
		
		mainHandText = new GUIText("MAIN HAND",fontSize,font,new Vector2f(0.107f,0.485f),1,true);
		mainHandText.setColour(1f, 0.84f, 0);
		texts.add(mainHandText);
		
		feetText = new GUIText("FEET",fontSize,font,new Vector2f(0f,0.815f),1,true);
		feetText.setColour(1f, 0.84f, 0);
		texts.add(feetText);
		
		updateText(); // Hide
		
		// Init textures
		// Calculate aspect ratio width/height of base UI
		float inventoryWidth = (float) INVENTORY_WIDTH / Window.getWidth();
		float inventoryHeight = (float) INVENTORY_HEIGHT / Window.getHeight();
		float slotWidth = (float) SLOT_WIDTH / Window.getWidth();
		float slotHeight = (float) SLOT_HEIGHT / Window.getHeight(); 
		int inventoryTextureId = loader.loadTexture("res/equip_inventory_base.png");
		int slotTextureId = loader.loadTexture("res/equip_inventory_slot.png");
		inventoryTexture = new GUITexture(inventoryTextureId,
				new Vector2f(0,0),new Vector2f(inventoryWidth,inventoryHeight));
		
		headSlotTexture = new GUITexture(slotTextureId,
				new Vector2f(0,inventoryHeight-slotHeight-(34f/Window.getHeight())),new Vector2f(slotWidth,slotHeight));
		
		chestSlotTexture = new GUITexture(slotTextureId,
				new Vector2f(0,(float)SLOT_HEIGHT/Window.getHeight()),new Vector2f(slotWidth,slotHeight));
		
		legsSlotTexture = new GUITexture(slotTextureId,
				new Vector2f(0,(float)-SLOT_HEIGHT/Window.getHeight()),new Vector2f(slotWidth,slotHeight));
		
		feetSlotTexture = new GUITexture(slotTextureId,
				new Vector2f(0,-inventoryHeight+slotHeight+(34f/Window.getHeight())),new Vector2f(slotWidth,slotHeight));
		
		mainHandSlotTexture = new GUITexture(slotTextureId,
				new Vector2f((float)SLOT_WIDTH*2/Window.getWidth(),0f)
				,new Vector2f(slotWidth,slotHeight));
		
		offHandSlotTexture = new GUITexture(slotTextureId,
				new Vector2f((float)((SLOT_WIDTH*4)+40f)/Window.getWidth(),0f)
				,new Vector2f(slotWidth,slotHeight));
		
		handsSlotTexture = new GUITexture(slotTextureId,
				new Vector2f((float)-((SLOT_WIDTH*2)+20)/Window.getWidth(),(float)(SLOT_HEIGHT+70)/Window.getHeight())
				,new Vector2f(slotWidth,slotHeight));
		
		ringSlotTexture = new GUITexture(slotTextureId,
				new Vector2f((float)-((SLOT_WIDTH*4)+60f)/Window.getWidth(),(float)(SLOT_HEIGHT+70)/Window.getHeight())
				,new Vector2f(slotWidth,slotHeight));
		
		rangedSlotTexture = new GUITexture(slotTextureId,
				new Vector2f((float)((SLOT_WIDTH*3)+20f)/Window.getWidth(),(float)-(SLOT_HEIGHT*2)/Window.getHeight())
				,new Vector2f(slotWidth,slotHeight));
		
		neckSlotTexture = new GUITexture(slotTextureId,
				new Vector2f((float)((SLOT_WIDTH*3)+20f)/Window.getWidth(),(float)(SLOT_HEIGHT*2)/Window.getHeight())
				,new Vector2f(slotWidth,slotHeight));
		
		shoulderSlotTexture = new GUITexture(slotTextureId,
				new Vector2f((float)-((SLOT_WIDTH*3)+40f)/Window.getWidth(),(float)((SLOT_HEIGHT*3)+70)/Window.getHeight())
				,new Vector2f(slotWidth,slotHeight));
		
		// Init GUIs
		inventoryGUI = new GUI(inventoryTexture);
		guis.add(inventoryGUI);
		headSlot = new GUI(headSlotTexture);
		guis.add(headSlot);
		legsSlot = new GUI(legsSlotTexture);
		guis.add(legsSlot);
		chestSlot = new GUI(chestSlotTexture);
		guis.add(chestSlot);
		feetSlot = new GUI(feetSlotTexture);
		guis.add(feetSlot);
		handsSlot = new GUI(handsSlotTexture);
		guis.add(handsSlot);
		ringSlot = new GUI(ringSlotTexture);
		guis.add(ringSlot);
		mainHandSlot = new GUI(mainHandSlotTexture);
		guis.add(mainHandSlot);
		offHandSlot = new GUI(offHandSlotTexture);
		guis.add(offHandSlot);	
		rangedSlot = new GUI(rangedSlotTexture);
		guis.add(rangedSlot);
		neckSlot = new GUI(neckSlotTexture);
		guis.add(neckSlot);
		shoulderSlot = new GUI(shoulderSlotTexture);
		guis.add(shoulderSlot);
		
		// Init entity display GUI
		entityDisplayTexture = new GUITexture(this.fbo.getTexture(),
				new Vector2f((float)-((SLOT_WIDTH*3)+40f)/Window.getWidth(),(float)-((SLOT_HEIGHT*2)+15f)/Window.getHeight()),
				new Vector2f(300f/Window.getWidth(),400f/Window.getHeight()));
		entityDisplay = new GUI(entityDisplayTexture);
		entityDisplay.setFbo(true);
		guis.add(entityDisplay);
	}

	@Override
	public void update() {
		checkKeyState();
		updateGuis();
	}
	
	private void checkKeyState()
	{
		if(!renderUI) return;
		int currentKeyState = GLFW.glfwGetKey(Window.getWindowID(), SHORT_KEY);
		if(currentKeyState == GLFW.GLFW_PRESS && lastKeyState == GLFW.GLFW_RELEASE)
		{
			this.visible = !this.visible;
			// Load texts once
			updateText();
		}
		lastKeyState = currentKeyState;
	}
	
	private void updateGuis()
	{
		if(!renderUI) return;
		for(GUI gui: guis)
		{
			gui.setVisible(visible);
		}
		
		for(Entry<EquipSlot,Entity> entry: inventory.entrySet())
		{
			Entity item = entry.getValue();
			if(item != null && !this.visible)
			{
				item.getComponentByType(EquipItem.class).getIcon().setVisible(false);
				item.getComponentByType(EquipItem.class).getDurabilityIndicator().setVisible(false);
			}
			else if(item != null)
			{
				// Equipped, show item icon
				item.getComponentByType(EquipItem.class).getIcon().setVisible(true);
				item.getComponentByType(EquipItem.class).getDurabilityIndicator().setVisible(true);
			}
		}
	}
	
	private void updateText()
	{
		if(!renderUI) return;

		
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
		
		for(Entry<EquipSlot,Entity> entry: inventory.entrySet())
		{
			EquipSlot slot = entry.getKey();
			boolean empty = entry.getValue() == null;
			switch(slot)
			{
			case HEAD: showHideText(empty,headText); break;
			case CHEST: showHideText(empty,chestText); break;
			case SHOULDERS: showHideText(empty,shoulderText); break;
			case NECK: showHideText(empty,neckText); break;
			case HANDS: showHideText(empty,handsText); break;
			case LEGS: showHideText(empty,legsText); break;
			case FEET: showHideText(empty,feetText); break;
			case OFF_HAND: showHideText(empty,offHandText); break;
			case MAIN_HAND: showHideText(empty,mainHandText); break;
			case RANGED: showHideText(empty,rangedText); break;
			case RING: showHideText(empty,ringText); break;
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

	public List<GUI> getGuis() {
		return guis;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isRenderUI() {
		return renderUI;
	}

	public void setRenderUI(boolean renderUI) {
		this.renderUI = renderUI;
	}
	
	public EntityFBO getFBO()
	{
		return fbo;
	}

	public Map<EquipSlot, Entity> getInventory() {
		return inventory;
	}
	
	
	
	
	
	
	
	

}
