package rendering;


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
import shaders.BasicShader;
import shaders.StencilShader;
import texturing.ModelTexture;
import utils.Maths;

public class EntityRenderer {
	
	private BasicShader shader;
	private StencilShader stencilShader;
	
	public EntityRenderer(BasicShader shader,StencilShader stencilShader,Matrix4f projectionMatrix)
	{
		this.shader = shader;
		this.stencilShader = stencilShader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		stencilShader.start();
		stencilShader.loadProjectionMatrix(projectionMatrix);
		stencilShader.stop();
	}
	
	public void render(Map<TexturedModel,List<Entity>> entities)
	{	
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		
		GL11.glColorMask(false, false, false, false);
		GL11.glDepthMask(false);
		
		GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
		
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
		
		GL11.glStencilMask(0xFF);
		
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		
		
		shader.start();
		for(TexturedModel model:entities.keySet())
		{			
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch)
			{
				prepareEntity(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getBaseModel().getVertCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
			
		}
		shader.stop();
		
		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
		
		GL11.glStencilMask(0x00);
		
		GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
		
		stencilShader.start();
		for(TexturedModel model:entities.keySet())
		{			
			prepareTexturedModelForStencil(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch)
			{
				if(entity.isHovered())
				{
					prepareEntityForStencil(entity,0.025f);
					stencilShader.loadHostility(true);
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getBaseModel().getVertCount(), GL11.GL_UNSIGNED_INT, 0);
				}
				
				
			}
			unbindTexturedModel();
			
		}
		stencilShader.stop();
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		
		shader.start();
		for(TexturedModel model:entities.keySet())
		{			
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch)
			{
				prepareEntity(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getBaseModel().getVertCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
			
		}
		shader.stop();
	}
	
	private void prepareTexturedModel(TexturedModel model)
	{
		BaseModel baseModel = model.getBaseModel();
		GL30.glBindVertexArray(baseModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		if(texture.isHasTransparency())
		{
			AdvancedRenderer.disableCulling();
		}
		shader.loadFakeLighting(texture.isUseFakeLighting());
		shader.loadNumberOfRows(texture.getNumberOfRows());
		shader.loadShineVariables(texture.getShineDamper(),texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
	}
	
	private void prepareTexturedModelForStencil(TexturedModel model)
	{
		BaseModel baseModel = model.getBaseModel();
		GL30.glBindVertexArray(baseModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		if(texture.isHasTransparency())
		{
			AdvancedRenderer.disableCulling();
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
	}
	
	private void prepareEntity(Entity entity)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 
				entity.getRotX(),entity.getRotY(), entity.getRotZ(),entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
	
	private void prepareEntityForStencil(Entity entity, float scaleValue)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 
				entity.getRotX(),entity.getRotY(), entity.getRotZ(),entity.getScale() + scaleValue);
		stencilShader.loadTransformationMatrix(transformationMatrix);
	}
	
	private void unbindTexturedModel()
	{
		AdvancedRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

}
