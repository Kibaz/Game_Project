package runtime;

import java.io.File;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import animation.AnimMeshLoader;
import animation.AnimatedCharacter;
import animation.Animation;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.TestMob;
import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUIRenderer;
import guis.GUITexture;
import models.BaseModel;
import models.TexturedModel;
import networking.Client;
import networking.PlayerData;
import particles.ParticleManager;
import pathfinding.Graph;
import pathfinding.GridSquare;
import physics.SAP;
import rendering.AdvancedRenderer;
import rendering.Loader;
import rendering.Window;
import terrains.Terrain;
import texturing.ModelTexture;
import water.WaterFBO;
import water.WaterPlane;
import water.WaterRenderer;
import water.WaterShader;
import worldData.TigranStartZone;
import worldData.World;

public class Main {

	public static void main(String[] args) throws UnknownHostException, SocketException {
		
		// Initialise lists of terrains, entities, animated characters, lights etc...
		List<Terrain> terrains = new ArrayList<>(); // Terrains
		List<Light> lights = new ArrayList<>(); // Lights
		List<AnimatedCharacter> animatedChars = new ArrayList<>(); // Animated characters
		List<Entity> entities = new ArrayList<>(); // Entities
		List<WaterPlane> water = new ArrayList<>(); // Any water?
		List<GUITexture> guis = new ArrayList<>(); // Store GUIs
		
		// Initialise loader for the game
		/*
		 * The Loader will handle loading 
		 * physical models, graphics, 
		 * text etc...
		 */
		Loader loader = new Loader();
		
		// Initialise Client communications program
		Client.serverAddress = InetAddress.getByName("127.0.0.1"); // Server IP address
		Client.serverPort = 8129; // Server port
		// Start listening for responses from the server
		
		// Initialise window
		Window.init();
		
		// Initialise Font Handler
		TextController.init(loader);
		
		// Initialise and create zones
		// Add game objects stored in each zone to prepare for rendering
		TigranStartZone tigranStartZone = new TigranStartZone(loader);
		terrains.addAll(tigranStartZone.getTerrains());
		lights.addAll(tigranStartZone.getLights());
		entities.addAll(tigranStartZone.getEntities());
		water.addAll(tigranStartZone.getWater());
		World.addZone(tigranStartZone.getName(), tigranStartZone);
		
		/* Test loading an animated character */
		BaseModel[] testModels = null;
		
		Animation animation = null;
		try {
			animation = AnimMeshLoader.loadAnimation("res/model.dae", loader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		testModels = animation.getModels();
		
		ModelTexture playerTex = new ModelTexture(loader.loadTexture("res/Character Texture.png"));
		TexturedModel playerTexMod = new TexturedModel(testModels[0], playerTex);
		
		Player player = new Player(loader,playerTexMod, new Vector3f(100,tigranStartZone.getTerrains().get(0).getTerrainHeight(100, 90),90),0,0,0,1);
		guis.add(player.getHealthBar());
		Client.setPreviousPlayerPosition(player.getPosition());
		World.addEntity(player);
		
		/* New code for setting up a single animated character */
		AnimatedCharacter animChar = new AnimatedCharacter(player);
		animChar.submitAnimation(AnimatedCharacter.RUN, animation);
		animChar.setCurrentAnimation(AnimatedCharacter.RUN);
		animChar.playCurrentAnimation();
		animatedChars.add(animChar);
		
		Client.listen();
		Client.send("Requesting connection".getBytes());
		
		// Set up player camera - 3rd person camera
		Camera camera = new Camera(player);
		
		AdvancedRenderer renderer = new AdvancedRenderer(loader, camera);
		
		ParticleManager.init(loader, renderer.getProjectionMatrix());
		
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		
		WaterFBO waterFBOS = new WaterFBO();
		
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFBOS);
		
		// Initialising physics
		SAP sap = new SAP();
		
		while(!Window.closed())
		{
			/*window.clear();*/
			
			// Testing client side prediction
			player.movePlayer(terrains, water, entities);
			//waterPlane.update(player);
			for(Entity entity: entities)
			{
				if(entity.isClicked())
				{
					if(entity instanceof TestMob)
					{
						TestMob curr = (TestMob) entity;
						if(!guis.contains(curr.getHealthBar()))
						{
							guis.add(curr.getHealthBar());
						}
						
					}
				}
				
				if(entity instanceof TestMob)
				{
					((TestMob) entity).update(player,terrains);
				}
			}
			
			// Carry out animations
			animChar.getAnimator().update();
			
			// Carry out physics engine
			sap.update();
			
			// Calculate camera movement based on player
			camera.move();
			
			renderer.renderShadowMap(entities, tigranStartZone.getSun());
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			if(water.get(0).isCameraUnderWater(camera))
			{
				renderer.shouldApplyCausticEffect(true);
				// render reflection texture
				waterFBOS.bindReflectionBuffer();
				float distance = 2 * (camera.getPosition().y - water.get(0).getHeight());
				camera.getPosition().y -= distance;
				camera.invertPitch();
				renderer.renderScene(entities, terrains, animatedChars, lights, camera, new Vector4f(0,-1,0,water.get(0).getHeight()+0.3f));
				camera.getPosition().y += distance;
				camera.invertPitch();
				waterFBOS.unbindFrameBuffer();
				
				// render refraction texture
				waterFBOS.bindRefractionBuffer();
				renderer.renderScene(entities, terrains,  animatedChars, lights, camera, new Vector4f(0,1,0,-water.get(0).getHeight() + 1f));
				waterFBOS.unbindFrameBuffer();
			}
			else
			{
				renderer.shouldApplyCausticEffect(false);
				// render reflection texture
				waterFBOS.bindReflectionBuffer();
				float distance = 2 * (camera.getPosition().y - water.get(0).getHeight());
				camera.getPosition().y -= distance;
				camera.invertPitch();
				renderer.renderScene(entities, terrains,  animatedChars, lights, camera, new Vector4f(0,1,0,-water.get(0).getHeight()+0.3f));
				camera.getPosition().y += distance;
				camera.invertPitch();
				waterFBOS.unbindFrameBuffer();
				
				// render refraction texture
				waterFBOS.bindRefractionBuffer();
				renderer.renderScene(entities, terrains,  animatedChars, lights, camera, new Vector4f(0,-1,0,water.get(0).getHeight() + 1f));
				waterFBOS.unbindFrameBuffer();
			}
			
			// render to the display, i.e default frame buffer
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			renderer.renderScene(entities, terrains, animatedChars, lights, camera, new Vector4f(0,-1,0,0));
			waterRenderer.render(water, camera, tigranStartZone.getSun(), renderer.getNearPlane(), renderer.getFarPlane());
			
			ParticleManager.render(camera);
			
			guiRenderer.render(guis);
			TextController.render();
			Window.update();;
		}
		
		// Clean up all rendering, processing etc...
		waterFBOS.cleanUp();
		waterShader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		Window.destroy();
		Client.disconnect();
	}
}
