package networking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerClientHandler {
	
	private Map<Integer,PeerClient> peerClients;
	
	public PeerClientHandler()
	{
		peerClients = new ConcurrentHashMap<>();
	}
	
	public Map<Integer,PeerClient> getPeers()
	{
		return peerClients;
	}
	
	public void addPeer(int id, PeerClient peer)
	{
		peerClients.put(id, peer);
	}
	
	

}
