package distribution;

import java.io.Serializable;

public class Message implements Serializable {
	private MessageHeader header;
	private MessageBody body;

	public Message(MessageHeader header, MessageBody body) {
		this.header = header;
		this.body = body;
	}

	public MessageHeader getHeader() {
		return header;
	}

	public MessageBody getBody() {
		return body;
	}
}
