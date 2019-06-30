package worldData;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import entities.Light;
import rendering.Loader;
import terrains.Terrain;
import water.WaterPlane;
public abstract class Zone {
	
	private String name;
	private List<Terrain> terrains;
	private List<Entity> entities;
	private List<Light> lights;
	private List<WaterPlane> water;
	
	protected Loader loader;
	
	public Zone(String name, Loader loader)
	{
		this.name = name;
		this.loader = loader;
		terrains = new ArrayList<>();
		entities = new ArrayList<>();
		lights = new ArrayList<>();
		water = new ArrayList<>();
		createTerrains();
		createWater();
		createStaticEntities();
		createLights();
	}
	
	// Create all terrains
	protected abstract void createTerrains();
	
	// Create all static entities
	protected abstract void createStaticEntities();
	
	// Create all lights for the zone
	protected abstract void createLights();
	
	// Method to create the water in a zone - there may not be a requirement for water
	protected abstract void createWater();
	
	protected Loader getLoader()
	{
		return loader;
	}
	
	public String getName()
	{
		return name;
	}

	public List<Terrain> getTerrains() {
		return terrains;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public List<Light> getLights() {
		return lights;
	}

	public List<WaterPlane> getWater() {
		return water;
	}
	
	// Other methods
	
	// Check whether the player is in the specified zone
	public boolean isPlayerInZone(Entity player)
	{
		for(Terrain terrain: terrains)
		{
			if(terrain.isEntityOnTerrain(player))
			{
				return true;
			}
		}
		
		return false;
	}

}
