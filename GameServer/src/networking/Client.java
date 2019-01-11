package networking;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import entityData.PlayerData;

public class Client {
	
	// Networking info
	private int port;
	private InetAddress address;
	
	
	public static final int KEY_PRESSED = 1;
	public static final int KEY_RELEASED = 0;
	
	private int ID; // Client reference number
	private Map<Character,Integer> keyInputs; // Store keyboard inputs and their states i.e. Pressed / Released
	
	private PlayerData playerData;
	
	public Client(int id, InetAddress address, int port)
	{
		this.ID = id;
		this.address = address;
		this.port = port;
		keyInputs = new HashMap<>();
		fillKeyInputs();
		playerData = new PlayerData(new Vector3f(100,0,90),0,0,0,1);
	}
	
	// Update the client's player data
	public void movePlayer()
	{
		checkKeyInputs();
		playerData.increaseRotation(0, playerData.getCurrentTurnSpeed() * Server.getDeltaTime(), 0);
		float distance = playerData.getCurrentSpeed() * Server.getDeltaTime();
		float distX = (float) (distance * Math.sin(Math.toRadians(playerData.getRotY())));
		float distZ = (float) (distance * Math.cos(Math.toRadians(playerData.getRotY())));
		playerData.increasePosition(distX, 0, distZ);
	}
	
	public void checkKeyInputs()
	{
		// Forwards and backwards movement
		if(keyInputs.get('w') == KEY_PRESSED)
		{
			playerData.setCurrentSpeed(PlayerData.GROUND_SPEED);
		}
		else if(keyInputs.get('s') == KEY_PRESSED)
		{
			playerData.setCurrentSpeed(-PlayerData.GROUND_SPEED);
		}
		else
		{
			playerData.setCurrentSpeed(0);
		}
		
		// Turning left and right
		if(keyInputs.get('a') == KEY_PRESSED)
		{
			playerData.setCurrentTurnSpeed(PlayerData.TURN_SPEED);
		}
		else if(keyInputs.get('d') == KEY_PRESSED)
		{
			playerData.setCurrentTurnSpeed(-PlayerData.TURN_SPEED);
		}
		else
		{
			playerData.setCurrentTurnSpeed(0);
		}
	}
	
	private void fillKeyInputs()
	{
		keyInputs.put('w', KEY_RELEASED);
		keyInputs.put('a', KEY_RELEASED);
		keyInputs.put('s', KEY_RELEASED);
		keyInputs.put('d', KEY_RELEASED);
		keyInputs.put(' ', KEY_RELEASED);
	}

	// Getters and Setters
	public int getID() {
		return ID;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	public void setPlayerData(PlayerData playerData) {
		this.playerData = playerData;
	}

	public int getPort() {
		return port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public Map<Character, Integer> getKeyInputs() {
		return keyInputs;
	}
	
	
	
	
	
	
	
	
	

}
