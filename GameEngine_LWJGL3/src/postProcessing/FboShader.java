package postProcessing;

import shaders.ShaderProgram;

public class FboShader extends ShaderProgram {
	
	private static final String VERTEX_SHADER = "src/shaders/simpleVS.txt";
	private static final String FRAGMENT_SHADER = "src/postProcessing/contrastFragment.txt";

	public FboShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		
	}
	
	

}
