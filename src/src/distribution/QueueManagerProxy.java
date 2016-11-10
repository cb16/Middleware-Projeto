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
	
	public void send(Message message) throws UnknownHostException, IOException {
		
		ClientRequestHandler requestHandler = new ClientRequestHandler("localhost", Config.port, false);
		Marshaller marshaller = new Marshaller();
		RequestPacket requestPacket = new RequestPacket();
		
		//Configuração da mensagem
		
		//Configuração do pacote
		
		//Envio da requisição
		requestHandler.send(marshaller.marshallRequestPacket(requestPacket));
		
	}
	
	public Message receive() {
		
	}
}
