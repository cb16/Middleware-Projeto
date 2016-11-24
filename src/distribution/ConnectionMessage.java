package distribution;


public class ConnectionMessage {
	private String connectionId;
	private Message message;
	
	public ConnectionMessage(String conId, Message message) {
		this.connectionId = conId;
		this.message = message;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public Message getMessage() {
		return message;
	}

}
