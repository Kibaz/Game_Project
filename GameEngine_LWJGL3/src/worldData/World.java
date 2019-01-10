package worldData;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;

public class World {
	
	public static List<Entity> worldObjects = new ArrayList<>();
	
	
	public static void addEntity(Entity entity)
	{
		worldObjects.add(entity);
	}
	
	public static void removeEntity(Entity entity)
	{
		worldObjects.remove(entity);
	}

}
