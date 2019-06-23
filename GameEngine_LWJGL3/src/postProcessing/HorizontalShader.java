package postProcessing;

import shaders.ShaderProgram;

public class HorizontalShader extends ShaderProgram{
	
	private static final String VERTEX_SHADER = "src/postProcessing/horizontalBlurVert.glsl";
	private static final String FRAGMENT_SHADER = "src/postProcessing/blurFragment.glsl";
	
	private int location_fboWidth;

	public HorizontalShader() {
		super(VERTEX_SHADER,FRAGMENT_SHADER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		location_fboWidth = super.getUniformLocation("fboWidth");
	}
	
	public void loadFBOWidth(float width)
	{
		super.loadFloat(location_fboWidth, width);
	}

}
