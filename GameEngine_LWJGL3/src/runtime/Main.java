package runtime;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import animation.AnimMeshLoader;
import animation.AnimatedCharacter;
import animation.Animation;
import animation.Animator;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontRendering.TextController;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import guis.GUIRenderer;
import guis.GUITexture;
import inputs.MousePicker;
import models.BaseModel;
import models.TexturedModel;
import networking.Client;
import networking.PeerClient;
import particles.ParticleGenerator;
import particles.ParticleManager;
import particles.ParticleTexture;
import physics.SAP;
import rendering.AdvancedRenderer;
import rendering.Loader;
import rendering.Window;
import terrains.Terrain;
import texturing.CausticTexture;
import texturing.ModelTexture;
import utils.OBJLoader;
import utils.Utils;
import water.WaterFBO;
import water.WaterPlane;
import water.WaterRenderer;
import water.WaterShader;
import worldData.World;

public class Main {

	public static void main(String[] args) {
		
		/* Configure and connect client */
		Client.port = 8192;
		try {
			Client.serverAddress = InetAddress.getByName("192.168.1.9");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Client.connect();
		Client.send("Connect".getBytes());
		
		// Initialise window
		Window.init();
		
		Loader loader = new Loader();
		
		TextController.init(loader);
		
		FontStyle font = new FontStyle(loader.decodePNGTexture("res/calibri.png", 0), new File("res/calibri.fnt"));
		GUIText text = new GUIText("Test text", 1, font, new Vector2f(0.5f,0.5f), 0.5f, true);
		text.setColour(1, 0, 0);
		
		BaseModel[] testModels = null;
		
		ModelTexture texture = new ModelTexture(loader.loadTexture("res/green.png"));
		
		// Store all animated entities to be rendered
		List<AnimatedCharacter> animatedChars = new ArrayList<>();
		Animation animation = null;
		try {
			animation = AnimMeshLoader.loadAnimation("res/model.dae", loader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		testModels = animation.getModels();
		
		ModelTexture testTex = new ModelTexture(loader.loadTexture("res/Character Texture.png"));
		TexturedModel testtexModel = new TexturedModel(testModels[0], testTex);
		
		ModelTexture terrainTex = new ModelTexture(loader.loadTexture("res/Brown.png"));
		
		terrainTex.setShineDamper(1);
		terrainTex.setReflectivity(0);
		
		texture.setShineDamper(10);
		texture.setReflectivity(1);
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		Terrain terrain = new Terrain(0,0,loader, terrainTex, "heightmap");
		Terrain terrain2 = new Terrain(1,0,loader,terrainTex, "heightmap");
		terrains.add(terrain);
		terrains.add(terrain2);
		
		// Box jumping test
		BaseModel box = OBJLoader.loadObj("cube", loader);
		ModelTexture boxTex = new ModelTexture(loader.loadTexture("res/green.png"));
		TexturedModel boxMod = new TexturedModel(box, boxTex);
		Entity boxEnt = new Entity(boxMod, new Vector3f(200,terrain.getTerrainHeight(200, 200),200),0,0,0,18);
		boxEnt.setStaticModel(true);
		World.addEntity(boxEnt);
		
		Player player = new Player(testtexModel, new Vector3f(100,terrain.getTerrainHeight(100, 90),90),0,0,0,1);
		System.out.println(player.getPosition());
		//animEntity.addEntity(player);
		//client.setPlayer(player);
		
		/* New code for setting up a single animated character */
		AnimatedCharacter animChar = new AnimatedCharacter(player);
		animChar.submitAnimation(AnimatedCharacter.RUN, animation);
		animChar.setCurrentAnimation(AnimatedCharacter.RUN);
		animChar.playCurrentAnimation();
		animatedChars.add(animChar);
		
		Camera camera = new Camera(player);
		
		AdvancedRenderer renderer = new AdvancedRenderer(loader, camera);
		
		ParticleManager.init(loader, renderer.getProjectionMatrix());
		
		BaseModel model = OBJLoader.loadObj("LowPolyTree", loader);
		List<Light> lights = new ArrayList<>();
		Light sun = new Light(new Vector3f(10000,15000,-10000), new Vector3f(1.3f,1.3f,1.3f));
		Light light1 = new Light(new Vector3f(200,10,300), new Vector3f(10,0,0), new Vector3f(1, 0.01f, 0.002f));
		Light light2 = new Light(new Vector3f(200,10,200), new Vector3f(0,0,10), new Vector3f(1, 0.01f, 0.002f));
		lights.add(sun);
		//lights.add(light1);
		//lights.add(light2);
		/*try {
			testModels = StaticModelLoader.load("res/LowPolyTree.obj", loader);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("res/fern.png"));
		fernTextureAtlas.setHasTransparency(true);
		fernTextureAtlas.setUseFakeLighting(true);
		fernTextureAtlas.setNumberOfRows(2);
		
		TexturedModel fern = new TexturedModel(OBJLoader.loadObj("fern", loader), fernTextureAtlas);
		
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for(int i = 0; i < 400; i++)
		{
			if(i % 2 == 0)
			{
				float x = random.nextFloat() * 800 - 400;
				float z = random.nextFloat() * 400;
				float y = terrain.getTerrainHeight(x, z);
				
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),0, random.nextFloat(), 0, 0.9f));
			}
		}
		
		TexturedModel tModel = new TexturedModel(model, texture);
				
		Entity treeEnt = new Entity(tModel, new Vector3f(90, terrain.getTerrainHeight(90, 70), 70), 0, 0, 0,1);
		treeEnt.setClickable(true);
		entities.add(treeEnt);
		treeEnt.setStaticModel(true);
		World.addEntity(treeEnt);
		World.addEntity(player);
		
		entities.add(boxEnt);
		
		/*for(int i = 0; i < 400; i++)
		{
			if(i % 2 == 0)
			{
				float x = random.nextFloat() * 300;
				float z = random.nextFloat() * 400;
				float y = terrain.getTerrainHeight(x, z);
				
				Entity entity = new Entity(tModel, random.nextInt(4), new Vector3f(x,y,z),0, random.nextFloat(), 0, 0.9f);
				entity.setStaticModel(true);
				entities.add(entity);
				World.addEntity(entity);
			}
		}*/
		
		
		List<GUITexture> guis = new ArrayList<GUITexture>();
		GUITexture gui = new GUITexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//guis.add(gui);
		
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		
		WaterFBO waterFBOS = new WaterFBO();
		
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFBOS);
		List<WaterPlane> water = new ArrayList<>();
		WaterPlane waterPlane = new WaterPlane(loader,40,40,-7);
		for(int i = 0; i < waterPlane.getCausticTextures().length; i++)
		{
			int temp = i + 1;
			if(temp / 10 == 0)
			{
				waterPlane.setCausticTextures(i, new CausticTexture(loader.loadTexture("res/test_caustic_00"+temp+".bmp")));
			}else
			{
				waterPlane.setCausticTextures(i, new CausticTexture(loader.loadTexture("res/test_caustic_0"+temp+".bmp")));
			}
		}
		water.add(waterPlane);
		terrain.setCausticTexture(waterPlane.getCausticTextures()[0]);
		terrain2.setCausticTexture(waterPlane.getCausticTextures()[0]);
		
		
		ParticleTexture partTexture = new ParticleTexture(loader.loadTexture("res/cosmic.png"),4, false);
		
		ParticleGenerator partGen = new ParticleGenerator(partTexture,40, 10, 0.1f, 1, 1.6f);
		partGen.setLifeError(0.1f);
		partGen.setSpeedError(0.25f);
		partGen.setScaleError(0.5f);
		partGen.randomizeRotation();
		
		SAP sap = new SAP();
		
		MousePicker picker = new MousePicker(camera,renderer.getProjectionMatrix(),terrain);
		
		Entity testEnt = new Entity(tModel,new Vector3f(100,terrain.getTerrainHeight(100, 90),90),0,0,0,1);
		entities.add(testEnt);
		
		int count = 0;
		while(!Window.closed())
		{
			/*window.clear();*/
			player.movePlayer(terrains, water,entities);
			//waterPlane.update(player);
			
			animChar.getAnimator().update();
			sap.update();
			picker.update(player);
			camera.move();
			
			if(picker.getCurrentTerrainPoint() != null)
			{
				if(picker.getCurrentHoveredEntity() != null)
				{
					picker.getCurrentHoveredEntity().setPosition(picker.getCurrentTerrainPoint());
				}else
				{
					
				}
			}
			
			System.out.println(player.getPosition());
			
			if(!Client.handler.getPeers().isEmpty())
			{
				PeerClient thisClient = Client.handler.getPeers().get(Client.ID);
				testEnt.setPosition(thisClient.getPlayerData().getPosition());
				if(thisClient.getServerRequests().get(0).contains("released"))
				{
					//player.getPosition().x = thisClient.getPlayerData().getPosition().x;
					//player.getPosition().z = thisClient.getPlayerData().getPosition().z;
					//player.setRotX(thisClient.getPlayerData().getRotX());
					//player.setRotY(thisClient.getPlayerData().getRotY());
					//player.setRotZ(thisClient.getPlayerData().getRotZ());
				}
			}
			
			
			partGen.generateParticles(player.getPosition());
			
			ParticleManager.update(camera);
			
			renderer.renderShadowMap(entities, sun);
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
			waterRenderer.render(water, camera, sun, renderer.getNearPlane(), renderer.getFarPlane());
			
			ParticleManager.render(camera);
			
			guiRenderer.render(guis);
			TextController.render();
			Window.update();;
		}
		
		Client.disconnect();
		ParticleManager.cleanUp();
		TextController.cleanUp();
		waterFBOS.cleanUp();
		waterShader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		Window.destroy();

	}

}
