package guis;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import models.BaseModel;
import rendering.Loader;
import utils.Maths;

public class HUDRenderer {
	
	private final BaseModel quad;
	private HUDShader shader;
	
	public HUDRenderer(Loader loader, Matrix4f projectionMatrix)
	{
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		quad = loader.loadToVAO(positions, 2);
		shader = new HUDShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(List<HUD> huds, Camera camera)
	{
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		for(HUD hud: huds)
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, hud.getTexture());
			Matrix4f matrix = createModelViewMatrix(hud.getPosition(),0,hud.getScale(),viewMatrix);
			shader.loadMaxScaleX(hud.getMaxScaleX());
			shader.loadCurrentScaleX(hud.getScale().x);
			shader.loadHealthPool(hud.isHealthPool());
			shader.loadModelViewMatrix(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertCount());
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private Matrix4f createModelViewMatrix(Vector3f position, float rotation, Vector3f scale, Matrix4f viewMatrix)
	{
		Matrix4f transformationMatrix = new Matrix4f();
		Matrix4f.translate(position, transformationMatrix, transformationMatrix);
		transformationMatrix.m00 = viewMatrix.m00;
		transformationMatrix.m01 = viewMatrix.m10;
		transformationMatrix.m02 = viewMatrix.m20;
		transformationMatrix.m10 = viewMatrix.m01;
		transformationMatrix.m11 = viewMatrix.m11;
		transformationMatrix.m12 = viewMatrix.m21;
		transformationMatrix.m20 = viewMatrix.m02;
		transformationMatrix.m21 = viewMatrix.m12;
		transformationMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0,0,1), transformationMatrix, transformationMatrix);
		Matrix4f.scale(scale, transformationMatrix, transformationMatrix);
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, transformationMatrix, null);
		return modelViewMatrix;
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
	}
	
	

}
