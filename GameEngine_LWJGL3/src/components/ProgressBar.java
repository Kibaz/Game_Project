package components;

import java.io.File;

import org.lwjgl.util.vector.Vector2f;

import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUI;
import guis.GUITexture;

public class ProgressBar extends Component {
	
	private int capacity;
	private int progress;
	
	private float maxScale;
	
	private Vector2f uiPosition;
	private Vector2f textPosition;
	
	private GUITexture frameTexture;
	private GUITexture poolTexture;
	
	private GUI frame;
	private GUI pool;
	
	private GUIText information;
	
	public ProgressBar(String name, int capacity, int progress, Vector2f uiPosition, Vector2f textPosition)
	{
		super(name);
		this.capacity = capacity;
		this.progress = progress;
		this.uiPosition = uiPosition;
		this.textPosition = textPosition;
		init();
	}

	@Override
	protected void init() {
		frameTexture = new GUITexture(loader.loadTexture("res/basic_health_bar_frame.png"), new Vector2f(uiPosition),new Vector2f(1,0.3f));
		poolTexture = new GUITexture(loader.loadTexture("res/basic_exp_pool.png"),new Vector2f(uiPosition),new Vector2f(1,0.3f));
		maxScale = poolTexture.getScale().x;
		frame = new GUI(frameTexture);
		pool = new GUI(poolTexture);
		FontStyle font = new FontStyle(loader.loadFontTexture("res/arial.png"),new File("res/arial.fnt"));
		information = new GUIText(progress + "/" + capacity,0.6f, font, textPosition,1,true);
		information.setColour(1, 1, 1);
	}

	@Override
	public void update() {
		information.setContent(progress + "/" + capacity);
		float scaleFactor = progress / (float) capacity;
		float prevScale = poolTexture.getScale().x;
		poolTexture.getScale().x = scaleFactor * maxScale;
		poolTexture.getPosition().x -= (prevScale - poolTexture.getScale().x) * 0.725f;
		//TextController.removeText(information);
		TextController.loadText(information);
	}

	@Override
	public void start() {
		
	}

	@Override
	public void cleanUp() {
		
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public GUI getFrame() {
		return frame;
	}

	public GUI getPool() {
		return pool;
	}
	
	

}
