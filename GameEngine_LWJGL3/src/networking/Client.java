package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import inputs.KeyboardHandler;
import runtime.Main;

public class Client {
	
	private static DatagramSocket udpSocket;
	public static InetAddress serverAddress;
	public static int serverPort;
	private static Thread clientThread;
	
	// Hold key states
	private static int prev_w_key_state = GLFW.GLFW_RELEASE;
	private static int prev_s_key_state = GLFW.GLFW_RELEASE;
	private static int prev_a_key_state = GLFW.GLFW_RELEASE;
	private static int prev_d_key_state = GLFW.GLFW_RELEASE;
	private static int prev_space_key_state = GLFW.GLFW_RELEASE;
	
	
	private static byte[] buffer = new byte[512];
	
	public static void listen()
	{
		try {
			udpSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		// Do not listen of UDP socket has not been initialised
		if(udpSocket == null)
		{
			return;
		}
		
		// run client listening for server information in separate thread
		clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{
					while(true)
					{
						// Configure Datagram packet
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						// Block whilst waiting for serve response
						udpSocket.receive(packet);
						
						String msg = new String(packet.getData(), 0, packet.getLength());
						
						if(msg.startsWith("Position: ") && Main.testEnt != null)
						{
							String values = msg.split(":")[1];
							System.out.println(msg);
							Vector3f position = new Vector3f(Float.parseFloat(values.split(",")[0]),
									Float.parseFloat(values.split(",")[1]),Float.parseFloat(values.split(",")[2]));
							float rx = Float.parseFloat(values.split(",")[3]);
							float ry = Float.parseFloat(values.split(",")[4]);
							float rz = Float.parseFloat(values.split(",")[5]);
							Main.testEnt.setPosition(position);
							Main.testEnt.setRotX(rx);
							Main.testEnt.setRotY(ry);
							Main.testEnt.setRotZ(rz);
						}
						

					}
				}catch (SocketException e)
				{
					System.out.println("Client udp socket closed...");
				}catch (IOException e)
				{
					e.printStackTrace();
				}


			}
			
		});
		
		// Start client thread
		clientThread.start();
	}
	
	// Send data to the server
	public static void send(byte[] data)
	{
		try {
			DatagramPacket packet = new DatagramPacket(data,data.length,serverAddress,serverPort);
			udpSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Check for inputs i.e Keyboard presses and Mouse clicks
	// Send this data to the server accordingly
	public static void sendInputs()
	{
		// Check for W key Press and Release Events
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_W))
		{
			if(prev_w_key_state == GLFW.GLFW_RELEASE)
			{
				// send client input
				send("w key pressed".getBytes());
			}
			prev_w_key_state = GLFW.GLFW_PRESS;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_W))
		{
			if(prev_w_key_state == GLFW.GLFW_PRESS)
			{
				// send client input
				send("w key released".getBytes());
			}
			prev_w_key_state = GLFW.GLFW_RELEASE;
		}
		
		// Check for S key Press and Release events
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S))
		{
			if(prev_s_key_state == GLFW.GLFW_RELEASE)
			{
				// send client input
				send("s key pressed".getBytes());
			}
			prev_s_key_state = GLFW.GLFW_PRESS;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_S))
		{
			if(prev_s_key_state == GLFW.GLFW_PRESS)
			{
				// send client input
				send("s key released".getBytes());
			}
			prev_s_key_state = GLFW.GLFW_RELEASE;
		}
		
		// Check for D key Press and Release events
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D))
		{
			if(prev_d_key_state == GLFW.GLFW_RELEASE)
			{
				// send client input
				send("d key pressed".getBytes());
			}
			prev_d_key_state = GLFW.GLFW_PRESS;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_D))
		{
			if(prev_d_key_state == GLFW.GLFW_PRESS)
			{
				// send client input
				send("d key released".getBytes());
			}
			prev_d_key_state = GLFW.GLFW_RELEASE;
		}
		
		// Check for A key Press and Release events
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A))
		{
			if(prev_a_key_state == GLFW.GLFW_RELEASE)
			{
				// send client input
				send("a key pressed".getBytes());
			}
			prev_a_key_state = GLFW.GLFW_PRESS;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_A))
		{
			if(prev_a_key_state == GLFW.GLFW_PRESS)
			{
				// send client input
				send("a key released".getBytes());
			}
			prev_a_key_state = GLFW.GLFW_RELEASE;
		}
		
		// Check for SPACE key Press and Release events
		if(KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE))
		{
			if(prev_space_key_state == GLFW.GLFW_RELEASE)
			{
				// send client input
				send("space key pressed".getBytes());
			}
			prev_space_key_state = GLFW.GLFW_PRESS;
		}
		
		if(KeyboardHandler.isKeyUp(GLFW.GLFW_KEY_SPACE))
		{
			if(prev_space_key_state == GLFW.GLFW_PRESS)
			{
				// send client input
				send("space key released".getBytes());
			}
			prev_space_key_state = GLFW.GLFW_RELEASE;
		}
	}
	
	public static void diconnect()
	{
		udpSocket.close();
		try {
			clientThread.join(); // wait for thread to terminate
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
