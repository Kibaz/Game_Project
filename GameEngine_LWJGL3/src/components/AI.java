package components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import combat.Ability;
import entities.Entity;
import rendering.Window;
import terrains.Terrain;
import utils.Maths;

public class AI extends Component {
	
	private static final float TURN_SPEED = 160;
	private static final float RUN_SPEED = 15;
	private static final float WALK_SPEED = 10;
	private static final float GRAVITY = -50;
	
	private float steerForce;
	private float currentSpeed;
	
	private float upSpeed;
	
	private float aggroRange;
	private float slowingRadius;
	private float wanderRadius;
	private float avoidanceForce;
	
	private List<Entity> entities;
	private List<Terrain> terrains;
	private List<Entity> enemies;
	
	private Map<String, Ability> abilities;
	
	private Vector2f target;
	private Entity targetToAttack;
	
	private Vector2f currentVelocity;
	private Vector2f acceleration;
	
	private Vector3f spawnLocation;
	
	private Random randomiser; // Randomiser
	
	private enum State {
		IDLE,
		ATTACK,
		WANDER,
		RETREAT,
		DEAD
	}
	
	private State state;
	
	private float idleTime;
	private float idleTimeLimit;
	
	public AI(String name, List<Entity> entities, List<Terrain> terrains, List<Entity> enemies, Map<String,Ability> abilities)
	{
		super(name);
		this.entities = entities;
		this.terrains = terrains;
		this.enemies = enemies;
		this.abilities = abilities;
		init();
	}

	@Override
	protected void init() {
		idleTime = 0;
		idleTimeLimit = 0;
		currentVelocity = new Vector2f(0,0);
		acceleration = new Vector2f(0,0);
		state = State.WANDER;
		randomiser = new Random();
	}

	@Override
	public void start() {
		spawnLocation = new Vector3f(entity.getPosition());
	}

	@Override
	public void cleanUp() {
		
	}

	@Override
	public void update() {
		if(entity != null)
		{
			verifyAction();
			
			move();
			
			EntityInformation info = entity.getComponentByType(EntityInformation.class);
			if(info != null)
			{
				if(info.getHealth() <= 0)
				{
					state = State.DEAD;
				}
			}
			
			// Update ability damage indicators
			for(String abilityName: abilities.keySet())
			{
				abilities.get(abilityName).getDamageIndicator().setPosition(entity.getPosition());
				abilities.get(abilityName).getDamageIndicator().setRotY(entity.getRotY());
				abilities.get(abilityName).update();
			}
			
			if(targetToAttack != null)
			{
				CombatManager combatManager = targetToAttack.getComponentByType(CombatManager.class);
				if(combatManager != null)
				{
					combatManager.setInCombat(true);
				}
				
				if(state == State.DEAD)
				{
					// Grant experience to target
					EntityInformation targetInfo = targetToAttack.getComponentByType(EntityInformation.class);
					float experienceGain = (info.getLevel() / (float) targetInfo.getLevel()) * (0.1f * targetInfo.getExperienceCap());
					targetInfo.setExperience(targetInfo.getExperience() + (int) experienceGain);
					combatManager.setInCombat(false);
					targetToAttack = null;
					QuestTracker.notifyTracker(info);
				}
			}
		}
		
	}
	
	private void verifyAction()
	{
		// If mob is in attack mode - seek the enemy
		if(state == State.ATTACK)
		{
			target = new Vector2f(targetToAttack.getPosition().x,targetToAttack.getPosition().z);
			currentSpeed = RUN_SPEED;
			alertToEnemies();
			attack();
			
			// Set entity in combat
			CombatManager combatManager = entity.getComponentByType(CombatManager.class);
			if(combatManager != null)
			{
				combatManager.setInCombat(true);
			}
			
			// Play running animation
		}
		else if(state == State.WANDER)
		{
			// Carry out wandering behaviour
			// This can be done by seeking out random points
			// Maybe giving it a job to do
			currentSpeed = WALK_SPEED;
			target = wander();
			alertToEnemies();
		}
		else if(state == State.IDLE)
		{
			currentSpeed = 0;
			idle();
			alertToEnemies();
		}
		else if(state == State.RETREAT)
		{
			currentSpeed = RUN_SPEED;
			target = new Vector2f(spawnLocation.x,spawnLocation.z);
			retreat();
			
			// Set entity not in combat
			CombatManager combatManager = entity.getComponentByType(CombatManager.class);
			if(combatManager != null)
			{
				combatManager.setInCombat(false);
			}
		}
		else if(state == State.DEAD)
		{
			currentSpeed = 0;
			target = null;
			// Play death animation initially (when state just changed)
		}
	}
	
	private void attack()
	{
		Ability abilityToUse = null;
		List<Ability> abilitiesAvailable = new ArrayList<>();
		for(String abilityName: abilities.keySet())
		{
			Ability ability = abilities.get(abilityName);
			if(!ability.isOnCooldown())
			{
				abilitiesAvailable.add(ability);
			}
		}
		
		int size = abilitiesAvailable.size();
		if(size > 0)
		{
			int index = randomiser.nextInt(size);
			abilityToUse = abilitiesAvailable.get(index);
		}
		
		if(abilityToUse != null)
		{
			abilityToUse.doEffect(enemies);
		}
	}
	
	private void move()
	{
		upSpeed += GRAVITY * Window.getFrameTime();
		
		float distY = upSpeed * Window.getFrameTime();
		
		entity.increasePosition(0, distY, 0);
		entity.getAABB().moveAABB(0, distY, 0);
		
		for(Terrain terrain: terrains)
		{
			if(terrain.isEntityOnTerrain(entity))
			{
				float terrainHeight = terrain.getTerrainHeight(entity.getPosition().x, entity.getPosition().z);
				if(entity.getPosition().y < terrainHeight)
				{
					entity.getPosition().y = terrainHeight;
					entity.getAABB().setY(entity.getModel().getBaseModel(), entity.getPosition());
					entity.getAABB().getCentre().y = entity.getPosition().y + (entity.getAABB().getHeight()/2f);
				}
			}
		}
		
		Vector2f avoidance = avoid(entities);
		Vector2f steering = seek();
		steering = Vector2f.add(steering, avoidance, null);
		Vector2f.add(acceleration, steering, acceleration);
		
		Vector2f.add(currentVelocity, acceleration, currentVelocity);
		
		float maxVel = currentSpeed * Window.getFrameTime();
		if(currentVelocity.length() > maxVel)
		{
			currentVelocity = Maths.mulScalar(currentVelocity,  maxVel / currentVelocity.length());
		}
		entity.increasePosition(currentVelocity.x, 0 , currentVelocity.y);
		entity.getAABB().moveAABB(currentVelocity.x, 0, currentVelocity.y);
		
		entity.getAABB().resetBox(entity.getPosition());
		
		acceleration = new Vector2f(0,0);
	}
	
	private void retreat()
	{
		float distance = Vector3f.sub(spawnLocation, entity.getPosition(), null).length();
		if(distance < 0.25f)
		{
			state = State.WANDER;
		}
	}
	
	private void alertToEnemies()
	{
		Entity enemy = findClosestEnemy();
		if(enemy != null)
		{
			Vector2f dirToenemy = Vector2f.sub(new Vector2f(enemy.getPosition().x,enemy.getPosition().z),
					new Vector2f(entity.getPosition().x, entity.getPosition().z), null);
			float distance = dirToenemy.length();

			// Check if enemy is in line of sight and within aggro range
			if(distance < aggroRange)
			{
				Vector2f facingVector = new Vector2f((float) Math.sin(Math.toRadians(entity.getRotY())), (float) Math.cos(Math.toRadians(entity.getRotY())));
				float dot = Vector2f.dot(facingVector, dirToenemy);
				float magDiv = dirToenemy.length() * facingVector.length();
				float angle = (float) Math.toDegrees(Math.acos(dot/magDiv));
				if(angle < 120)
				{
					targetToAttack = enemy;
					
					EntityInformation targetInfo = targetToAttack.getComponentByType(EntityInformation.class);
					if(targetInfo != null)
					{
						if(targetInfo.getHealth() <= 0)
						{
							if(state == State.ATTACK)
							{
								state = State.RETREAT;
							}
							
						}
						else
						{
							state = State.ATTACK;
						}
					}
				}

			}
			else
			{
				if(state == State.ATTACK)
				{
					state = State.RETREAT;
					CombatManager combatManager = targetToAttack.getComponentByType(CombatManager.class);
					if(combatManager != null)
					{
						combatManager.setInCombat(false);
					}
					targetToAttack = null;
				}
				
			}
		}

	}
	
	private Entity findClosestEnemy()
	{

		if(enemies.size() == 0 || enemies.isEmpty())
		{
			return null;
		}
		
		Entity closest = enemies.get(0);
		float distanceToClosest = Maths.distance(closest.getPosition(), entity.getPosition());
		for(int i = 1; i < enemies.size(); i++)
		{
			float distanceToNew = Maths.distance(enemies.get(i).getPosition(), entity.getPosition());
			if(distanceToNew < distanceToClosest)
			{
				closest = enemies.get(i);
			}
		}
		
		return closest;
	}
	
	private void idle()
	{
		// Play idle animation
		
		idleTime += Window.getFrameTime();
		if(idleTime > idleTimeLimit)
		{
			idleTime %= idleTimeLimit;
			state = State.WANDER;
		}
	}
	
	private Vector2f seek()
	{
		if(target == null)
		{
			return new Vector2f(0,0);
		}
		Vector2f currPos = new Vector2f(entity.getPosition().x,entity.getPosition().z);
		Vector2f desired = Vector2f.sub(target, currPos, null);
		
		float angle = Maths.findVectorAngle(desired);
		float diffAngles = (entity.getRotY() - angle + 180) % 360 - 180;
		diffAngles = diffAngles < -180 ? diffAngles + 360 : diffAngles;

		if((int) diffAngles > 0)
		{
			entity.increaseRotation(0, -TURN_SPEED * Window.getFrameTime(), 0);
		}
		else if((int) diffAngles < 0)
		{
			entity.increaseRotation(0, TURN_SPEED * Window.getFrameTime(), 0);
		}
		
		float maxVel = currentSpeed * Window.getFrameTime();
		if(desired.length() < slowingRadius && state == State.ATTACK)
		{
			desired.normalise();
			desired = Maths.mulScalar(desired, 0);
		}
		else
		{
			desired.normalise();
			desired = Maths.mulScalar(desired, maxVel);
		}
		
		Vector2f steering = Vector2f.sub(desired, currentVelocity, null);
		float maxForce = steerForce * Window.getFrameTime();
		if(steering.length() > maxForce)
		{
			Maths.mulScalar(steering, maxForce / steering.length());
		}
		
		return steering;
	}
	
	private Vector2f wander()
	{
		if(target == null)
		{
			Random random = new Random();
			int targetPosX = (int) (random.nextInt((int) (wanderRadius + 1 + wanderRadius)) - wanderRadius);
			int targetPosZ = (int) (random.nextInt((int) (wanderRadius + 1 + wanderRadius)) - wanderRadius);
			return new Vector2f(spawnLocation.x + targetPosX, spawnLocation.z + targetPosZ);
		}
		else
		{
			if(Maths.distance(new Vector2f(entity.getPosition().x, entity.getPosition().z), target) < 0.25f)
			{
				Random random = new Random();
				int stateId = random.nextInt(2);
				if(stateId == 0)
				{
					state = State.IDLE;
					idleTimeLimit = random.nextFloat() * 10 + 1;
				}
				else
				{
					state = State.WANDER;
				}
				return null;
			}
			return target;
		}
	}
	
	private Vector2f avoid(List<Entity> entities)
	{
		if(currentVelocity.length() == 0)
		{
			return new Vector2f(0,0);
		}
		float dynamicVel = currentVelocity.length() / (currentSpeed * Window.getFrameTime());
		Vector2f scaledVelocity = currentVelocity.normalise(null);
		scaledVelocity = Maths.mulScalar(scaledVelocity, dynamicVel);
		Vector2f ahead = Vector2f.add(new Vector2f(entity.getPosition().x,entity.getPosition().z), scaledVelocity, null);
		
		Vector2f halfScaledVelocity = currentVelocity.normalise(null);
		halfScaledVelocity = Maths.mulScalar(halfScaledVelocity, dynamicVel * 0.5f);
		Vector2f halfAhead = Vector2f.add(new Vector2f(entity.getPosition().x,entity.getPosition().z), halfScaledVelocity, null);
		for(Entity entity: entities)
		{
			if(entity.isStaticModel())
			{
				// Check for collision with ahead ray vectors
				Vector2f centre = new Vector2f(entity.getAABB().getCentre().x,entity.getAABB().getCentre().z);
				float radius = entity.getAABB().getWidth() * entity.getScale() * 2 * 0.5f;
				if(shouldAvoid(ahead,halfAhead,centre,radius))
				{
					Vector2f avoidance = Vector2f.sub(ahead, centre, null);
					avoidance.normalise();
					avoidance = Maths.mulScalar(avoidance, avoidanceForce);
					return avoidance;
				}
			}
		}

		
		return new Vector2f(0,0);
	}
	
	private boolean shouldAvoid(Vector2f ahead, Vector2f halfAhead, Vector2f centre, float radius)
	{
		return Maths.distance(centre, ahead) <= radius || 
				Maths.distance(centre, halfAhead) <= radius || 
				Maths.distance(centre, new Vector2f(entity.getPosition().x,entity.getPosition().z)) <= radius;
	}

	// Getters and Setters
	public void setAggroRange(float aggroRange) {
		this.aggroRange = aggroRange;
	}

	public void setAvoidanceForce(float avoidanceForce) {
		this.avoidanceForce = avoidanceForce;
	}

	public void setSteerForce(float steerForce) {
		this.steerForce = steerForce;
	}

	public void setWanderRadius(float wanderRadius) {
		this.wanderRadius = wanderRadius;
	}

	public void setSlowingRadius(float slowingRadius) {
		this.slowingRadius = slowingRadius;
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	

}
