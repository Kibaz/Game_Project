package equip;

import rendering.FrameBufferCreator;

public class EntityFBO extends FrameBufferCreator {
	
	private static final int WIDTH = 300;
	private static final int HEIGHT = 400;
	
	private int frameBuffer;
	private int texture;
	private int depthBuffer;
	
	public EntityFBO()
	{
		super();
	}

	@Override
	protected void initBuffers() {
		frameBuffer = super.createFrameBuffer();
		texture = super.createBindingTexture(WIDTH, HEIGHT);
		super.textures.add(texture);
		depthBuffer = super.createRenderBuffer(WIDTH, HEIGHT);
		super.renderBuffers.add(depthBuffer);
		super.unbindFrameBuffer();
	}
	
	public void bindBuffer()
	{
		super.bindFrameBuffer(frameBuffer, WIDTH, HEIGHT);
	}

	public int getFrameBuffer() {
		return frameBuffer;
	}

	public int getTexture() {
		return texture;
	}

	public int getDepthBuffer() {
		return depthBuffer;
	}
	
	

}
