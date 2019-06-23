package postProcessing;

import shaders.ShaderProgram;

public class MixerShader extends ShaderProgram {
	
	private static final String VERTEX_SHADER = "src/shaders/simpleVS.txt";
	private static final String FRAGMENT_SHADER = "src/PostProcessing/mixerFrag.glsl";
	
	private int location_colourTexture;
	private int location_effectTexture;

	public MixerShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		location_colourTexture = super.getUniformLocation("colourTexture");
		location_effectTexture = super.getUniformLocation("effectTexture");
		
	}
	
	public void connectTextureUnits()
	{
		super.loadInt(location_colourTexture, 0);
		super.loadInt(location_effectTexture, 1);
	}

}
