package distribution;

import java.io.IOException;
import java.net.UnknownHostException;

import utils.Config;
import infrastructure.ClientRequestHandler;

public class QueueManagerProxy {
	private String queueName;
	
	public QueueManagerProxy(String queueName) {
		this.queueName = queueName;
	}
	
	public void send(Message message, Enum operation) throws UnknownHostException, IOException {
		
		ClientRequestHandler requestHandler = new ClientRequestHandler("localhost", Config.port, false);
		Marshaller marshaller = new Marshaller();
		RequestPacket requestPacket;
		
		//Configuração do pacote
		if(operation == Operation.LIST) {
			RequestPacketHeader packetHeader = new RequestPacketHeader(operation);
			RequestPacketBody packetBody = new RequestPacketBody(null, null);
			requestPacket = new RequestPacket(packetHeader, packetBody);
		} else {
			RequestPacketHeader packetHeader = new RequestPacketHeader(operation);
			RequestPacketBody packetBody = new RequestPacketBody(message, null);
			requestPacket = new RequestPacket(packetHeader, packetBody);
		}
		
		//Envio da requisição
		requestHandler.send(marshaller.marshallRequestPacket(requestPacket));
		
	}
	
	public Message receive() throws IOException, ClassNotFoundException {
		ClientRequestHandler requestHandler = new ClientRequestHandler("localhost", Config.port, false);
		
		Marshaller marshaller = new Marshaller();
		
		byte[] bytes = requestHandler.receive();
		
		Message message = marshaller.unmarshallMessage(bytes);
		
		return message;
	}
}
