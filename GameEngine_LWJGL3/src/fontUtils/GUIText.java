package fontUtils;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import fontRendering.TextController;

public class GUIText {
	
	private String content;
	private float fontSize;
	
	private int meshVAO;
	private int vertCount;
	private Vector3f colour = new Vector3f(0,0,0);
	
	private Vector2f position; // Relative to the position on the screen as opposed to the 3D world
	private Vector3f worldPosition; // Relative to the world
	private float maxLineSize;
	private int numLines;
	
	private FontStyle font;
	
	private boolean centered = false;
	
	private boolean isFloating = false;
	
	private float opacity;
	
	private Entity assocEntity;
	
	public GUIText(String text, float fontSize, FontStyle font, Vector2f position, float maxLineSize, boolean centered)
	{
		this.content = text;
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		this.maxLineSize = maxLineSize;
		this.centered = centered;
		this.opacity = 1;
		// load text
		TextController.loadText(this);
	}
	
	// For creating text which will float within world space (3D Space)
	public GUIText(String text, float fontSize, FontStyle font, Vector3f worldPosition,float maxLineSize,boolean centered)
	{
		this.content = text;
		this.fontSize = fontSize;
		this.font = font;
		this.maxLineSize = maxLineSize;
		this.worldPosition = worldPosition;
		this.centered = centered;
		this.isFloating = true;
		this.opacity = 1;
		TextController.loadText(this);
	}
	
	public void remove()
	{
		// remove text
		TextController.removeText(this);
	}
	
	public void animate()
	{
		this.opacity -= 0.015;
		if(assocEntity != null)
		{
			this.worldPosition = new Vector3f(assocEntity.getPosition().x,
					this.getWorldPosition().y + 0.1f,assocEntity.getPosition().z);
		}
		
		if(this.opacity < 0)
		{
			// Mark this text to be removed
			TextController.addToRemovalQueue(this);
		}
	}
	
	public FontStyle getFont()
	{
		return font;
	}
	
	public void setColour(float red, float green, float blue)
	{
		colour.set(red, green, blue);
	}
	

	public Vector3f getColour() {
		return colour;
	}

	
	public int getNumberOfLines()
	{
		return numLines;
	}
	
    protected void setNumberOfLines(int numLines) {
        this.numLines = numLines;
    }

	public String getContent() {
		return content;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setMesh(int vao, int vertCount)
	{
		this.meshVAO = vao;
		this.vertCount = vertCount;
	}
	
	public int getMesh() {
		return meshVAO;
	}

	public int getVertCount() {
		return vertCount;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public void setPosition(Vector2f position)
	{
		this.position = position;
	}

	public float getMaxLineSize() {
		return maxLineSize;
	}

	public boolean isCentered() {
		return centered;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isFloating() {
		return isFloating;
	}

	public void setFloating(boolean isFloating) {
		this.isFloating = isFloating;
	}

	public Vector3f getWorldPosition() {
		return worldPosition;
	}

	public void setWorldPosition(Vector3f worldPosition) {
		this.worldPosition = worldPosition;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public Entity getAssocEntity() {
		return assocEntity;
	}

	public void setAssocEntity(Entity assocEntity) {
		this.assocEntity = assocEntity;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
