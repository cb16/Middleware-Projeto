package distribution;

public class Invoker {
	private String op;
	private ConnectionMessage connectionMessage;
	
	public Invoker(String op, ConnectionMessage connectionMessage){
		this.op = op;
		this.connectionMessage = connectionMessage;
	}
	
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public ConnectionMessage getConnectionMessage() {
		return connectionMessage;
	}
	public void setConnectionMessage(ConnectionMessage connectionMessage) {
		this.connectionMessage = connectionMessage;
	}
	
}
