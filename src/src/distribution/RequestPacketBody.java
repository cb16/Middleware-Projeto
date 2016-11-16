package distribution;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestPacketBody implements Serializable {
	private Message message;
	private ArrayList<Object> parameters;
	
	public RequestPacketBody(Message message, ArrayList<Object> parameters) {
		this.message = message;
		this.parameters = parameters;
	}
}
