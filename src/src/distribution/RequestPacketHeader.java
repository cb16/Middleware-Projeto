package distribution;

import java.io.Serializable;

public class RequestPacketHeader implements Serializable {
	private Enum operation;
	
	public RequestPacketHeader(Enum operation) {
		this.operation = operation;
	}
}
