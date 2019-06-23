package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class EffectMixer extends PostProcessingEffect{

	public EffectMixer() {
		super(new MixerShader());
		shader.start();
		((MixerShader) shader).connectTextureUnits();
		shader.stop();
		
	}
	
	public void render(int colourTexture, int effectTexture)
	{
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, effectTexture);
		renderer.render();
		shader.stop();
	}

}
