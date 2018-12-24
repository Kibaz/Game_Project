package fontUtils;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import fontRendering.TextController;

public class GUIText {
	
	private String content;
	private float fontSize;
	
	private int meshVAO;
	private int vertCount;
	private Vector3f colour = new Vector3f(0,0,0);
	
	private Vector2f position; // Relative to the position on the screen as opposed to the 3D world
	private float maxLineSize;
	private int numLines;
	
	private FontStyle font;
	
	private boolean centered = false;
	
	public GUIText(String text, float fontSize, FontStyle font, Vector2f position, float maxLineSize, boolean centered)
	{
		this.content = text;
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		this.maxLineSize = maxLineSize;
		this.centered = centered;
		// load text
		TextController.loadText(this);
	}
	
	public void remove()
	{
		// remove text
		TextController.removeText(this);
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

	public float getMaxLineSize() {
		return maxLineSize;
	}

	public boolean isCentered() {
		return centered;
	}
	
	
	
	
}
