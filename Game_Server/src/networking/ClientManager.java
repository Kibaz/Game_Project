package networking;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientManager {
	
	private static CopyOnWriteArrayList<Client> clients;
	
	public static void init()
	{
		clients = new CopyOnWriteArrayList<>();
	}
	
	public static List<Client> getClients()
	{
		return clients;
	}
	
	public static void addClient(Client client)
	{
		clients.add(client);
	}
	
	public static void removeClient(Client client)
	{
		clients.remove(client);
	}
	
	public static Client getClientContext(InetAddress ipAddress, int port)
	{
		return clientExists(ipAddress,port);
	}
	
	private static Client clientExists(InetAddress ipAddress, int port)
	{
		for(Client client: clients)
		{
			if(client.getIpAddress().equals(ipAddress) && client.getPort() == port)
			{
				return client;
			}
		}
		
		Client client = new Client(ipAddress,port);
		clients.add(client);
		return client;
	}

}
