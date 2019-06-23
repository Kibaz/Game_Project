package postProcessing;

import shaders.ShaderProgram;

public class VerticalShader extends ShaderProgram {
	
	private static final String VERTEX_SHADER = "src/postProcessing/verticalBlurVert.glsl";
	private static final String FRAGMENT_SHADER = "src/postProcessing/blurFragment.glsl";
	
	private int location_fboHeight;

	public VerticalShader() {
		super(VERTEX_SHADER,FRAGMENT_SHADER);
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		location_fboHeight = super.getUniformLocation("fboHeight");
		
	}
	
	public void loadFBOHeight(float height)
	{
		super.loadFloat(location_fboHeight, height);
	}

}
