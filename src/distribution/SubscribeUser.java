package distribution;

import java.net.InetAddress;

public class SubscribeUser {
	InetAddress IPAdress;
	int port;
	String id;
	
	public SubscribeUser(InetAddress IPAdress, int port, String id) {
		this.IPAdress = IPAdress;
		this.port = port;
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public InetAddress getIP() {
		return IPAdress;
	}
	
	public int getPort() {
		return port;
	}

}
