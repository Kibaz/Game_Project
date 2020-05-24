package components;

import java.io.File;

import org.lwjgl.util.vector.Vector2f;

import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUI;
import guis.GUITexture;
import inputs.MouseCursor;
import rendering.Window;

public class EntityProfile extends Component {
	
	/*
	 * This component will extract the information from
	 * other components and display them in a GUI on the screen
	 * when the entity is hovered over with the mouse 
	 */
	
	private GUITexture profileDisplayTexture;
	private GUI profileDisplay;
	
	private GUIText levelDisplay;
	private GUIText healthDisplay;
	private GUIText titleDisplay;
	
	public EntityProfile()
	{
		super("entity_profile");
	}

	@Override
	public void init() {
		profileDisplayTexture = new GUITexture(loader.loadTexture("res/ent_profile.png"),new Vector2f(0,0),new Vector2f(0.1f,0.1f));
		profileDisplay = new GUI(profileDisplayTexture);
		float fontSize = 0.45f;
		FontStyle font = new FontStyle(loader.loadFontTexture("res/arial.png"),new File("res/arial.fnt"));
		levelDisplay = new GUIText("test",fontSize,font,new Vector2f(0,0),1,false);
		levelDisplay.setColour(1, 1, 1);
		healthDisplay = new GUIText("test",fontSize,font,new Vector2f(0,0),1,false);
		healthDisplay.setColour(1, 1, 1);
		titleDisplay = new GUIText("test",fontSize,font,new Vector2f(0,0),1,false);
		titleDisplay.setColour(1, 0.84f, 0);
		profileDisplay.setVisible(false);
	}

	@Override
	public void update() {
		if(entity != null)
		{
			if(entity.isHovered())
			{
				profileDisplay.setVisible(true);
				TextController.loadText(titleDisplay);
				TextController.loadText(healthDisplay);
				TextController.loadText(levelDisplay);
			}
			else
			{
				profileDisplay.setVisible(false);
				TextController.removeText(titleDisplay);
				TextController.removeText(healthDisplay);
				TextController.removeText(levelDisplay);
			}
			
			EntityInformation info = entity.getComponentByType(EntityInformation.class);
			if(info != null)
			{
				titleDisplay.setContent(info.getTitle());
				levelDisplay.setContent("Level: " + info.getLevel());
				String healthInfo = (info.getHealth() > 0) ? "Health: " + info.getHealth() + " / " + info.getMaxHealth() : "DEAD";
				healthDisplay.setContent(healthInfo);
			}
		}
		
		Vector2f guiPosition = calculateGUIPosition(MouseCursor.getXPos(),MouseCursor.getYPos());
		Vector2f textPosition = calculateTextPosition(MouseCursor.getXPos(),MouseCursor.getYPos());
		profileDisplayTexture.setPosition(guiPosition);
		titleDisplay.setPosition(new Vector2f(textPosition.x,textPosition.y));
		levelDisplay.setPosition(new Vector2f(textPosition.x,textPosition.y + 0.025f));
		healthDisplay.setPosition(new Vector2f(textPosition.x,textPosition.y + 0.05f));
	}

	@Override
	public void start() {
		
	}

	@Override
	public void cleanUp() {
		
	}
	
	private Vector2f calculateGUIPosition(float mouseX, float mouseY)
	{
		/* Normalise mouse cursor coordinates */
		float x = (2f*mouseX) / Window.getWidth() - 1f;
		float y = (2f*mouseY) / Window.getHeight() - 1f;
		
		/* Calculate offset */
		
		float offsetX = profileDisplayTexture.getScale().x;
		float offsetY = profileDisplayTexture.getScale().y;
		
		
		return new Vector2f(x + offsetX + 0.01f,-y - offsetY - 0.01f);
	}
	
	private Vector2f calculateTextPosition(float mouseX, float mouseY)
	{
		float x = mouseX / Window.getWidth();
		float y = mouseY / Window.getHeight();
		
		return new Vector2f(x + 0.01f,y + 0.01f);
	}

	public GUI getProfileDisplay() {
		return profileDisplay;
	}

	public GUIText getLevelDisplay() {
		return levelDisplay;
	}

	public GUIText getHealthDisplay() {
		return healthDisplay;
	}

	public GUIText getTitleDisplay() {
		return titleDisplay;
	}
	
	

}
