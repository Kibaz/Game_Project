package guis;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.BaseModel;
import rendering.Loader;
import utils.Maths;

public class GUIRenderer {
	
	private final BaseModel quad;
	private GUIShader shader;
	
	public GUIRenderer(Loader loader)
	{
		float[] positions = { -1, 1, 
							-1, -1, 
							1, 1, 
							1, -1 };
		quad = loader.loadToVAO(positions,2);
		shader = new GUIShader();
	}
	
	public void render(List<GUI> guis)
	{
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		// Render non-selected GUIs
		for(GUI gui: guis)
		{
			if(gui.isVisible() && !gui.isSelected())
			{
				renderGUI(gui);
			}
		}
		// Render selected GUIs
		for(GUI gui: guis)
		{
			if(gui.isVisible() && gui.isSelected())
			{
				renderGUI(gui);
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private void renderGUI(GUI gui)
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getGUITexture().getTexture());
		Matrix4f matrix = Maths.createTransformationMatrix(gui.getGUITexture().getPosition(),gui.getGUITexture().getScale());
		shader.loadHovered(gui.isHovered());
		shader.loadTransformationMatrix(matrix);
		shader.loadFBO(gui.isFbo());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertCount());
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
	}

}
