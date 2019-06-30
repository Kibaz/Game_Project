package components;

import java.io.File;

import org.lwjgl.util.vector.Vector2f;

import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUI;
import guis.GUITexture;

public class HealthBarFrame extends Component{
	
	private float health;
	private float maxHealth;
	private int level;
	
	private float maxScale;
	
	private float fontSize;
	
	private Vector2f uiPosition;
	private Vector2f levelPosition;
	private Vector2f healthPosition;
	
	private GUITexture healthPoolTexture;
	private GUITexture healthFrameTexture;
	
	private FontStyle generalFont;
	
	private GUIText levelInfo;
	private GUIText healthInfo;
	
	private GUI healthPool;
	private GUI healthFrame;
	
	private boolean visible;
	
	public HealthBarFrame(String name, Vector2f uiPosition, Vector2f levelPosition, Vector2f healthPosition)
	{
		super(name);
		this.uiPosition = uiPosition;
		this.levelPosition = levelPosition;
		this.healthPosition = healthPosition;
		init();
	}

	@Override
	protected void init() {
		visible = false;
		fontSize = 0.6f;
		healthFrameTexture = new GUITexture(loader.loadTexture("res/basic_health_bar_frame.png"),new Vector2f(uiPosition),new Vector2f(0.2f,0.3f));
		healthPoolTexture = new GUITexture(loader.loadTexture("res/basic_health_pool.png"), new Vector2f(uiPosition), new Vector2f(0.2f,0.3f));
		maxScale = healthFrameTexture.getScale().x;
		healthPool = new GUI(healthPoolTexture);
		healthFrame = new GUI(healthFrameTexture);
		generalFont = new FontStyle(loader.loadFontTexture("res/arial.png"),new File("res/arial.fnt"));
		levelInfo = new GUIText("Lv: " + level, fontSize, generalFont,levelPosition,1,false);
		levelInfo.setColour(1,1,1);
		healthInfo = new GUIText(health + "/" + maxHealth, fontSize, generalFont, healthPosition,1,true);
		healthInfo.setColour(1, 1, 1);
		TextController.removeText(levelInfo);
		TextController.removeText(healthInfo);
	}

	@Override
	public void update() {
		if(entity != null)
		{
			EntityInformation entityInfo = entity.getComponentByType(EntityInformation.class);
			if(entityInfo != null)
			{
				levelInfo.setContent("Lv: " + entityInfo.getLevel());
				String healthTextInfo = (entityInfo.getHealth() > 0) ? entityInfo.getHealth() + " / " + entityInfo.getMaxHealth() : "DEAD";
				healthInfo.setContent(healthTextInfo);
				// Calculate current fraction of health
				float scaleFactor = entityInfo.getHealth() / (float) entityInfo.getMaxHealth();
				// Scale health pool by health factor
				float prevScale = healthPoolTexture.getScale().x;
				healthPoolTexture.getScale().x = scaleFactor * maxScale;
				// Translate health pool gui to compensate for origin scaling
				healthPoolTexture.getPosition().x -= (prevScale - healthPoolTexture.getScale().x) * 0.725f;
			}
		}
		
		if(visible)
		{
			TextController.removeText(healthInfo);
			TextController.removeText(levelInfo);
			TextController.loadText(healthInfo);
			TextController.loadText(levelInfo);
			healthPool.setVisible(true);
			healthFrame.setVisible(true);
		}
		else
		{
			TextController.removeText(healthInfo);
			TextController.removeText(levelInfo);
			healthPool.setVisible(false);
			healthFrame.setVisible(false);
		}
	}

	@Override
	public void start() {
		
	}

	@Override
	public void cleanUp() {
		loader.cleanUp();
	}

	public float getHealth() {
		return health;
	}
	
	public float getMaxHealth()
	{
		return maxHealth;
	}
	
	public void setMaxHealth(float maxHealth)
	{
		this.maxHealth = maxHealth;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public GUI getHealthPool() {
		return healthPool;
	}

	public GUI getHealthFrame() {
		return healthFrame;
	}

	public GUIText getLevelInfo() {
		return levelInfo;
	}

	public GUIText getHealthInfo() {
		return healthInfo;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	
	
	
	
	
	

}
