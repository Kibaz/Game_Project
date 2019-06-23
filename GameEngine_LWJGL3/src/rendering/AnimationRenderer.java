package rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import animation.AnimatedEntity;
import models.BaseModel;
import models.TexturedModel;
import shaders.AnimatedModelShader;
import texturing.ModelTexture;
import utils.Maths;

public class AnimationRenderer {
	
	private AnimatedModelShader shader;
	
	public AnimationRenderer(AnimatedModelShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(AnimatedEntity entity)
	{
		prepareEntity(entity);
		shader.loadMatrixArray(entity.getJointTransforms());
		prepareModel(entity.getModel());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getBaseModel().getVertCount(),GL11.GL_UNSIGNED_INT,0);
		unbindMesh();
	}
	
	private void prepareModel(TexturedModel model)
	{
		GL30.glBindVertexArray(model.getBaseModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		ModelTexture texture = model.getTexture();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
	}
	
	private void prepareEntity(AnimatedEntity entity)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(),entity.getRotZ(),entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	private void unbindMesh()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);
	}

}
