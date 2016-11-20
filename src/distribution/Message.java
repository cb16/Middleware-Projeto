package distribution;


import java.util.ArrayList;

public class Message{
	private MessageHeader header;
	private MessageOptionalHeader optionalHeader;
	private MessagePayload payload;
	
	public Message(MessageHeader header){
		this.header = header;
	}
	
	public ArrayList<Byte> toBytes(){
		ArrayList<Byte> encoded = new ArrayList<>();
		
		encoded.addAll(header.toBytes());
		encoded.addAll(optionalHeader.getContent());
		encoded.addAll(payload.getContent());
		
		return encoded;
	}
	
	public MessageHeader getHeader() {
		return header;
	}

	public void setHeader(MessageHeader header) {
		this.header = header;
	}

	public MessageOptionalHeader getOptionalHeader() {
		return optionalHeader;
	}

	public void setOptionalHeader(MessageOptionalHeader optionalHeader) {
		this.optionalHeader = optionalHeader;
	}

	public MessagePayload getPayload() {
		return payload;
	}

	public void setPayload(MessagePayload payload) {
		this.payload = payload;
	}
}