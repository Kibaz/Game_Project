package worldData;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import models.TexturedModel;
import rendering.Loader;
import terrains.Terrain;
import texturing.ModelTexture;

public abstract class Zone {
	
	private String name;
	
	protected Loader loader;
	
	public Zone(String name, Loader loader)
	{
		this.name = name;
		this.loader = loader;
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
	
	// Method to create the water in a zone - there may not be a requirment for water
	protected abstract void createWater();
	
	protected Loader getLoader()
	{
		return loader;
	}

}
