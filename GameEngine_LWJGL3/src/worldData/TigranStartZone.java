package worldData;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import inventory.Item;
import models.BaseModel;
import models.TexturedModel;
import professions.Mineral;
import professions.MiningNode;
import rendering.Loader;
import terrains.Terrain;
import texturing.CausticTexture;
import texturing.ModelTexture;
import utils.OBJLoader;
import utils.StaticModelLoader;
import water.WaterPlane;

public class TigranStartZone extends Zone{
	
	private BaseModel tree;
	private BaseModel box;
	private BaseModel fern;
	
	private Light sun;

	public TigranStartZone(Loader loader) {
		super("Ti'Thar Thul Jungle", loader);
	}
	
	// Getters and Setters
	public Light getSun() {
		return sun;
	}

	// Inherited methods
	@Override
	protected void createTerrains() {
		ModelTexture terrainTex = new ModelTexture(super.getLoader().loadTexture("res/Brown.png"));
		terrainTex.setShineDamper(1);
		terrainTex.setReflectivity(0);
		Terrain terrain1 = new Terrain(0, 0,loader,terrainTex, "heightmap");
		Terrain terrain2 = new Terrain(1, 0,loader,terrainTex, "heightmap");
		super.getTerrains().add(terrain1);
		super.getTerrains().add(terrain2);
		World.addTerrain(terrain1);
		World.addTerrain(terrain2);
	}

	@Override
	protected void createStaticEntities() {
		// Load Base Models
		tree = OBJLoader.loadObj("LowPolyTree", loader);
		box = OBJLoader.loadObj("cube", loader);
		fern = OBJLoader.loadObj("fern", loader);
		
		
		BaseModel slope = OBJLoader.loadObj("slope", loader);
		
		// Load Textures
		ModelTexture treeTex = new ModelTexture(loader.loadTexture("res/green.png"));
		ModelTexture fernTex = new ModelTexture(loader.loadTexture("res/fern.png"));
		fernTex.setHasTransparency(true);
		fernTex.setUseFakeLighting(true);
		fernTex.setNumberOfRows(2);
		
		// Create Textured Models
		TexturedModel treeTexModel = new TexturedModel(tree,treeTex);
		TexturedModel fernTexModel = new TexturedModel(fern, fernTex);
		TexturedModel boxTexModel = new TexturedModel(box,treeTex);
		TexturedModel slopeTextModel = new TexturedModel(slope,treeTex);
		
		// Create Entities
		Entity treeEnt = new Entity(treeTexModel, new Vector3f(90, super.getTerrains().get(0).getTerrainHeight(90, 70), 70), 0, 0, 0, 1);
		treeEnt.setStaticModel(true);
		
		Entity boxEnt = new Entity(boxTexModel,new Vector3f(200, super.getTerrains().get(0).getTerrainHeight(200, 200),200),0,0,0,18);
		boxEnt.setStaticModel(true);
		
		Entity slopeEnt = new Entity(slopeTextModel,new Vector3f(250,super.getTerrains().get(0).getTerrainHeight(250, 210),210),0,0,0,1);
		slopeEnt.setStaticModel(true);
		
		Random random = new Random();
		for(int i = 0; i < 400; i++)
		{
			float x = random.nextFloat() * 800 - 400;
			float z = random.nextFloat() * 400;
			float y = super.getTerrains().get(0).getTerrainHeight(x, z);
			
			Entity fernEnt = new Entity(fernTexModel, random.nextInt(4), new Vector3f(x,y,z),0,random.nextFloat(),0,0.9f);
			super.getEntities().add(fernEnt);
		}
		
		BaseModel[] copperVeinModels = StaticModelLoader.load("res/copper_vein.obj", loader);
		TexturedModel copperVeinTexModel = new TexturedModel(copperVeinModels[0],new ModelTexture(loader.loadTexture("res/copper_vein.png")));
		Entity copperVein = new Entity(copperVeinTexModel,new Vector3f(230,super.getTerrains().get(0).getTerrainHeight(230, 120),120),0,0,0,1);
		copperVein.setStaticModel(true);
		MiningNode copperNode = new MiningNode("Copper Vein","Copper",Mineral.MAX_CAPACITY);
		copperVein.addComponent(copperNode);
		
		for(Item ore: copperNode.getOres())
		{
			super.getEntities().add(ore.getEntity());
			World.addEntity(ore.getEntity());
		}
		
		super.getEntities().add(copperVein);
		super.getEntities().add(treeEnt);
		super.getEntities().add(boxEnt);
		super.getEntities().add(slopeEnt);
		World.addEntity(treeEnt);
		World.addEntity(boxEnt);
		World.addEntity(slopeEnt);
		World.addEntity(copperVein);
	}

	@Override
	protected void createLights() {
		sun = new Light(new Vector3f(10000,15000,-10000), new Vector3f(1.3f,1.3f,1.3f));
		super.getLights().add(sun);
	}

	@Override
	protected void createWater() {
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
		super.getWater().add(waterPlane);
		super.getTerrains().get(0).setCausticTexture(waterPlane.getCausticTextures()[0]);
		super.getTerrains().get(1).setCausticTexture(waterPlane.getCausticTextures()[0]);
	}


	
	
	

}
