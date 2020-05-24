package professions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import inventory.Item;
import rendering.Window;
import terrains.Terrain;
import utils.Maths;
import worldData.World;

public class MiningNode extends InteractiveNode {
	
	private final float ORE_ANIM_TIME = 1.5f;
	
	private final float ORE_RISING_TIME = 0.3f;
	
	private final float DROP_RADIUS = 1;
	
	private final float RISE_THRESHOLD = 5;
	
	private float time = 0; // Timer for ore extraction animation
	
	private Item[] ores;
	
	private int lastCount;
	
	private Random random;
	
	private boolean removing;
	
	private Item minedOre;
	
	private Vector3f dropPosition;
	private Vector3f dropRotation;
	private Vector3f origin;
	
	private float riseValue;

	public MiningNode(String title,String nodeName,int stages) {
		super(title,nodeName,stages);
		lastCount = this.count;
		random = new Random();
		removing = false;
	}
	
	@Override
	public void init()
	{
		super.init();
		initOres();
	}
	
	@Override
	public void update()
	{
		super.update();
		removeOreEvent();
	}
	
	private void removeOreEvent()
	{
		if(lastCount != count)
		{
			minedOre = ores[count]; // Take ore off the top
			dropPosition = calculateDropPosition();
			dropRotation = new Vector3f(180,0,0);
			origin = new Vector3f(minedOre.getEntity().getPosition());
			riseValue = 10 + random.nextFloat() * (RISE_THRESHOLD - 10);
			removing = true;
		}
		
		if(removing)
		{
			time += Window.getFrameTime();
			if(time > ORE_ANIM_TIME)
			{
				// Remove an ore from the node
				removeOre();
			}
			else
			{
				animateOre();
			}
			
			
		}
		
		lastCount = count;
	}
	
	private void removeOre()
	{
		minedOre.getEntity().setClickable(true); // Allow ore to be picked up
		minedOre.getEntity().setPosition(dropPosition);
		minedOre.getEntity().setRotX(dropRotation.x);
		minedOre.getEntity().setRotY(dropRotation.y);
		minedOre.getEntity().setRotZ(dropRotation.z);
		minedOre.getEntity().getAABB().resetBox(dropPosition);
		minedOre = null;
		dropPosition = new Vector3f(0,0,0);
		dropRotation = new Vector3f(0,0,0);
		removing = false;
		time = 0;
	}
	
	private void animateOre()
	{
		float progression = time/ORE_ANIM_TIME;
		
		Entity oreEntity = minedOre.getEntity();
		Vector3f rotation = Maths.interpolate(new Vector3f(oreEntity.getRotX(),
				oreEntity.getRotY(),oreEntity.getRotZ()), dropRotation, progression);

		
		Vector3f position = new Vector3f();
		if(time < ORE_RISING_TIME)
		{
			position.x = dropPosition.x;
			position.y = origin.y + riseValue;
			position.z = dropPosition.z;
			position = Maths.interpolate(oreEntity.getPosition(),position,progression);
		}
		else
		{
			position = Maths.interpolate(oreEntity.getPosition(),dropPosition,progression);
		}
		
		oreEntity.setPosition(position);
		oreEntity.setRotX(rotation.x);
		oreEntity.setRotY(rotation.y);
		oreEntity.setRotZ(rotation.z);
		

	}
	
	private Vector3f calculateDropPosition()
	{
		float areaRadius = entity.getModelWidth() + DROP_RADIUS;
		float angle = (float) (Math.random() * Math.PI * 2);
		float r = (float) (areaRadius * Math.sqrt(angle));
		float x = ((float) Math.sin(angle) * r) + entity.getPosition().x;
		float z = ((float) Math.cos(angle) * r) + entity.getPosition().z;
		Terrain terrain = null;
		for(Terrain t: World.worldTerrains)
		{
			if(t.isEntityOnTerrain(entity))
			{
				terrain = t;
			}
		}
		return new Vector3f(x,terrain.getTerrainHeight(x, z),z);
	}
	
	private void initOres()
	{
		ores = new Item[stages];
		List<Integer> excludes = new ArrayList<>();
		for(int i = 0; i < stages; i++)
		{
			Mineral mineral = NodeBank.getMineralByName(nodeName);
			Vector3f position = new Vector3f();
			int offsetIndex = getRandomNumber(0,Mineral.MAX_CAPACITY,excludes);
			excludes.add(offsetIndex);
			Vector3f offsetPosition = mineral.getPositionOffsets()[offsetIndex];
			Vector3f offsetRotation = mineral.getRotationOffsets()[offsetIndex];
			position.x = entity.getPosition().x + offsetPosition.x;
			position.y = entity.getPosition().y + offsetPosition.y;
			position.z = entity.getPosition().z + offsetPosition.z;
			float rotX = entity.getRotX() + offsetRotation.x;
			float rotY = entity.getRotY() + offsetRotation.y;
			float rotZ = entity.getRotZ() + offsetRotation.z;
			Entity oreEntity = new Entity(mineral.getModel(),position,rotX,rotY,rotZ,1);
			Item ore = new Item("Copper Ore",mineral.getIconTexture());
			ore.setStackLimit(10);
			oreEntity.addComponent(ore);
			ores[i] = ore;
			World.addEntity(oreEntity);
		}
	}
	
	private int getRandomNumber(int start, int end,List<Integer> exclude)
	{
		int number = random.nextInt(end - start - exclude.size());
		for(int i = 0; i < exclude.size(); i++)
		{
			if(!exclude.contains(number))
			{
				break;
			}
			number++;
		}
		
		return number;
	}

	public Item[] getOres() {
		return ores;
	}
	
	

}
