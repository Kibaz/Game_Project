package postProcessing;

import org.lwjgl.opengl.GL11;

import buffers.FBO;

public class FboRenderer {
	
	private FBO fbo;
	
	public FboRenderer(int width, int height)
	{
		this.fbo = new FBO(width, height, FBO.NONE);
	}
	
	public FboRenderer() {}
	
	public void render()
	{
		if(fbo != null)
		{
			fbo.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if(fbo != null)
		{
			fbo.unbindFrameBuffer();
		}
	}
	
	public int getOutputTexture()
	{
		return fbo.getColourTexture();
	}
	
	public void cleanUp()
	{
		if(fbo != null)
		{
			fbo.cleanUp();
		}
	}
	
	public void setFBO(FBO fbo)
	{
		this.fbo = fbo;
	}

}
