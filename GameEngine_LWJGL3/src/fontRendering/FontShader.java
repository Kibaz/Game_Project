package fontRendering;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram {
	
	private static final String VERTEX_PATH = "src/fontRendering/fontVert.txt";
	private static final String FRAGMENT_PATH = "src/fontRendering/fontFrag.txt";
	
	private int location_colour;
	private int location_translation;
	private int location_projectionMatrix;
	private int location_modelViewMatrix;
	private int location_isFloating;
	private int location_opacity;
	
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
		location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_isFloating = super.getUniformLocation("isFloating");
		location_opacity = super.getUniformLocation("opacity");
	}
	
	public void loadColour(Vector3f colour)
	{
		super.loadVector(location_colour, colour);
	}
	
	public void loadTranslation(Vector2f translation)
	{
		super.load2DVector(location_translation, translation);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewModelMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_modelViewMatrix, matrix);
	}
	
	public void loadFloatingText(boolean isFloating)
	{
		super.loadBoolean(location_isFloating, isFloating);
	}
	
	public void loadOpacity(float opacity)
	{
		super.loadFloat(location_opacity, opacity);
	}
	
	
	
	


}
