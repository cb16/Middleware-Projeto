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
		this.requestHandler = new ClientRequestHandler("localhost", Config.port);
	}
	
	public void closeConnection() {
		requestHandler.close();
	}
	
	public void send(Message message, Enum operation) throws UnknownHostException, IOException {
		//Envio da requisição
		requestHandler.send(message.toByteArray());
		
	}
	
	public Message receive() throws IOException, ClassNotFoundException {
		byte[] bytes = requestHandler.receive();
		
		Message message = null;
		
		if(bytes != null)
			message = new Message(bytes);
		
		return message;
	}
	
	public ClientRequestHandler getRequestHandler() {
		return requestHandler;
	}
}
