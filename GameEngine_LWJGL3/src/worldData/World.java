package worldData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Entity;
import terrains.Terrain;

public class World {
	
	public static List<Entity> worldObjects = new ArrayList<>();
	public static List<Terrain> worldTerrains = new ArrayList<>();
	public static Map<String,Zone> zones = new HashMap<>();
	
	public static void addEntity(Entity entity)
	{
		worldObjects.add(entity);
	}
	
	public static void removeEntity(Entity entity)
	{
		worldObjects.remove(entity);
	}
	
	public static void addZone(String name, Zone zone)
	{
		zones.put(name, zone);
	}
	
	public static void addTerrain(Terrain terrain)
	{
		worldTerrains.add(terrain);
	}

}
