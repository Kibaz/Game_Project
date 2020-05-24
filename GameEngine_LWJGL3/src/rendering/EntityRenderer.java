package rendering;


import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import animation.Bone;
import components.AnimationComponent;
import components.EntityInformation;
import entities.Entity;
import equip.EquipInventory;
import equip.EquipItem;
import inventory.Inventory;
import inventory.Item;
import models.BaseModel;
import models.TexturedModel;
import shaders.EntityShader;
import shaders.StencilShader;
import texturing.ModelTexture;
import utils.Maths;

public class EntityRenderer {
	
	//private BasicShader shader;
	private EntityShader shader;
	private StencilShader stencilShader;
	
	public EntityRenderer(EntityShader shader,StencilShader stencilShader,Matrix4f projectionMatrix)
	{
		this.shader = shader;
		this.stencilShader = stencilShader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		if(stencilShader != null)
		{
			stencilShader.start();
			stencilShader.loadProjectionMatrix(projectionMatrix);
			stencilShader.stop();
		}

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
		renderEntityPass(entities);
		shader.stop();
		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
		
		GL11.glStencilMask(0x00);
		
		GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
		
		stencilShader.start();
		renderStencilPass(entities);
		stencilShader.stop();
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		shader.start();
		renderEntityPass(entities);
		shader.stop();
	}
	
	private void renderEntityPass(Map<TexturedModel,List<Entity>> entities)
	{
		for(TexturedModel model:entities.keySet())
		{			
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity: batch)
			{	
				renderEntity(entity,model);
			}
			unbindTexturedModel();
			
		}
	}
	
	private void renderEntity(Entity entity, TexturedModel model) {
		if(entity.hasComponent(EquipItem.class))
		{
			EquipItem item = entity.getComponentByType(EquipItem.class);
			Entity parent = item.getParent();
			if(parent != null && item.getParent().getComponentByType(EquipInventory.class).isEquipped(entity))
			{
				// Render item at location of parent and attachment point
				AnimationComponent parentAnim = parent.getComponentByType(AnimationComponent.class);
				Bone bone = parentAnim.findBone(item.getAttachPoint());
				Matrix4f parentTransform = Maths.createTransformationMatrix(parent.getPosition(), 
						parent.getRotX(),parent.getRotY(),parent.getRotZ(), parent.getScale());
				Matrix4f boneWorldMatrix = Matrix4f.mul(parentTransform, bone.getFinalTransform(), null);
				shader.loadTransformationMatrix(boneWorldMatrix);
			}
			else
			{
				prepareEntity(entity); // Got dropped
			}
		}
		else
		{
			prepareEntity(entity);
		}
		AnimationComponent animationComponent = entity.getComponentByType(AnimationComponent.class);
		if(animationComponent != null)
		{
			shader.loadAnimationComponent(true);
			shader.loadJointTransforms(animationComponent.getJointTransforms());
		}
		else
		{
			shader.loadAnimationComponent(false);
		}
		
		boolean render = true;
		if(entity.hasComponent(Item.class) || entity.hasComponent(EquipItem.class))
		{
			Item item = entity.getComponentByType(Item.class);
			if(item == null) item = entity.getComponentByType(EquipItem.class);
			if(item.getParent() != null && item.getParent().hasComponent(Inventory.class)
					&& item.getParent().getComponentByType(Inventory.class).containsItem(item))
			{
				render = false;
			}
		}
		
		if(render)
		{
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.getBaseModel().getVertCount(),GL11.GL_UNSIGNED_INT,0);
		}
		
		
		
	}
	
	public void renderSingleEntity(Entity entity)
	{
		shader.start();
		prepareTexturedModel(entity.getModel());
		renderEntity(entity,entity.getModel());
		shader.stop();
	}
	
	private void renderStencilPass(Map<TexturedModel,List<Entity>> entities)
	{
		for(TexturedModel model: entities.keySet())
		{			
			prepareTexturedModelForStencil(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity: batch)
			{
				if(entity.isHovered())
				{
					prepareEntityForStencil(entity,0.025f);
					AnimationComponent animationComponent = entity.getComponentByType(AnimationComponent.class);
					
					if(animationComponent != null)
					{
						stencilShader.loadAnimationComponent(true);
						stencilShader.loadJointTransforms(animationComponent.getJointTransforms());
					}
					else
					{
						stencilShader.loadAnimationComponent(false);
					}
					
					if(entity.hasComponent(EntityInformation.class))
					{
						EntityInformation info = entity.getComponentByType(EntityInformation.class);
						stencilShader.loadHostility(info.isHostile());
					}
					else
					{
						// Tell GPU if the entity is an item
						stencilShader.loadHostility(false);
					}
					
					stencilShader.loadIsItem(entity.hasComponent(EquipItem.class) || entity.hasComponent(Item.class));

					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getBaseModel().getVertCount(), GL11.GL_UNSIGNED_INT, 0);
				}

			}
			unbindTexturedModel();

		}
	}
	
	private void prepareTexturedModel(TexturedModel model)
	{
		BaseModel baseModel = model.getBaseModel();
		GL30.glBindVertexArray(baseModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		if(model.getBaseModel().hasAnimationData())
		{
			GL20.glEnableVertexAttribArray(3);
			GL20.glEnableVertexAttribArray(4);
		}
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
		if(model.getBaseModel().hasAnimationData())
		{
			GL20.glEnableVertexAttribArray(3);
			GL20.glEnableVertexAttribArray(4);
		}
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
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);
	}

}
