package guis;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class GUIShader extends ShaderProgram {
	
	private static final String VERTEX_PATH = "src/guis/guiVertexShader.txt";
	private static final String FRAGMENT_PATH = "src/guis/guiFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_hovered;
	private int location_fbo;

	public GUIShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
		
	}
	
	public void loadTransformationMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadHovered(boolean hovered)
	{
		super.loadBoolean(location_hovered, hovered);
	}
	
	public void loadFBO(boolean fbo)
	{
		super.loadBoolean(location_fbo, fbo);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_hovered = super.getUniformLocation("hovered");
		location_fbo = super.getUniformLocation("fbo");
		
	}

}
