package runtime;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import networking.Server;
import terrains.Terrain;
import world.Zone;

public class Main {

	public static void main(String[] args) {
		
		// Descriptors - i.e Zones
		Zone zone = new Zone("Ti'Thar Thul Jungle");
		
		List<Terrain> terrains = new ArrayList<>();
		Terrain terrain1 = new Terrain(0,0,"heightmap");
		Terrain terrain2 = new Terrain(1,0,"heightmap");
		terrains.add(terrain1);
		terrains.add(terrain2);
		
		// Configure a server instance
		Server server = new Server(8129);
		server.start(terrain1); // Start the server
		
		/*
		 * Whilst the server is running
		 */
		while(server.isRunning())
		{
			// Carry out updates
			server.update(terrains);
		}
		
		// Cease all operations
		server.cleanUp();
	}

}
