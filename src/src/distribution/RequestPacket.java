package distribution;

import java.io.Serializable;

public class RequestPacket implements Serializable {
	private RequestPacketHeader header;
	private RequestPacketBody body;
	
	public RequestPacket(RequestPacketHeader header, RequestPacketBody body) {
		this.header = header;
		this.body = body;
	}
}
