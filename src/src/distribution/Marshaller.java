package distribution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Marshaller {
	public byte[] marshallMessage(Message message) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		
		objectStream.writeObject(message);
		return byteStream.toByteArray();
	}
	
	public Message unmarshallMessage(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objectStream = new ObjectInputStream(byteStream);
		
		return (Message) objectStream.readObject();
	}
	
	public byte[] marshallRequestPacket(RequestPacket requestPacket) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		
		objectStream.writeObject(requestPacket);
		return byteStream.toByteArray();
	}
	
	public RequestPacket unmarshallRequestPacket(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objectStream = new ObjectInputStream(byteStream);
		
		return (RequestPacket) objectStream.readObject();
	}

}
