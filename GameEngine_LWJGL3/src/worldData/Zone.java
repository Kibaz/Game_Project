package worldData;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import entities.Entity;
import terrains.Terrain;

public class Zone {
	
	private List<Terrain> terrains;
	private List<Entity> staticEntities;
	
	private String name;
	
	private Vector2f dimensions;
	
	public Zone(String name)
	{
		this.name = name;
	}

}
