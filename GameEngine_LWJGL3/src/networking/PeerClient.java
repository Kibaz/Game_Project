package networking;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import animation.AnimatedCharacter;
import entities.Entity;
import models.TexturedModel;
import peerData.PeerPlayerData;
import rendering.Window;
import utils.DataTransfer;
import worldData.World;

public class PeerClient {
	
	private PeerPlayerData playerData;
	
	private int id;
	
	private Entity entity;
	
	private AnimatedCharacter animChar;
	
	private Map<Integer,String> serverRequests;
	
	public PeerClient()
	{
		serverRequests = new HashMap<>();
	}
	
	public int getID() {
		return id;
	}
	
	public Map<Integer,String> getServerRequests()
	{
		return serverRequests;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	/* The ID of the client will always be appended to the end of
	 * each packet received from the server
	 * and thus can be extracted in the following way
	 */
	public void getIdFromBytes(byte[] data)
	{
		byte[] idBytes = new byte[4];
		int idData = data.length - 4;
		int count = 0;
		for(int i = idData; i < data.length; i++)
		{
			idBytes[count] = data[i];
			count++;
		}
		
		int clientID = DataTransfer.byteArrayToInteger(idBytes);
		this.id = clientID;
	}

	public Entity getEntity() {
		return entity;
	}
	
	public AnimatedCharacter getAnimaterCharacter()
	{
		return animChar;
	}
	
	private void setEntity()
	{
		TexturedModel model = null;
		for(Entity ent: World.worldObjects)
		{
			if(ent.getModel().getBaseModel().getVaoID() == playerData.getModelID())
			{
				model = ent.getModel();
				break;
			}
		}
		entity = new Entity(model, playerData.getPosition(), playerData.getRotX(),
				playerData.getRotY(),playerData.getRotZ(),1);
		createAnimatedCharacter();
	}
	
	public void processRequest(int position, String request)
	{
		serverRequests.put(position, request);
	}
	
	private void createAnimatedCharacter()
	{
		animChar = new AnimatedCharacter(entity);
	}

	public PeerPlayerData getPlayerData()
	{
		return playerData;
	}

	public void setPlayerData(PeerPlayerData playerData)
	{
		this.playerData = playerData;
	}
	
	public void update()
	{
		
	}


}
