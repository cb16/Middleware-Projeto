package distribution;

import java.net.InetAddress;

public class SubscribeUser {
	InetAddress IPAdress;
	int port;
	int ID;
	
	public SubscribeUser(InetAddress IPAdress, int port, int ID) {
		this.IPAdress = IPAdress;
		this.port = port;
		this.ID = ID;
	}
	
	public SubscribeUser(InetAddress IPAdress, int port) {
		this.IPAdress = IPAdress;
		this.port = port;
	}
	
	public InetAddress getIP() {
		return IPAdress;
	}
	
	public int getPort() {
		return port;
	}

}
