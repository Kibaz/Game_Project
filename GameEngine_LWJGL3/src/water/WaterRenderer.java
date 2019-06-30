package water;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import models.BaseModel;
import rendering.Loader;
import rendering.Window;
import utils.Maths;

public class WaterRenderer {
	
	private static final String DUDV_MAP = "res/waterDUDV.png";
	private static final String NORMAL_MAP = "res/normal.png";
	private static final float WAVE_SPEED = 0.005f;
	
	private BaseModel waterPlane;
	private WaterShader shader;
	private WaterFBO fbos;
	
	private float waveMotion = 0;
	
	private int dudvTexture;
	private int normalMap;
	
	private float time = 0;
	
	public WaterRenderer(Loader loader, WaterShader shader, Matrix4f projectionMatrix, WaterFBO fbos)
	{
		this.shader = shader;
		this.fbos = fbos;
		dudvTexture = loader.loadTexture(DUDV_MAP);
		normalMap = loader.loadTexture(NORMAL_MAP);
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(List<WaterPlane> water, Camera camera, Light sun, float near, float far)
	{
		
		for(WaterPlane plane: water)
		{
			prepare(plane,camera, sun, near, far);
			Matrix4f modelMatrix = Maths.createTransformationMatrix(new Vector3f(plane.getX(),plane.getHeight(),plane.getZ())
					,0,0,0, WaterPlane.SIZE);
			shader.loadModelMatrix(modelMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, plane.getVertCount());
			unbind();
		}
		
	}
	
	private void prepare(WaterPlane plane, Camera camera, Light sun, float near, float far)
	{
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadLight(sun);
		shader.loadNearPlane(near);
		shader.loadFarPlane(far);
		shader.loadTime(Window.getFrameTime());
		waveMotion += WAVE_SPEED * Window.getFrameTime();
		waveMotion %= 1;
		shader.loadWaveMotion(waveMotion);
		updateTime();
		GL30.glBindVertexArray(plane.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		//GL20.glEnableVertexAttribArray(1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void unbind()
	{
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL20.glDisableVertexAttribArray(0);
		//GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private void updateTime()
	{
		time+= WAVE_SPEED;
		shader.loadTime(time);
	}

}
