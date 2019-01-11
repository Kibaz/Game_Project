package runtime;

import org.lwjgl.glfw.GLFW;

import networking.Server;

public class Main {

	public static void main(String[] args) {
		// Configure a server instance
		Server server = new Server(8129);
		server.start(); // Start the server
		
		/*
		 * Whilst the server is running
		 */
		while(server.isRunning())
		{
			// Carry out updates
			server.update();
			server.getDeltaTime();
		}
		
		// Cease all operations
		server.cleanUp();
	}

}
