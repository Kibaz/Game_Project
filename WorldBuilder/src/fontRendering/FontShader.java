package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram {
	
	private static final String VERTEX_PATH = "src/fontRendering/fontVert.txt";
	private static final String FRAGMENT_PATH = "src/fontRendering/fontFrag.txt";
	
	private int location_colour;
	private int location_translation;
	
	public FontShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
	}
	
	public void loadColour(Vector3f colour)
	{
		super.loadVector(location_colour, colour);
	}
	
	public void loadTranslation(Vector2f translation)
	{
		super.load2DVector(location_translation, translation);
	}
	
	


}
