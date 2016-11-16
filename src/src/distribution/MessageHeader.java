package distribution;

import java.io.Serializable;

public class MessageHeader implements Serializable {
	private byte mqttControlPacketType;
	private byte remainingLength;
}
