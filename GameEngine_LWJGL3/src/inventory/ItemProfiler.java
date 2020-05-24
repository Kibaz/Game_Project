package inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import components.Component;
import equip.EquipItem;
import equip.EquipSlot;
import equip.ItemStat;
import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUI;
import guis.GUITexture;
import rendering.Loader;

public class ItemProfiler {
	
	// Constants
	private static final float TITLE_FONT_SIZE = 0.5f;
	private static final float STAT_FONT_SIZE = 0.4f;
	private static final float TITLE_LINE_OFFSET = 0.025f;
	private static final float LINE_OFFSET = 0.02f;
	
	private static final float TEXT_MARGIN_X = 0.005f;
	private static final float TEXT_MARGIN_Y = 0.005f;
	
	// UI Components
	private static GUITexture profileTexture;
	private static GUI profile;
	
	private static GUIText title;
	
	private static List<GUIText> texts;
	private static FontStyle font;
	
	private static GUI itemGUI = null;
	
	public static void init(Loader loader,List<GUI> guis)
	{
		texts = new ArrayList<>();
		profileTexture = new GUITexture(loader.loadTexture("res/ent_profile.png"),new Vector2f(0,0),new Vector2f(0.1f,0.1f));
		profile = new GUI(profileTexture);
		profile.setSelected(true); // So that it renders on top
		font = new FontStyle(loader.loadFontTexture("res/arial.png"),new File("res/arial.fnt"));
		title = new GUIText("",TITLE_FONT_SIZE,font,new Vector2f(0,0),1,false);
		title.setColour(1, 0.84f, 0);
		guis.add(profile);
		destroy(); // Hide and remove text
	}
	
	public static void build(Item item, Vector2f itemPosition, Vector2f itemScale)
	{
		destroy();
		Vector2f profilePosition = calculateProfilePosition(itemPosition,itemScale);
		profile.getGUITexture().setPosition(profilePosition);
		profile.setVisible(true);
		title.setContent(item.getItemName());
		TextController.loadText(title);
		Vector2f textPosition = calculateTextPosition(profilePosition);
		textPosition.y += TEXT_MARGIN_Y;
		title.setPosition(textPosition);
		float yOffset = TITLE_LINE_OFFSET;
		// Check if item is equippable
		if(item instanceof EquipItem)
		{
			String slot = equipSlotToString(((EquipItem) item).getEquipSlot());
			GUIText slotText = new GUIText(slot,STAT_FONT_SIZE,font,textPosition,1,false);
			slotText.setColour(0.52f, 0f, 0.52f);
			textPosition = new Vector2f(textPosition.x,textPosition.y + yOffset);
			slotText.setPosition(textPosition);
			texts.add(slotText);
			yOffset = LINE_OFFSET;
		}
		
		for(ItemStat stat: item.getStats())
		{
			// Create a GUIText for each stat
			textPosition = new Vector2f(textPosition.x,textPosition.y+yOffset);
			GUIText text = new GUIText(stat.display(),STAT_FONT_SIZE,font,textPosition,1,false);
			text.setColour(1, 1, 1);
			texts.add(text); // Add to stat lines for profile rendering
			yOffset = LINE_OFFSET;
		}
	}
	
	private static String equipSlotToString(EquipSlot slot)
	{
		String slotAsString = slot.toString();
		slotAsString = slotAsString.toLowerCase(); // Convert all to lower case
		String[] words = slotAsString.split("_"); // Split multiple words
		
		String result = "";
		for(int i = 0; i < words.length; i++)
		{
			if(i > 0) result += " ";
			result += words[i].substring(0,1).toUpperCase() + words[i].substring(1);
		}
		
		return result;
	}
	
	public static void update()
	{
		if(itemGUI == null)
		{
			destroy();
		}
		else
		{
			if(!(itemGUI.isHovered() && itemGUI.isVisible() && !itemGUI.isSelected()))
			{
				destroy();
			}
		}
	}
	
	private static Vector2f calculateProfilePosition(Vector2f itemPosition,Vector2f itemScale) {
		float offsetX = profileTexture.getScale().x + itemScale.x;
		float offsetY = profileTexture.getScale().y - itemScale.y;
		return new Vector2f(itemPosition.x - offsetX, itemPosition.y - offsetY);
	}
	
	private static Vector2f calculateTextPosition(Vector2f profilePosition)
	{
		float x = (profilePosition.x * 0.5f) + 0.5f - (profile.getGUITexture().getScale().x / 2f);
		x += TEXT_MARGIN_X;
		float y = (profilePosition.y * -0.5f) + 0.5f - (profile.getGUITexture().getScale().y / 2f);
		return new Vector2f(x,y);
	}

	public static void destroy()
	{
		TextController.removeText(title);
		
		for(GUIText text: texts)
		{
			TextController.removeText(text);
		}
		
		profile.setVisible(false);
		texts = new ArrayList<>();
	}
	
	public static void setProfiledItem(GUI gui)
	{
		itemGUI = gui;
	}
}
