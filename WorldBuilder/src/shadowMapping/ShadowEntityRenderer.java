package shadowMapping;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.BaseModel;
import models.TexturedModel;
import rendering.AdvancedRenderer;
import utils.Maths;

public class ShadowEntityRenderer {
	
	private Matrix4f projectionViewMatrix;
	private ShadowShader shader;
	
	public ShadowEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix)
	{
		this.projectionViewMatrix = projectionViewMatrix;
		this.shader = shader;
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities)
	{
		for(TexturedModel tmodel: entities.keySet())
		{
			BaseModel model = tmodel.getBaseModel();
			bindModel(model);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tmodel.getTexture().getTextureID());
			if(tmodel.getTexture().isHasTransparency())
			{
				AdvancedRenderer.disableCulling();
			}
			List<Entity> batch = entities.get(tmodel);
			for(Entity entity: batch)
			{
				prepareEntity(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			if(tmodel.getTexture().isHasTransparency())
			{
				AdvancedRenderer.enableCulling();
			}
		}
		unbindModel();
	}
	
	private void bindModel(BaseModel model)
	{
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}
	
	private void unbindModel()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareEntity(Entity entity)
	{
		Matrix4f modelMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), 
				entity.getRotZ(), entity.getScale());
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
		shader.loadMVPMatrix(mvpMatrix);
	}

}
