package distribution;

import java.io.Serializable;

public class RequestPacket implements Serializable {
	private RequestPacketHeader header;
	private RequestPacketBody body;
}
