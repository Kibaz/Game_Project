package guis;

import java.io.File;

import org.lwjgl.util.vector.Vector2f;

import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import rendering.Loader;

public class CharacterUIFrame extends UIFrame {
	
	private final float MAX_HEALTH;
	
	private FontStyle generalFont;
	
	private GUI healthBarFrame;
	private GUI healthPool;
	
	private GUIText levelInfo;
	private GUIText healthInfo;
	
	private int level;
	private float health;
	
	private Vector2f framePosition;
	private Vector2f frameScale;

	public CharacterUIFrame(Loader loader, int level, float health, float fontSize, 
			Vector2f framePosition, Vector2f frameScale) {
		super(loader);
		this.MAX_HEALTH = health;
		this.level = level;
		this.health = health;
		this.framePosition = framePosition;
		this.frameScale = frameScale;
		generalFont = new FontStyle(loader.loadFontTexture("res/arial.png"),new File("res/arial.fnt"));
		healthBarFrame = super.createGUI("res/basic_health_bar_frame.png", new Vector2f(framePosition), new Vector2f(frameScale));
		healthPool = super.createGUI("res/basic_health_pool.png", new Vector2f(framePosition), new Vector2f(frameScale));
		levelInfo = super.createTextDisplay("Lv: " + level, fontSize, generalFont, new Vector2f(0.24f,0.035f), 1, false);
		levelInfo.setColour(1, 1, 1);
		healthInfo = super.createTextDisplay(Float.toString(health) + " / " + Float.toString(MAX_HEALTH), fontSize, generalFont, new Vector2f(-0.1475f,0.035f), 1, true);
		healthInfo.setColour(1, 1, 1);
		TextController.removeText(levelInfo);
		TextController.removeText(healthInfo);
	}

	@Override
	public void updateFrame() {
		levelInfo.setContent("Lv: " + level);
		String healthTextInfo = (health > 0) ? Float.toString(health) + " / " + Float.toString(MAX_HEALTH) : "DEAD"; 
		healthInfo.setContent(healthTextInfo);
		float scaleFactor = health / MAX_HEALTH;
		// Scale health pool
		float prevScale = healthPool.getGUITexture().getScale().x;
		healthPool.getGUITexture().getScale().x = scaleFactor * frameScale.x;
		
		// Translate to compensate for origin scaling
		healthPool.getGUITexture().getPosition().x -= (prevScale - healthPool.getGUITexture().getScale().x) * 0.725f;
		
		//TextController.removeText(levelInfo);
		//TextController.removeText(healthInfo);
		//TextController.loadText(levelInfo);
		//TextController.loadText(healthInfo);
	}
	
	public void setFramePosition(Vector2f framePosition)
	{
		this.framePosition = framePosition;
	}
	
	public void setHealthInfoPosition(Vector2f position)
	{
		this.healthInfo.setPosition(position);
	}
	
	public void setLevelInfoPosition(Vector2f position)
	{
		this.levelInfo.setPosition(position);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}
	
	

}
