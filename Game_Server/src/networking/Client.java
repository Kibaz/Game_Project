package networking;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.util.vector.Vector3f;

import components.Movement;
import entities.Entity;
import inputs.Input;
import terrains.Terrain;

public class Client {
	
	private UUID id;
	
	private InetAddress ipAddress;
	private int port;
	
	private Entity player;
	
	private CopyOnWriteArrayList<Input> inputs;
	
	private int snapshotCount = 0;
	
	private Movement movement;
	
	public Client(InetAddress ipAddress, int port)
	{
		this.ipAddress = ipAddress;
		this.port = port;
		this.id = UUID.randomUUID();
		this.player = new Entity(new Vector3f(100,0,90),0,0,0,1);
		this.movement = new Movement();
		player.addComponent(movement);
		inputs = new CopyOnWriteArrayList<>();
	}
	
	public void update(List<Terrain> terrains)
	{
		List<Input> processedInputs = new ArrayList<>();
		for(Input input: inputs)
		{
			applyInput(input,terrains);
			processedInputs.add(input);
		}
		
		// Clear all processed inputs
		inputs.removeAll(processedInputs);
		
		movement.applyGravity();
		
		player.increasePosition(0, movement.getJumpSpeed() * Server.getDeltaTime(), 0);
		
		collideWithTerrain(terrains);
	}
	
	private void applyInput(Input input, List<Terrain> terrains)
	{
		if(input.getInput().startsWith("w "))
		{
			movement.setCurrentSpeed(20);
		}	
		else if(input.getInput().startsWith("s "))
		{
			movement.setCurrentSpeed(-10);
		}
		else
		{
			movement.setCurrentSpeed(0);
			movement.setCurrentVelocity(new Vector3f(0,0,0));
		}
		
		if(input.getInput().startsWith("d "))
		{
			movement.setCurrentTurnSpeed(-160);
			
		}
		else if(input.getInput().startsWith("a "))
		{
			movement.setCurrentTurnSpeed(160);
		}
		else
		{
			movement.setCurrentTurnSpeed(0);
		}
		
		if(input.getInput().startsWith("space "))
		{
			movement.jump();
		}
		
		player.increaseRotation(0, movement.getCurrentTurnSpeed() * input.getTime(), 0);
		
		float distance = movement.getCurrentSpeed() * input.getTime();
		float dx = (float) (distance * Math.sin(Math.toRadians(player.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(player.getRotY())));
		movement.setCurrentVelocity(new Vector3f(dx,0,dz));
		
		player.increasePosition(movement.getCurrentVelocity().x, 
				movement.getCurrentVelocity().y, 
				movement.getCurrentVelocity().z);
	}
	
	private void collideWithTerrain(List<Terrain> terrains)
	{
		for(Terrain terrain: terrains)
		{
			if(terrain.isEntityOnTerrain(player))
			{
				float terrainHeight = terrain.getTerrainHeight(player.getPosition().x, player.getPosition().z);
				if(player.getPosition().y < terrainHeight)
				{
					player.getPosition().y = terrainHeight;
					movement.setAirborne(false);
				}
			}
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		Client client = (Client) other;
		if(client.getIpAddress().equals(client.getIpAddress()) &&
				client.getPort() == client.getPort())
		{
			return true;
		}
		
		return false;
	}
	
	public void addInput(Input input)
	{
		inputs.add(input);
	}
	
	public Entity getPlayer()
	{
		return player;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}
	
	public UUID getID()
	{
		return id;
	}
	
	public void incrementSnaphots()
	{
		snapshotCount++;
	}
	
	public int getSnapshotCount()
	{
		return snapshotCount;
	}
	
	

}
