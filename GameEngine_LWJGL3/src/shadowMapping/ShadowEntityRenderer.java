package shadowMapping;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import animation.Bone;
import components.AnimationComponent;
import entities.Entity;
import equip.EquipItem;
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
		for(TexturedModel model: entities.keySet())
		{
			bindModel(model.getBaseModel());
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
			if(model.getTexture().isHasTransparency())
			{
				AdvancedRenderer.disableCulling();
			}
			List<Entity> batch = entities.get(model);
			for(Entity entity: batch)
			{
				
				renderEntity(entity,model);
				
			}
			if(model.getTexture().isHasTransparency())
			{
				AdvancedRenderer.enableCulling();
			}
			unbindModel();
		}
	}
	
	private void renderEntity(Entity entity, TexturedModel model)
	{
		if(entity.hasComponent(EquipItem.class))
		{
			EquipItem item = entity.getComponentByType(EquipItem.class);
			Entity parent = item.getParent();
			if(parent != null)
			{
				prepareChildEntity(entity,parent,item);
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
			shader.loadJointTransforms(animationComponent.getJointTransforms());
			shader.loadAnimated(true);
		}
		else
		{
			shader.loadAnimated(false);
		}	

		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getBaseModel().getVertCount(), GL11.GL_UNSIGNED_INT, 0);
	}
	
	private void bindModel(BaseModel model)
	{
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		if(model.hasAnimationData())
		{
			GL20.glEnableVertexAttribArray(3);
			GL20.glEnableVertexAttribArray(4);
		}
	}
	
	private void unbindModel()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareEntity(Entity entity)
	{
		Matrix4f modelMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), 
				entity.getRotZ(), entity.getScale());
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
		shader.loadMVPMatrix(mvpMatrix);
	}
	
	private void prepareChildEntity(Entity entity,Entity parent,EquipItem item)
	{
		// Render item at location of parent and attachment point
		AnimationComponent parentAnim = parent.getComponentByType(AnimationComponent.class);
		Bone bone = parentAnim.findBone(item.getAttachPoint());
		Matrix4f parentTransform = Maths.createTransformationMatrix(parent.getPosition(), 
				parent.getRotX(),parent.getRotY(),parent.getRotZ(), parent.getScale());
		Matrix4f boneWorldMatrix = Matrix4f.mul(parentTransform, bone.getFinalTransform(), null);
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, boneWorldMatrix, null);
		shader.loadMVPMatrix(mvpMatrix);
	}

}
