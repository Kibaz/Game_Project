package guis;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import fontUtils.FontStyle;
import fontUtils.GUIText;
import rendering.Loader;

public abstract class UIFrame {
	
	private List<GUI> guis;
	private List<GUIText> textGuis;
	
	private Loader loader;
	
	public UIFrame(Loader loader)
	{
		this.loader = loader;
		guis = new ArrayList<>();
		textGuis = new ArrayList<>();
	}
	
	protected GUI createGUI(String texturePath, Vector2f position, Vector2f scale)
	{	
		GUITexture guiTexture = new GUITexture(loader.loadTexture(texturePath),position,scale);
		GUI gui = new GUI(guiTexture);
		addGUI(gui);
		return gui;
	}
	
	public abstract void updateFrame();
	
	protected GUIText createTextDisplay(String text, float fontSize, FontStyle font, Vector2f position, float maxLineSize, boolean centered)
	{	
		GUIText textDisplay = new GUIText(text,fontSize,font,position,maxLineSize,centered);
		addTextDisplay(textDisplay);
		return textDisplay;
	}
	
	private void addGUI(GUI gui)
	{
		guis.add(gui);
	}
	
	private void addTextDisplay(GUIText text)
	{
		textGuis.add(text);
	}

	public List<GUI> getGuis() {
		return guis;
	}

	public List<GUIText> getTextGuis() {
		return textGuis;
	}
	
	

}
