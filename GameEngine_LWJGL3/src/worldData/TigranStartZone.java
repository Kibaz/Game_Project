package worldData;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import entities.TestMob;
import models.BaseModel;
import models.TexturedModel;
import rendering.Loader;
import terrains.Terrain;
import texturing.CausticTexture;
import texturing.ModelTexture;
import utils.OBJLoader;
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
		super.getTerrains().add(new Terrain(0, 0,loader,terrainTex, "heightmap"));
		super.getTerrains().add(new Terrain(1, 0,loader,terrainTex, "heightmap"));
	}

	@Override
	protected void createStaticEntities() {
		// Load Base Models
		tree = OBJLoader.loadObj("LowPolyTree", loader);
		box = OBJLoader.loadObj("cube", loader);
		fern = OBJLoader.loadObj("fern", loader);
		
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
		
		// Create Entities
		Entity treeEnt = new Entity(treeTexModel, new Vector3f(90, super.getTerrains().get(0).getTerrainHeight(90, 70), 70), 0, 0, 0, 1);
		treeEnt.setStaticModel(true);
		
		Entity boxEnt = new Entity(boxTexModel,new Vector3f(200, super.getTerrains().get(0).getTerrainHeight(200, 200),200),0,0,0,18);
		boxEnt.setStaticModel(true);
		
		TestMob test = new TestMob(loader,boxTexModel,new Vector3f(70, super.getTerrains().get(0).getTerrainHeight(70, 90),90),0,0,0,2);
		test.setClickable(true);
		
		
		Random random = new Random();
		for(int i = 0; i < 400; i++)
		{
			float x = random.nextFloat() * 800 - 400;
			float z = random.nextFloat() * 400;
			float y = super.getTerrains().get(0).getTerrainHeight(x, z);
			
			super.getEntities().add(new Entity(fernTexModel, random.nextInt(4), new Vector3f(x,y,z),0,random.nextFloat(),0,0.9f));
		}
		
		super.getEntities().add(treeEnt);
		super.getEntities().add(boxEnt);
		super.getEntities().add(test);
		World.addEntity(treeEnt);
		World.addEntity(boxEnt);
		
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
