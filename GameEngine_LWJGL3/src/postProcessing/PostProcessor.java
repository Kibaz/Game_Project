package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import models.BaseModel;
import rendering.Loader;
import rendering.Window;

public class PostProcessor {
	
	private static final float[] VERTICES = {-1,1,-1,-1,1,1,1,-1};
	private static BaseModel quad;
	private static EffectChanger effectChanger;
	private static HorizontalBlur horizontalBlur;
	private static HorizontalBlur horizontalBlurLayer2;
	private static VerticalBlur verticalBlur;
	private static VerticalBlur verticalBlurLayer2;
	private static BlurFilter blurFilter;
	private static EffectMixer effectMixer;
	
	public static void init(Loader loader)
	{
		quad = loader.loadToVAO(VERTICES, 2);
		effectChanger = new EffectChanger();
		blurFilter = new BlurFilter(Window.getWidth()/2, Window.getHeight()/2);
		horizontalBlur = new HorizontalBlur(Window.getWidth()/8,Window.getHeight()/8);
		verticalBlur = new VerticalBlur(Window.getWidth()/8,Window.getHeight()/8);
		horizontalBlurLayer2 = new HorizontalBlur(Window.getWidth()/2,Window.getHeight()/2);
		verticalBlurLayer2 = new VerticalBlur(Window.getWidth()/2,Window.getHeight()/2);
		effectMixer = new EffectMixer();
	}
	
	public static void handlePostProcessing(int texture)
	{
		begin(); // Start rendering process
		blurFilter.render(texture);
		horizontalBlurLayer2.render(blurFilter.getOutputTexture());
		verticalBlurLayer2.render(horizontalBlurLayer2.getOutputTexture());
		horizontalBlur.render(verticalBlurLayer2.getOutputTexture());
		verticalBlur.render(horizontalBlur.getOutputTexture());
		effectMixer.render(texture, verticalBlur.getOutputTexture());
		end(); // End rendering process
	}
	
	public static void cleanUp()
	{
		effectChanger.cleanUp();
		horizontalBlur.cleanUp();
		verticalBlur.cleanUp();
		blurFilter.cleanUp();
		effectMixer.cleanUp();
	}
	
	public static void begin()
	{
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public static void end()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
