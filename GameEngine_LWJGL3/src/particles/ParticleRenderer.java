package particles;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import models.BaseModel;
import rendering.Loader;
import utils.Maths;

public class ParticleRenderer {
	
	// Basic fixed model until alternate solution is produced
	private static final float[] VERTICES = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
	private static final int MAX_INSTANCES = 10000;
	private static final int INSTANCE_DATA_LENGTH = 21;
	
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
	
	private BaseModel particleShape;
	private ParticleShader shader;
	
	private Loader loader;
	private int vbo;
	private int pointer = 0;
	
	public ParticleRenderer(Loader loader, Matrix4f projectionMatrix)
	{
		this.loader = loader;
		this.vbo = loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		particleShape = loader.loadToVAO(VERTICES, 2);
		loader.addInstancedAttrbute(particleShape.getVaoID(), vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
		loader.addInstancedAttrbute(particleShape.getVaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
		loader.addInstancedAttrbute(particleShape.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
		loader.addInstancedAttrbute(particleShape.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);
		loader.addInstancedAttrbute(particleShape.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 16);
		loader.addInstancedAttrbute(particleShape.getVaoID(), vbo, 6, 1, INSTANCE_DATA_LENGTH, 20);
		shader = new ParticleShader();
		shader.start(); // Ensure shader program has started before loading any data
		shader.loadProjectionMatrix(projectionMatrix); // Load projection matrix to glsl shader
		shader.stop(); // Stop the shader after loading
	}
	
	public void render(Map<ParticleTexture, List<Particle>> particles, Camera camera)
	{
		// render
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		prepare();
		for(ParticleTexture texture : particles.keySet())
		{
			bindTexture(texture);
			List<Particle> particleList = particles.get(texture);
			pointer = 0;
			float[] vboData = new float[particleList.size() * INSTANCE_DATA_LENGTH];
			for(Particle particle: particleList)
			{
				updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), viewMatrix, vboData);
				updateTextureCoordInfo(particle, vboData);
			}
			loader.updateVBO(vbo, vboData, buffer);
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, particleShape.getVertCount(), particleList.size());
		}
		finish();
		
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
	}
	
	private void updateTextureCoordInfo(Particle particle, float[] data)
	{
		data[pointer++] = particle.getTexOffset1().x;
		data[pointer++] = particle.getTexOffset1().y;
		data[pointer++] = particle.getTexOffset2().x;
		data[pointer++] = particle.getTexOffset2().y;
		data[pointer++] = particle.getBlend();
	}
	
	private void bindTexture(ParticleTexture texture)
	{
		if(texture.isAdditive())
		{
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		}
		else
		{
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
		shader.loadNumberOfRows(texture.getNumRows());
	}
	
	private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix, float[] vboData)
	{
		Matrix4f transMatrix = new Matrix4f();
		Matrix4f.translate(position, transMatrix, transMatrix);
		transMatrix.m00 = viewMatrix.m00;
		transMatrix.m01 = viewMatrix.m10;
		transMatrix.m02 = viewMatrix.m20;
		transMatrix.m10 = viewMatrix.m01;
		transMatrix.m11 = viewMatrix.m11;
		transMatrix.m12 = viewMatrix.m21;
		transMatrix.m20 = viewMatrix.m02;
		transMatrix.m21 = viewMatrix.m12;
		transMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0,0,1), transMatrix, transMatrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), transMatrix, transMatrix);
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, transMatrix, null);
		storeMatrixData(modelViewMatrix, vboData);
	}
	
	private void storeMatrixData(Matrix4f matrix, float[] vboData)
	{
		vboData[pointer++] = matrix.m00;
		vboData[pointer++] = matrix.m01;
		vboData[pointer++] = matrix.m02;
		vboData[pointer++] = matrix.m03;
		vboData[pointer++] = matrix.m10;
		vboData[pointer++] = matrix.m11;
		vboData[pointer++] = matrix.m12;
		vboData[pointer++] = matrix.m13;
		vboData[pointer++] = matrix.m20;
		vboData[pointer++] = matrix.m21;
		vboData[pointer++] = matrix.m22;
		vboData[pointer++] = matrix.m23;
		vboData[pointer++] = matrix.m30;
		vboData[pointer++] = matrix.m31;
		vboData[pointer++] = matrix.m32;
		vboData[pointer++] = matrix.m33;
	}
	
	private void prepare()
	{
		// prepare
		shader.start();
		GL30.glBindVertexArray(particleShape.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);
		GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
	}
	
	private void finish()
	{
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

}
