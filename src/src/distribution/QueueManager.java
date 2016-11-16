package distribution;

import infrastructure.ServerRequestHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.Config;

public class QueueManager implements IQueueManager {
	private String host;
	private int port;
	Map<String, Queue> queues;
	private ServerRequestHandler requestHandler;
	private Marshaller marshaller;
	
	public QueueManager(String host, int port) {
		this.host = host;
		this.port = port;
		this.queues = new HashMap<String, Queue>();
		instantiateQueues();
		this.requestHandler = new ServerRequestHandler(Config.port);
		this.marshaller = new Marshaller();
	}
	
	public void instantiateQueues() {
		queues.put("publish", new Queue()); // publishers enviam para essa fila
		queues.put("subscribe", new Queue()); // subscribers enviam para essa fila
		queues.put("send", new Queue()); // usada pelo broker para enviar mensagens para os 
										//subscribers (principalmente) e publishers
	}

	public void send(Message message) {
		
		
	}

	public RequestPacket receive() throws IOException, ClassNotFoundException {
		
		//mudar para colocar na fila!
		
		byte[] bytes = requestHandler.receive();
		RequestPacket requestPacket = marshaller.unmarshallRequestPacket(bytes);
		return requestPacket;
	}
}
