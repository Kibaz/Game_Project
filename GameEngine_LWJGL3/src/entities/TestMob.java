package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import guis.GUITexture;
import models.TexturedModel;
import pathfinding.GridSquare;
import rendering.Loader;
import rendering.Window;
import terrains.Terrain;

public class TestMob extends Entity {
	
	private final float MAX_HEALTH = 100;
	private final float AGGRO_RANGE = 15;
	private final float RUN_SPEED = 25;
	
	private float currentSpeed = 0;
	
	private float health;
	
	private boolean alive;
	private boolean attacked;
	private boolean hostile;
	
	private float attackTime = 0;
	private float timeBetweenAttacks = 1;
	// User Interface
	private GUITexture healthBar; 
	private GUITexture mobUI;

	public TestMob(Loader loader, TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		health = MAX_HEALTH;
		healthBar = new GUITexture(loader.loadTexture("res/green_health_test.png"), new Vector2f(-0.5f, 0.85f), new Vector2f(0.125f, 0.02f));
		mobUI = new GUITexture(loader.loadTexture("res/test_char_ui.png"), new Vector2f(-0.5f, 0.85f), new Vector2f(0.25f, 0.25f));
		alive = true;
		attacked = false;
		hostile = true;
	}
	
	public void update(Player player, List<Terrain> terrains)
	{
		if(hostile)
		{
			if(isPlayerInAggroRange(player))
			{
				// Move entity to the player's location
				trackPlayer(player,terrains);
				attack(player);
			}
		}else
		{
			if(attacked)
			{
				// Attack back
				attack(player);
			}
			
			if(this.getHealth() <= 0)
			{
				attacked = false; // No longer attacked as dead
				alive = false; // Dead creature
			}
		}
	}
	
	private void attack(Player player)
	{
		// Put player in comabt
		player.setCombatStatus(true);
		player.setCurrentSelectedEntity(this);
		// Deal within damage range
		Random random = new Random();
		int damage = random.nextInt((11-6)+1)+6;
		if(attackTime == 0)
		{
			// Attack the player
			if(player.getHealth() > 0 && this.getHealth() > 0)
			{
				player.setHealth(player.getHealth() - damage);
				// Calculate and animate health-bar
				float originX = player.getHealthBar().getScale().x;
				player.getHealthBar().getScale().x = (player.getHealth() / player.getMaxHealth()) * 0.125f;
				float currentX = player.getHealthBar().getScale().x;
				float diff = originX - currentX;
				player.getHealthBar().getPosition().x = player.getHealthBar().getPosition().x - diff;
			}
		}
		attackTime += Window.getFrameTime();
		if(attackTime > timeBetweenAttacks)
		{
			attackTime = 0;
		}
	}
	
	public void trackPlayer(Player player, List<Terrain> terrains)
	{
		float dotProduct = Vector3f.dot(super.getPosition(), player.getPosition());
		float playerLngth = player.getPosition().length();
		float mobLngth = super.getPosition().length();
		float angle = (float) Math.acos(dotProduct / (playerLngth * mobLngth));
		float distance = RUN_SPEED * Window.getFrameTime();
		float dx = (float) (distance * Math.sin(angle));
		float dz = (float) (distance * Math.cos(angle));
		
		for(Terrain t: terrains)
		{
			if(t.isEntityOnTerrain(this))
			{
				int gridX = (int) Math.floor((this.getPosition().x - t.getX()) / t.getGrid().getGridSquareSize());
				int gridZ = (int) Math.floor((this.getPosition().z - t.getZ()) / t.getGrid().getGridSquareSize());
				int PgridX = (int) Math.floor((player.getPosition().x - t.getX()) / t.getGrid().getGridSquareSize());
				int PgridZ = (int) Math.floor((player.getPosition().z - t.getZ()) / t.getGrid().getGridSquareSize());
				t.getGrid().setStartPosition(new Vector2f(gridX,gridZ));
				t.getGrid().setTargetPosition(new Vector2f(PgridX,PgridZ));
				// Find shortest path from entity to player position
				ArrayList<GridSquare> path = t.getGrid().executePathfinder();
				if(path== null)
				{
					System.out.println("No Path Found!");
				}else
				{
					for(GridSquare gs: path)
					{
						float x = gs.getX() * t.getGrid().getGridSquareSize();
						float z = gs.getZ() * t.getGrid().getGridSquareSize();
						float tHeight = t.getTerrainHeight(x, z);
						Vector3f pointAlongPath = new Vector3f(x,tHeight,z);
						Vector3f distanceToPoint = Vector3f.sub(pointAlongPath, this.getPosition(), null);
						float distX = RUN_SPEED * Window.getFrameTime() * distanceToPoint.x;
						float distY = RUN_SPEED * Window.getFrameTime() * distanceToPoint.y;
						float distZ = RUN_SPEED * Window.getFrameTime() * distanceToPoint.z;
						super.increasePosition(distX, distY, distZ);
					}
				}
				
				// After path to player has been found - reset for further checks
				t.getGrid().resetPathfinder();
				
			}
		}
	}
	
	public boolean isPlayerInAggroRange(Player player)
	{
		// Calculate boundary for click range
		Vector3f boundaryPos = new Vector3f(super.getPosition().x + AGGRO_RANGE, super.getPosition().y + AGGRO_RANGE, 
				super.getPosition().z + AGGRO_RANGE);
		Vector3f boundaryNeg = new Vector3f(super.getPosition().x - AGGRO_RANGE, super.getPosition().y - AGGRO_RANGE, 
				super.getPosition().z - AGGRO_RANGE);
		float distXPos = boundaryPos.x - player.getPosition().x;
		float distYPos = boundaryPos.y - player.getPosition().y;
		float distZPos = boundaryPos.z - player.getPosition().z;
		
		float distXNeg = boundaryNeg.x - player.getPosition().x;
		float distYNeg = boundaryNeg.y - player.getPosition().y;
		float distZNeg = boundaryNeg.z - player.getPosition().z;
		
		if(distXPos > 0 && distXNeg < 0 &&
		   distYPos > 0 && distYNeg < 0 &&
		   distZPos > 0 && distZNeg < 0)
		{
			return true;
		}
		
		return false;
	}

	// Getters and setters
	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}
	
	public GUITexture getHealthBar()
	{
		return healthBar;
	}
	
	public GUITexture getMobUI()
	{
		return mobUI;
	}

	public float getMaxHealth() {
		return MAX_HEALTH;
	}
	
	public boolean isAlive()
	{
		return alive;
	}
	
	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
	
	public boolean isAttacked()
	{
		return attacked;
	}
	
	public void setAttacked(boolean attacked)
	{
		this.attacked = attacked;
	}
	
	
	
	

}
