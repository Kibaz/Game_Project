package fontRendering;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import utils.Maths;

public class FontRenderer {
	
	private FontShader shader;
	private Matrix4f projectionMatrix;
	
	public FontRenderer(Matrix4f projectionMatrix)
	{
		this.shader = new FontShader();
		this.projectionMatrix = projectionMatrix;
	}
	
	public void render(Map<FontStyle, List<GUIText>> texts,Camera camera)
	{
		prepare();
		for(FontStyle font: texts.keySet())
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			List<GUIText> list = texts.get(font);
			for(GUIText text : list)
			{
				renderText(text,camera);
			}
		}
		finish();
	}

	private void prepare()
	{	
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.start();
	}
	
	private void renderText(GUIText text,Camera camera)
	{
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadColour(text.getColour());
		shader.loadOpacity(text.getOpacity());
		if(text.isFloating())
		{
			Matrix4f viewMatrix = Maths.createViewMatrix(camera);
			shader.loadProjectionMatrix(projectionMatrix);
			shader.loadViewModelMatrix(Maths.createModelViewMatrix(text.getWorldPosition(), 0f, new Vector3f(10,10,10), viewMatrix));
			shader.loadFloatingText(true);
		}
		else
		{
			shader.loadFloatingText(false);
			shader.loadTranslation(text.getPosition());
		}
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void finish()
	{
		shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
	}

}
