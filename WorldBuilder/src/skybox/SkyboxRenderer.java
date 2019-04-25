package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import models.BaseModel;
import rendering.Loader;
import rendering.Window;

public class SkyboxRenderer {
	
	private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {
		    -SIZE,  SIZE, -SIZE,
		    -SIZE, -SIZE, -SIZE,
		    SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,
		     SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,

		    -SIZE, -SIZE,  SIZE,
		    -SIZE, -SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE,  SIZE,
		    -SIZE, -SIZE,  SIZE,

		     SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,

		    -SIZE, -SIZE,  SIZE,
		    -SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE, -SIZE,  SIZE,
		    -SIZE, -SIZE,  SIZE,

		    -SIZE,  SIZE, -SIZE,
		     SIZE,  SIZE, -SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		    -SIZE,  SIZE,  SIZE,
		    -SIZE,  SIZE, -SIZE,

		    -SIZE, -SIZE, -SIZE,
		    -SIZE, -SIZE,  SIZE,
		     SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,
		    -SIZE, -SIZE,  SIZE,
		     SIZE, -SIZE,  SIZE
	};
	
	private static String[] TEXTURE_PATHS = {"right.tga", "left.tga", "top.tga", "bottom.tga", "back.tga", "front.tga"};
	private static String[] NIGHT_TEXTURE_PATHS = {"night_rt.tga", "night_lf.tga", "night_up.tga", "night_bot.tga", "night_bk.tga", "night_ft.tga"};
	
	private BaseModel cube;
	private int texture;
	private int nightTexture;
	private SkyboxShader shader;
	private float timeElapsed = 0;
	
	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix)
	{
		cube = loader.loadToVAO(VERTICES, 3);
		texture = loader.loadCubeMap(TEXTURE_PATHS);
		nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_PATHS);
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Camera camera, float r, float g, float b)
	{
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadFogColour(r,g,b);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private void bindTextures()
	{
		timeElapsed += Window.getFrameTime() * 1000;
		timeElapsed %= 24000;
		int texture1 = texture;
		int texture2 = nightTexture;
		float blend = 0;		
		
		/*
		 * Deprecated code - used to test Day/Night Cycle is feasible
		 * 
		if(time >= 0 && time < 5000)
		{
			texture1 = nightTexture;
			texture2 = nightTexture;
			blend = (time-0)/(5000-0);
		}
		else if(time >= 5000 && time < 8000)
		{
			texture1 = nightTexture;
			texture2 = texture;
			blend = (time - 5000)/(8000 - 5000);
		}
		else if(time >= 8000 && time < 21000)
		{
			texture1 = nightTexture;
			texture2 = texture;
			blend = (time-8000)/(21000 - 8000);
		}
		else
		{
			texture1 = texture;
			texture2 = texture;
			blend = (time - 21000)/(24000 - 21000);
		}*/
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlend(blend);
	}
	

}
