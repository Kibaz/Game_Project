package runtime;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import animation.AnimationLoader;
import buffers.FBO;
import combat.Ability;
import combat.AbilityRenderer;
import combat.ArcIndicator;
import combat.ChainedEffect;
import combat.DOT;
import combat.Effect;
import combat.InstantDamage;
import components.AnimationComponent;
import components.CombatManager;
import components.EntityInformation;
import components.EntityProfile;
import components.HealthBarFrame;
import components.Motion;
import components.ProgressBar;
import components.QuestInterface;
import components.QuestTracker;
import entities.Camera;
import entities.Entity;
import entities.Light;
import fontRendering.TextController;
import guis.GUI;
import guis.GUIRenderer;
import guis.GUITexture;
import guis.HUD;
import guis.HUDRenderer;
import inputs.MousePicker;
import interfaceObjects.Quest;
import models.TexturedModel;
import networking.Client;
import networking.PeerClient;
import networking.UDPClient;
import objectives.Enumeration;
import objectives.Task;
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
	
	public static TexturedModel testModel;

	public static void main(String[] args) throws UnknownHostException, SocketException {
		
		// Initialise lists of terrains, entities, animated characters, lights etc...
		List<Terrain> terrains = new ArrayList<>(); // Terrains
		List<Light> lights = new ArrayList<>(); // Lights
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
		
		// Initialise window
		Window.init();
		
		// Initialise Font Handler
		TextController.init(loader);
		
		QuestTracker.init();
		
		// Initialise and create zones
		// Add game objects stored in each zone to prepare for rendering
		TigranStartZone tigranStartZone = new TigranStartZone(loader);
		terrains.addAll(tigranStartZone.getTerrains());
		lights.addAll(tigranStartZone.getLights());
		entities.addAll(tigranStartZone.getEntities());
		water.addAll(tigranStartZone.getWater());
		World.addZone(tigranStartZone.getName(), tigranStartZone);
		
		GUITexture testButtonTexture = new GUITexture(loader.loadTexture("res/test_button.png"),new Vector2f(0,-0.85f),new Vector2f(0.05f,0.05f));
		GUI testButton = new GUI(testButtonTexture);
		testButton.setClickable(true);
		guis.add(testButton);
		
		HealthBarFrame playerHealthFrame = new HealthBarFrame("player_health_frame",new Vector2f(-0.75f,0.9f),new Vector2f(0.0125f,0.035f), new Vector2f(-0.375f,0.035f));
		playerHealthFrame.setVisible(true);
		Entity player = AnimationLoader.loadAnimatedFile("res/model.dae","res/Character Texture.png",new Vector3f(100,
				tigranStartZone.getTerrains().get(0).getTerrainHeight(100, 90),90),0,0,0,1,loader);
		player.getComponentByType(AnimationComponent.class).setCurrentAnimation("");
		/*player.addComponent(new Motion());
		player.getComponentByType(Motion.class).setRunSpeed(20);
		player.getComponentByType(Motion.class).setWalkSpeed(10);
		player.addComponent(new Collider("collider",terrains));
		player.getComponentByType(Collider.class).start();*/
		
		// Handler networking initialisation
		Client client = new Client(player,"127.0.0.1",8061,8060);
		client.connect();
		client.listen();
		
		player.addComponent(new Motion());
		player.getComponentByType(Motion.class).setRunSpeed(20);
		player.getComponentByType(Motion.class).setWalkSpeed(10);
		//player.addComponent(new Controller("controller",client));
		player.addComponent(new EntityInformation("Player",1,100,100));
		player.addComponent(playerHealthFrame);
		player.addComponent(new CombatManager("combat_manager"));
		player.addComponent(new ProgressBar("exp_bar",100,0,new Vector2f(0,-0.95f),new Vector2f(0,0.96f)));
		guis.add(player.getComponentByType(ProgressBar.class).getFrame());
		guis.add(player.getComponentByType(ProgressBar.class).getPool());
		guis.add(playerHealthFrame.getHealthFrame());
		guis.add(playerHealthFrame.getHealthPool());
		entities.add(player);
		World.addEntity(player);
		
		Entity testQuestGuy = new Entity(player.getModel(),new Vector3f(150,
				tigranStartZone.getTerrains().get(0).getTerrainHeight(150, 150),150),-90,0,0,1);
		testQuestGuy.getAABB().setRotation(-90, 0, 0);
		testQuestGuy.getAABB().resetBox(testQuestGuy.getPosition());
		EntityInformation questGuyInfo = new EntityInformation("Quest Guy",5,450,450);
		testQuestGuy.addComponent(questGuyInfo);
		testQuestGuy.addComponent(new HealthBarFrame("npc_health_frame",new Vector2f(-0.3f,0.9f),new Vector2f(0.22f,0.035f), new Vector2f(-0.1475f,0.035f)));
		String questDescription = "Greetings, weary traveller! I am in need of serious help!"
				+ " A number of cowboy enthusiasts have made their way onto these lands and"
				+ " i must have the place cleansed of them at once. I used to be the grand"
				+ " protector of these lands, however, since the great struggle that set "
				+ " our kind in ruin and dispair, I am yet to recover to my full strength."
				+ " Please aid me! I will be sure to reward you appropriately for your efforts.";
		String questSummary = "Quest Guy has requested that you lay waste to the cowboy"
				+ " infestation over yonder. Slay a total of 4 cowboys and return to him"
				+ " when the deed has been done!";
		Quest testQuest = new Quest("Cowboy Massacre",questDescription,questSummary, new Enumeration("Test Mob",4,Task.PURGE));
		testQuest.setLevelRequirement(1);
		testQuestGuy.addComponent(new QuestInterface(testQuest));
		guis.add(testQuestGuy.getComponentByType(QuestInterface.class).getQuestLog());
		testQuestGuy.addComponent(new EntityProfile());
		guis.add(testQuestGuy.getComponentByType(EntityProfile.class).getProfileDisplay());
		guis.add(testQuestGuy.getComponentByType(HealthBarFrame.class).getHealthFrame());
		guis.add(testQuestGuy.getComponentByType(HealthBarFrame.class).getHealthPool());
		guis.add(testQuestGuy.getComponentByType(QuestInterface.class).getAcceptButton());
		guis.add(testQuestGuy.getComponentByType(QuestInterface.class).getDeclineButton());
		guis.add(testQuestGuy.getComponentByType(QuestInterface.class).getCompleteButton());
		testQuestGuy.setClickable(true);
		entities.add(testQuestGuy);
		World.addEntity(testQuestGuy);
		
		List<Effect> chainedEffects = new ArrayList<>();
		chainedEffects.add(new InstantDamage(100,100));
		DOT testPoision = new DOT(5,1,3,5);
		chainedEffects.add(testPoision);
		ChainedEffect testChainedEffect = new ChainedEffect(chainedEffects);
		
		ArcIndicator arcIndicator = new ArcIndicator(player.getPosition(),0,15,20,15,120);
		arcIndicator.buildIndicator(loader);
		Ability testAbility = new Ability(testButton,"Slash","test",arcIndicator,testChainedEffect,3,Ability.Type.INSTANT);
		
		abilities.add(testAbility);
		
		// Set up player camera - 3rd person camera
		Camera camera = new Camera(player);
		
		AdvancedRenderer renderer = new AdvancedRenderer(loader, camera);
		
		HUDRenderer hudRenderer = new HUDRenderer(loader,renderer.getProjectionMatrix());
		
		ParticleManager.init(loader, renderer.getProjectionMatrix());
		
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		
		WaterFBO waterFBOS = new WaterFBO();
		
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFBOS);
		Entity testEnt = new Entity(player.getModel(), new Vector3f(100,tigranStartZone.getTerrains().get(0).getTerrainHeight(100, 90),90),-90,0,0,1);
		entities.add(testEnt);
		
		// Initialising physics
		SAP sap = new SAP();
		
		MousePicker picker = new MousePicker(camera,renderer.getProjectionMatrix(),tigranStartZone.getTerrains().get(0));
		
		
		FBO multiSampleFbo = new FBO(Window.getWidth(),Window.getHeight());
		FBO outFbo = new FBO(Window.getWidth(),Window.getHeight(),FBO.DEPTH_TEXTURE);
		
		PostProcessor.init(loader);
		
		AbilityRenderer abilityRenderer = new AbilityRenderer(loader,renderer.getProjectionMatrix());
		
		while(!Window.closed())
		{
			/*window.clear();*/
			client.update(terrains);
			
			for(UUID clientID: Client.getPeerClients().keySet())
			{
				PeerClient current = Client.getPeerByID(clientID);
				current.update();
				if(!entities.contains(current.getEntity()))
				{
					entities.add(current.getEntity());
				}
			}

			arcIndicator.setPosition(player.getPosition());
			arcIndicator.setRotY(player.getRotY());
			testAbility.update();
			
			for(Entity entity: entities)
			{
				entity.update();
			}
			
			for(Ability ability: abilities)
			{
				ability.update();
			}
			
			picker.update(player,entities,guis,abilities);
			
			if(picker.getCurrentHoveredEntity() != null)
			{
				HealthBarFrame healthFrame = picker.getCurrentHoveredEntity().getComponentByType(HealthBarFrame.class);
				QuestInterface questInterface = picker.getCurrentHoveredEntity().getComponentByType(QuestInterface.class);
				if(questInterface != null) questInterface.setVisible(true);
				if(healthFrame != null) healthFrame.setVisible(true);
			}
			
			if(picker.getPreviousHoveredEntity() != null)
			{
				HealthBarFrame healthFrame = picker.getPreviousHoveredEntity().getComponentByType(HealthBarFrame.class);
				QuestInterface questInterface = picker.getPreviousHoveredEntity().getComponentByType(QuestInterface.class);
				if(healthFrame != null) healthFrame.setVisible(false);
				if(questInterface != null) questInterface.setVisible(false);
			}
			
			
			// Carry out physics engine
			sap.update();
			
			QuestTracker.update(player);
			
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
				renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0,-1,0,water.get(0).getHeight()+0.3f));
				camera.getPosition().y += distance;
				camera.invertPitch();
				waterFBOS.unbindFrameBuffer();
				
				// render refraction texture
				waterFBOS.bindRefractionBuffer();
				renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0,1,0,-water.get(0).getHeight() + 1f));
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
				renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0,1,0,-water.get(0).getHeight()+0.3f));
				camera.getPosition().y += distance;
				camera.invertPitch();
				waterFBOS.unbindFrameBuffer();
				
				// render refraction texture
				waterFBOS.bindRefractionBuffer();
				renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0,-1,0,water.get(0).getHeight() + 1f));
				waterFBOS.unbindFrameBuffer();
			}
			
			// render to the display, i.e default frame buffer
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			
			multiSampleFbo.bindFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0,-1,0,0));
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
		for(Entity entity: entities)
		{
			entity.cleanUpComponents();
		}
		PostProcessor.cleanUp();
		Window.destroy();
		client.end();
	}
}
