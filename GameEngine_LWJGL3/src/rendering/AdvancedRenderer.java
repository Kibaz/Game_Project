package rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import combat.Ability;
import combat.AbilityRenderer;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.EntityShader;
import shaders.StencilShader;
import shaders.TerrainShader;
import shadowMapping.ShadowRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;

public class AdvancedRenderer {
	
	public static final float FOV = 50;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	
	private static final float RED = 0.5444f;
	private static final float GREEN = 0.62f;
	private static final float BLUE = 0.69f;
	
	private Matrix4f projectionMatrix;
	
	private boolean applyCausticEffect;
	
	private EntityShader entityShader = new EntityShader();
	private StencilShader stencilShader = new StencilShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private ShadowRenderer shadowRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	
	private AbilityRenderer abilityRenderer;
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<>(); // Store entities with reference to their model
	private List<Terrain> terrains = new ArrayList<>(); // Store list of terrains to be rendered
	
	private List<Ability> abilities;
	public AdvancedRenderer(Loader loader, Camera camera)
	{
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(entityShader,stencilShader,projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		abilityRenderer = new AbilityRenderer(loader,projectionMatrix);
		this.shadowRenderer = new ShadowRenderer(camera);
	}
	
	public static void enableCulling()
	{
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void renderScene(List<Entity> entities, List<Terrain> terrains, List<Light> lights, Camera camera, Vector4f clipPlane)
	{
		for(Terrain terrain: terrains)
		{
			processTerrain(terrain);
		}
		
		for(Entity entity: entities)
		{
			processEntity(entity);
		}
		
		render(lights, camera,clipPlane);
	}
	
	public void render(List<Light> lights, Camera camera, Vector4f clipPlane)
	{
		prepare();
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		if(applyCausticEffect)
		{
			terrainShader.loadCausticEffect(0.1f);
			
		}
		else
		{
			terrainShader.loadCausticEffect(0.0f);
		}
		terrainRenderer.render(terrains,shadowRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		abilityRenderer.render(abilities, camera);
		entityShader.start();
		entityShader.loadClipPlane(clipPlane);
		entityShader.loadLights(lights);
		entityShader.loadViewMatrix(camera);
		entityShader.stop();
		stencilShader.start();
		stencilShader.loadViewMatrix(camera);
		stencilShader.stop();
		renderer.render(entities);

		skyboxRenderer.render(camera, RED, GREEN, BLUE);
		terrains.clear();
		entities.clear();
	}
	
	public void processTerrain(Terrain terrain)
	{
		terrains.add(terrain);
	}
	
	
	public void prepare()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0.0f, 0.0f, 0.1f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}
	
	public void processEntity(Entity entity)
	{
		TexturedModel model = entity.getModel();
		List<Entity> batch = entities.get(model);
		if(batch!=null)
		{
			batch.add(entity);
		}else
		{
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(model, newBatch);
		}
	}
	
	public void renderShadowMap(List<Entity> entityList, Light sun)
	{
		for(Entity entity: entityList)
		{
			processEntity(entity);
		}
		shadowRenderer.render(entities, sun);
		entities.clear();
	}
	
	public void setAbilities(List<Ability> abilities)
	{
		this.abilities = abilities;
	}
	
	public int getShadowMapTexture()
	{
		return shadowRenderer.getShadowMap();
	}
	
	public void cleanUp()
	{
		entityShader.cleanUp();
		terrainShader.cleanUp();
		shadowRenderer.cleanUp();
	}
	
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
	private void createProjectionMatrix()
	{
		float aspectRatio = (float) Window.getWidth() / (float) Window.getHeight();
		float y_scale = (float) ((1f /Math.tan(Math.toRadians(FOV/2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2*NEAR_PLANE*FAR_PLANE)/frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	public float getNearPlane()
	{
		return NEAR_PLANE;
	}
	
	public float getFarPlane()
	{
		return FAR_PLANE;
	}
	
	public void shouldApplyCausticEffect(boolean apply)
	{
		this.applyCausticEffect = apply;
	}
	
}
