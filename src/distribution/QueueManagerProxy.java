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
		RequestPacket requestPacket;
		
		//Configuração do pacote
		if(operation == Operation.LIST || operation == Operation.CONNECT) {
			requestHandler = new ClientRequestHandler("localhost", Config.port, true);
		} else {
			requestHandler = new ClientRequestHandler("localhost", Config.port, false);
		}
		
		//Envio da requisição
		requestHandler.send(message.toByteArray());
		
	}
	
	public Message receive(boolean waitingResponse) throws IOException, ClassNotFoundException {
		if(!waitingResponse)
			requestHandler = new ClientRequestHandler("localhost", Config.port, false);
		
		byte[] bytes = requestHandler.receive();
		
		Message message = new Message(bytes);
		
		return message;
	}
}
