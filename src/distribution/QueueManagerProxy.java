package distribution;

import java.io.IOException;
import java.net.UnknownHostException;

import utils.Config;
import infrastructure.ClientRequestHandler;

public class QueueManagerProxy {
	private ClientRequestHandler requestHandler;
	
	public QueueManagerProxy(String queueName) {
		this.requestHandler = new ClientRequestHandler("localhost", Config.port);
	}
	
	public void closeConnection() {
		requestHandler.close();
	}
	
	public void send(Message message, Operation operation) throws UnknownHostException, IOException {
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
