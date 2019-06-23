package combat;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class IndicatorShader extends ShaderProgram {
	
	public static final String VERTEX_SHADER = "src/combat/indicatorVS.glsl";
	public static final String FRAGMENT_SHADER = "src/combat/indicatorFS.glsl";
	
	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;

	public IndicatorShader() {
		super(VERTEX_SHADER,FRAGMENT_SHADER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		location_modelMatrix = super.getUniformLocation("modelMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}
	
	
	public void loadModelMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_modelMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	

}
