package runtime;

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
import animation.AnimatedEntity;
import animation.AnimationLoader;
import buffers.FBO;
import combat.Ability;
import combat.AbilityRenderer;
import combat.ArcIndicator;
import combat.ChainedEffect;
import combat.DOT;
import combat.Effect;
import combat.InstantDamage;
import components.AI;
import components.Collider;
import components.CombatManager;
import components.Controller;
import components.FloatingHealthBar;
import components.HealthBarFrame;
import components.Motion;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontRendering.TextController;
import fontUtils.GUIText;
import guis.GUI;
import guis.GUIRenderer;
import guis.GUITexture;
import guis.HUD;
import guis.HUDRenderer;
import inputs.MousePicker;
import networking.Client;
import particles.ParticleManager;
import physics.SAP;
import postProcessing.PostProcessor;
import rendering.AdvancedRenderer;
import rendering.Loader;
import rendering.Window;
import terrains.Terrain;
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
		List<AnimatedEntity> animatedEntities = new ArrayList<>();
		List<Entity> entities = new ArrayList<>(); // Entities
		List<WaterPlane> water = new ArrayList<>(); // Any water?
		List<GUI> guis = new ArrayList<>(); // Store GUIs
		List<HUD> huds = new ArrayList<>();
		List<Ability> abilities = new ArrayList<>();
		
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
		
		GUITexture testButtonTexture = new GUITexture(loader.loadTexture("res/test_button.png"),new Vector2f(0,-0.9f),new Vector2f(0.05f,0.05f));
		GUI testButton = new GUI(testButtonTexture);
		testButton.setClickable(true);
		guis.add(testButton);
		
		AnimatedEntity testAnimEnt = AnimationLoader.loadAnimatedFile("res/model.dae","res/Character Texture.png",new Vector3f(100,
				tigranStartZone.getTerrains().get(0).getTerrainHeight(100, 90),90),90,0,0,1,loader);
		
		Player player = new Player(loader,testAnimEnt.getModel(), new Vector3f(100,tigranStartZone.getTerrains().get(0).getTerrainHeight(100, 90),90),0,0,0,1);
		player.setBones(testAnimEnt.getBones());
		player.getAABB().setRotation(-90, 0, 0);
		player.getEllipsoid().setRotation(-90, 0, 0);
		player.setJointTransforms(testAnimEnt.getJointTransforms());
		player.setGlobalTransformationMatrix(testAnimEnt.getGlobalTransformationMatrix());
		player.setAnimations(testAnimEnt.getAnimations());
		player.setCurrentAnimation("");
		player.setClickable(true);
		animatedEntities.add(player);
		guis.addAll(player.getUIFrame().getGuis());
		for(GUIText text: player.getUIFrame().getTextGuis())
		{
			TextController.loadText(text);
		}
		
		Entity test = new Entity(testAnimEnt.getModel(), new Vector3f(100,tigranStartZone.getTerrains().get(0).getTerrainHeight(100, 90),90),0,0,0,1);
		test.setStaticModel(false);
		Motion motion = new Motion();
		motion.setRunSpeed(20);
		motion.setSwimSpeed(10);
		motion.setWalkSpeed(10);
		Controller controller = new Controller("player_controller");
		test.addComponent(motion);
		test.addComponent(controller);
		test.addComponent(new Collider("collider",terrains));
		test.getComponentByType(Collider.class).start();
		
		entities.add(test);
		World.addEntity(test);
		
		Entity cubeMob = new Entity(testAnimEnt.getModel(),new Vector3f(50,
				tigranStartZone.getTerrains().get(0).getTerrainHeight(50, 50),50),-90,0,0,1);
		cubeMob.getAABB().setRotation(-90, 0, 0);
		cubeMob.setClickable(true);
		HealthBarFrame mobHealthFrame = new HealthBarFrame("npc_health_frame",new Vector2f(-0.3f,0.9f),new Vector2f(0.22f,0.035f), new Vector2f(-0.1475f,0.035f));
		FloatingHealthBar floatingHealthBar = new FloatingHealthBar("npc_floating_health");
		List<Entity> mobEnemies = new ArrayList<>();
		mobEnemies.add(player);
		AI mobAI = new AI("mob_ai",entities,terrains,mobEnemies);
		mobAI.setAggroRange(50);
		mobAI.setAvoidanceForce(0.3f);
		mobAI.setWanderRadius(50);
		mobAI.setSlowingRadius(7);
		mobAI.setSteerForce(0.1f);
		cubeMob.addComponent(mobHealthFrame);
		cubeMob.addComponent(floatingHealthBar);
		cubeMob.addComponent(mobAI);
		cubeMob.addComponent(new CombatManager("mob_combat_manager"));
		huds.add(floatingHealthBar.getHealthFrame());
		huds.add(floatingHealthBar.getHealthPool());
		guis.add(mobHealthFrame.getHealthFrame());
		guis.add(mobHealthFrame.getHealthPool());
		entities.add(cubeMob);
		World.addEntity(cubeMob);
		
		cubeMob.getComponentByType(HealthBarFrame.class).setMaxHealth(100);
		cubeMob.getComponentByType(HealthBarFrame.class).setHealth(100);
		cubeMob.getComponentByType(HealthBarFrame.class).setLevel(1);
		cubeMob.getComponentByType(HealthBarFrame.class).setVisible(false);
		cubeMob.getComponentByType(AI.class).start();
		TextController.loadText(cubeMob.getComponentByType(HealthBarFrame.class).getHealthInfo());
		TextController.loadText(cubeMob.getComponentByType(HealthBarFrame.class).getLevelInfo());
		
		//guis.add(player.getHealthBar());
		Client.setPreviousPlayerPosition(player.getPosition());
		World.addEntity(player);
		
		Client.listen();
		Client.send("Requesting connection".getBytes());
		
		// Set up player camera - 3rd person camera
		Camera camera = new Camera(player);
		
		AdvancedRenderer renderer = new AdvancedRenderer(loader, camera);
		
		HUDRenderer hudRenderer = new HUDRenderer(loader,renderer.getProjectionMatrix());
		
		ParticleManager.init(loader, renderer.getProjectionMatrix());
		
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		
		WaterFBO waterFBOS = new WaterFBO();
		
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFBOS);
		Entity testEnt = new Entity(testAnimEnt.getModel(), new Vector3f(100,tigranStartZone.getTerrains().get(0).getTerrainHeight(100, 90),90),-90,0,0,1);
		entities.add(testEnt);
		
		animatedEntities.add(testAnimEnt);
		
		// Initialising physics
		SAP sap = new SAP();
		
		MousePicker picker = new MousePicker(camera,renderer.getProjectionMatrix(),tigranStartZone.getTerrains().get(0));
		
		
		FBO multiSampleFbo = new FBO(Window.getWidth(),Window.getHeight());
		FBO outFbo = new FBO(Window.getWidth(),Window.getHeight(),FBO.DEPTH_TEXTURE);
		
		PostProcessor.init(loader);
		
		AbilityRenderer abilityRenderer = new AbilityRenderer(loader,renderer.getProjectionMatrix());
		List<Effect> chainedEffects = new ArrayList<>();
		chainedEffects.add(new InstantDamage(13,6));
		DOT testPoision = new DOT(5,1,3,5);
		chainedEffects.add(testPoision);
		ChainedEffect testChainedEffect = new ChainedEffect(chainedEffects);
		
		ArcIndicator arcIndicator = new ArcIndicator(player.getPosition(),0,15,20,15,120);
		arcIndicator.buildIndicator(loader);
		Ability testAbility = new Ability(testButton,"Slash","test",arcIndicator,testChainedEffect,3,Ability.Type.INSTANT);
		
		abilities.add(testAbility);
		
		while(!Window.closed())
		{
			/*window.clear();*/
			Client.sendInputs();
			// Testing client side prediction
			player.movePlayer(terrains, water, entities);
			arcIndicator.setPosition(player.getPosition());
			arcIndicator.setRotY(player.getRotY());
			testAbility.update();
			cubeMob.update();
			test.update();
			
			for(Ability ability: abilities)
			{
				ability.update();
			}
			
			//waterPlane.update(player);
			if(Client.getCurrentPlayerPosition() != null)
			{
				testEnt.setPosition(Client.getCurrentPlayerPosition());
			}
			
			player.update();
			
			picker.update(player,entities,guis,abilities);
			
			if(picker.getCurrentHoveredEntity() != null)
			{
				HealthBarFrame healthFrame = picker.getCurrentHoveredEntity().getComponentByType(HealthBarFrame.class);
				if(healthFrame != null)
				{
					healthFrame.setVisible(true);
				}
			}
			
			if(picker.getPreviousHoveredEntity() != null)
			{
				HealthBarFrame healthFrame = picker.getPreviousHoveredEntity().getComponentByType(HealthBarFrame.class);
				if(healthFrame != null)
				{
					healthFrame.setVisible(false);
				}
			}
			
			
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
				renderer.renderScene(entities, terrains, animatedEntities, lights, camera, new Vector4f(0,-1,0,water.get(0).getHeight()+0.3f));
				camera.getPosition().y += distance;
				camera.invertPitch();
				waterFBOS.unbindFrameBuffer();
				
				// render refraction texture
				waterFBOS.bindRefractionBuffer();
				renderer.renderScene(entities, terrains, animatedEntities, lights, camera, new Vector4f(0,1,0,-water.get(0).getHeight() + 1f));
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
				renderer.renderScene(entities, terrains,  animatedEntities, lights, camera, new Vector4f(0,1,0,-water.get(0).getHeight()+0.3f));
				camera.getPosition().y += distance;
				camera.invertPitch();
				waterFBOS.unbindFrameBuffer();
				
				// render refraction texture
				waterFBOS.bindRefractionBuffer();
				renderer.renderScene(entities, terrains, animatedEntities, lights, camera, new Vector4f(0,-1,0,water.get(0).getHeight() + 1f));
				waterFBOS.unbindFrameBuffer();
			}
			
			// render to the display, i.e default frame buffer
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			
			multiSampleFbo.bindFrameBuffer();
			renderer.renderScene(entities, terrains, animatedEntities, lights, camera, new Vector4f(0,-1,0,0));
			waterRenderer.render(water, camera, tigranStartZone.getSun(), renderer.getNearPlane(), renderer.getFarPlane());
			multiSampleFbo.unbindFrameBuffer();
			multiSampleFbo.resolveToFBO(GL30.GL_COLOR_ATTACHMENT0,outFbo);
			PostProcessor.handlePostProcessing(outFbo.getColourTexture());
			ParticleManager.render(camera);
			
			
			abilityRenderer.render(abilities, camera);
			guiRenderer.render(guis);
			hudRenderer.render(huds, camera);
			
			TextController.render();
			Window.update();;
		}
		
		// Clean up all rendering, processing etc...
		multiSampleFbo.cleanUp();
		outFbo.cleanUp();
		waterFBOS.cleanUp();
		waterShader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		PostProcessor.cleanUp();
		Window.destroy();
		Client.disconnect();
	}
}
