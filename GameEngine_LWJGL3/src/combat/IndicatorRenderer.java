package combat;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import utils.Maths;

public class IndicatorRenderer {
	
	private IndicatorShader shader;
	
	public IndicatorRenderer(Matrix4f projectionMatrix)
	{
		shader = new IndicatorShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(DamageIndicator indicator, Camera camera)
	{
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.start();
		shader.loadViewMatrix(Maths.createViewMatrix(camera));
		prepareIndicator(indicator);
		shader.loadModelMatrix(Maths.createTransformationMatrix(indicator.getPosition(), 0, indicator.getRotY(), 0, 1));
		GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, indicator.getModel().getVertCount());
		unbindIndicator();
		shader.stop();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void prepareIndicator(DamageIndicator indicator)
	{
		GL30.glBindVertexArray(indicator.getModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
	}
	
	public void unbindIndicator()
	{
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
	}

}
