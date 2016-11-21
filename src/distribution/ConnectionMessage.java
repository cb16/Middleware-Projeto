package distribution;

import java.net.InetAddress;

public class ConnectionMessage {
	private int connectionId;
	private Message message;
	
	public ConnectionMessage(int conId, Message message) {
		this.connectionId = conId;
		this.message = message;
	}

	public int getConnectionId() {
		return connectionId;
	}

	public Message getMessage() {
		return message;
	}

}
