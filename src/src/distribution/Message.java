package distribution;

import java.io.Serializable;

public class Message implements Serializable {
	private MessageHeader header;
	private MessageBody body;

}
