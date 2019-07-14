package runtime;

import java.util.ArrayList;
import java.util.List;

import networking.ClientManager;
import networking.Server;
import terrains.Terrain;

public class Main {
	
	public static void main(String args[])
	{
		ClientManager.init(); // Initialise client manager for managing UDP Clients
		Server server = new Server(8061,8060);
		List<Terrain> terrains = new ArrayList<>();
		terrains.add(new Terrain(0,0));
		terrains.add(new Terrain(1,0));
		server.start();
		server.update(terrains);
	}
	
	

}
