package distribution;


import java.util.ArrayList;

public class Message{
	private MessageHeader header;
	private MessageOptionalHeader optionalHeader;
	private ArrayList<Byte> payload;
	
	public ArrayList<Byte> toBytes(){
		ArrayList<Byte> encoded = new ArrayList<>();
		
		encoded.addAll(header.toBytes());
		encoded.addAll(optionalHeader.toBytes());
		encoded.addAll(payload);
		
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

	public ArrayList<Byte> getPayload() {
		return payload;
	}

	public void setPayload(ArrayList<Byte> payload) {
		this.payload = payload;
	}

	public Message(MessageHeader header) {
		this.header = header;
	}
}
