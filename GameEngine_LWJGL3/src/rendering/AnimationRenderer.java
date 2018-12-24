package rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import animation.AnimEntity;
import animation.AnimatedCharacter;
import animation.AnimatedFrame;
import animation.Animation;
import entities.Entity;
import models.BaseModel;
import models.TexturedModel;
import shaders.AnimatedModelShader;
import texturing.ModelTexture;
import utils.Maths;
import utils.Utils;

public class AnimationRenderer {
	
	private AnimatedModelShader shader;
	
	public AnimationRenderer(AnimatedModelShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(List<AnimatedCharacter> animatedChars)
	{
		for(AnimatedCharacter animChar: animatedChars)
		{
			prepareTexturedModel(animChar.getEntity().getModel(), animChar);
			prepareEntity(animChar.getEntity());
			BaseModel model = animChar.getEntity().getModel().getBaseModel();
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}

	}
	
	private void prepareTexturedModel(TexturedModel model, AnimatedCharacter animChar)
	{
		BaseModel baseModel = model.getBaseModel();
		GL30.glBindVertexArray(baseModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		ModelTexture texture = model.getTexture();
		shader.loadShineVariables(texture.getShineDamper(),texture.getReflectivity());
		shader.loadMatrixArray(animChar.getAnimationTransforms());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
	}
	
	private void unbindTexturedModel()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareEntity(Entity entity)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 
				entity.getRotX(),entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	

}
