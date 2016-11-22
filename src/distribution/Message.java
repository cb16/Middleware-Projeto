package distribution;


import java.util.ArrayList;

public class Message{
	private MessageHeader header;
	private MessageOptionalHeader optionalHeader = new MessageOptionalHeader();
	private MessagePayload payload = new MessagePayload();
	
	public Message(MessageHeader header){
		this.header = header;
	}
	
	public Message(byte[] content){
		ArrayList<Byte> list = new ArrayList<>();
		int fromIndexPayload = 2;
		
		for(byte b : content){
			list.add(b);
		}
		
		header = new MessageHeader(list);
		
		if(header.getOperation() == Operation.PUBLISH || header.getOperation() == Operation.SUBSCRIBE){
			optionalHeader = new MessageOptionalHeader(list.subList(list.size() - header.getRemainingLength(), list.size()), header.getOperation());
			fromIndexPayload += optionalHeader.length();
		}
		
		ArrayList<Byte> payloadContent = new ArrayList<Byte>(list.subList(fromIndexPayload, list.size()));
		payload.setContent(payloadContent);
	}
	
	public ArrayList<Byte> toBytes(){
		ArrayList<Byte> encoded = new ArrayList<>();
		
		encoded.addAll(header.toBytes());
		encoded.addAll(optionalHeader.getContent());
		encoded.addAll(payload.getContent());
		
		return encoded;
	}
	
	public byte[] toByteArray(){
		ArrayList<Byte> encoded = toBytes();
		byte[] result = new byte[encoded.size()];
		
		for(int i = 0; i < encoded.size(); ++i){
			result[i] = encoded.get(i).byteValue();
		}
		
		return result;
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