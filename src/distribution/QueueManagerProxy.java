package distribution;

import java.io.IOException;
import java.net.UnknownHostException;

import utils.Config;
import infrastructure.ClientRequestHandler;

public class QueueManagerProxy {
	private String queueName;
	private ClientRequestHandler requestHandler;
	
	public QueueManagerProxy(String queueName) {
		this.queueName = queueName;
	}
	
	public void send(Message message, Enum operation) throws UnknownHostException, IOException {
		
		Marshaller marshaller = new Marshaller();
		RequestPacket requestPacket;
		
		//Configuração do pacote
		if(operation == Operation.LIST) {
			requestHandler = new ClientRequestHandler("localhost", Config.port, true);
			RequestPacketHeader packetHeader = new RequestPacketHeader(operation);
			RequestPacketBody packetBody = new RequestPacketBody(null, null);
			requestPacket = new RequestPacket(packetHeader, packetBody);
		} else {
			requestHandler = new ClientRequestHandler("localhost", Config.port, false);
			RequestPacketHeader packetHeader = new RequestPacketHeader(operation);
			RequestPacketBody packetBody = new RequestPacketBody(message, null);
			requestPacket = new RequestPacket(packetHeader, packetBody);
		}
		
		//Envio da requisição
		requestHandler.send(marshaller.marshallRequestPacket(requestPacket));
		
	}
	
	public Message receive(boolean waitingResponse) throws IOException, ClassNotFoundException {
		if(!waitingResponse)
			requestHandler = new ClientRequestHandler("localhost", Config.port, false);
		
		Marshaller marshaller = new Marshaller();
		
		byte[] bytes = requestHandler.receive();
		
		Message message = marshaller.unmarshallMessage(bytes);
		
		return message;
	}
}
