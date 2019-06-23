package guis;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class HUDShader extends ShaderProgram {
	
	private static final String VERTEX_SHADER = "src/guis/hudVertexShader.txt";
	private static final String FRAGMENT_SHADER = "src/guis/hudFragmentShader.txt";
	
	private int location_viewModelMatrix;
	private int location_projectionMatrix;
	private int location_isHealthPool;
	private int location_currentScaleX;
	private int location_maxScaleX;

	public HUDShader() {
		super(VERTEX_SHADER,FRAGMENT_SHADER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		location_viewModelMatrix = super.getUniformLocation("modelViewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_isHealthPool = super.getUniformLocation("isHealthPool");
		location_maxScaleX = super.getUniformLocation("maxScaleX");
		location_currentScaleX = super.getUniformLocation("currentScaleX");
	}
	
	public void loadModelViewMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_viewModelMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadHealthPool(boolean isHealthPool)
	{
		super.loadBoolean(location_isHealthPool, isHealthPool);
	}
	
	public void loadMaxScaleX(float maxScaleX)
	{
		super.loadFloat(location_maxScaleX, maxScaleX);
	}
	
	public void loadCurrentScaleX(float currentScaleX)
	{
		super.loadFloat(location_currentScaleX, currentScaleX);
	}
	

}
