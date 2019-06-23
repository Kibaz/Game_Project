package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import shaders.ShaderProgram;

public abstract class PostProcessingEffect {
	
	protected FboRenderer renderer;
	protected ShaderProgram shader;
	
	public PostProcessingEffect(ShaderProgram shader)
	{
		renderer = new FboRenderer();
		this.shader = shader;
	}
	
	protected void render(int texture)
	{
		shader.start();
		GL30.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		renderer.render();
		shader.stop();
	}
	
	protected void cleanUp()
	{
		shader.cleanUp();
		renderer.cleanUp();
	}
	
	public int getOutputTexture()
	{
		return renderer.getOutputTexture();
	}

}
