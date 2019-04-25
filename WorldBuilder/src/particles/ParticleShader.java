package particles;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {
	
	private static final String VERTEX_PATH = "src/particles/particleVertexShader.txt";
	private static final String FRAGMENT_PATH = "src/particles/particleFragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_numberOfRows;

	public ParticleShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "textureOffsets");
		super.bindAttribute(6, "blendFactor");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadNumberOfRows(float rows) {
		super.loadFloat(location_numberOfRows, rows);
	}
	
	

}
