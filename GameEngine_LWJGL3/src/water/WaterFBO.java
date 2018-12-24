package water;

import rendering.FrameBufferCreator;

public class WaterFBO extends FrameBufferCreator {
	
	private static final int REFLECTION_WIDTH = 320;
	private static final int REFLECTION_HEIGHT = 180;
	
	private static final int REFRACTION_WIDTH = 1280;
	private static final int REFRACTION_HEIGHT = 720;
	
	private int reflectionFrameBuffer;
	private int reflectionTexture;
	private int reflectionDepthBuffer;
	
	private int refractionFrameBuffer;
	private int refractionTexture;
	private int refractionDepthTexture;
	
	public WaterFBO()
	{
		super();
	}

	@Override
	protected void initBuffers() {
		initReflectionBuffer();
		initRefractionBuffer();
	}
	
	public int getReflectionTexture() {
		return reflectionTexture;
	}

	public int getRefractionTexture() {
		return refractionTexture;
	}

	public int getRefractionDepthTexture() {
		return refractionDepthTexture;
	}

	private void initReflectionBuffer()
	{
		reflectionFrameBuffer = super.createFrameBuffer();
		super.frameBuffers.add(reflectionFrameBuffer);
		reflectionTexture = super.createBindingTexture(REFLECTION_WIDTH, REFLECTION_HEIGHT);
		super.textures.add(reflectionTexture);
		reflectionDepthBuffer = super.createRenderBuffer(REFLECTION_WIDTH, REFLECTION_HEIGHT);
		super.renderBuffers.add(reflectionDepthBuffer);
		super.unbindFrameBuffer();
	}
	
	private void initRefractionBuffer()
	{
		refractionFrameBuffer = super.createFrameBuffer();
		super.frameBuffers.add(refractionFrameBuffer);
		refractionTexture = super.createBindingTexture(REFRACTION_WIDTH, REFRACTION_HEIGHT);
		super.textures.add(refractionTexture);
		refractionDepthTexture = super.createDepthBindingTexture(REFRACTION_WIDTH, REFRACTION_HEIGHT);
		super.textures.add(refractionDepthTexture);
		super.unbindFrameBuffer();
	}
	
	public void bindReflectionBuffer()
	{
		super.bindFrameBuffer(reflectionFrameBuffer, REFLECTION_WIDTH, REFLECTION_HEIGHT);
	}
	
	public void bindRefractionBuffer()
	{
		super.bindFrameBuffer(refractionFrameBuffer, REFRACTION_WIDTH, REFRACTION_HEIGHT);
	}
	
	

}
