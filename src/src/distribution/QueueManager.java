package distribution;

import infrastructure.ServerRequestHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.Config;

public class QueueManager extends Thread implements IQueueManager {
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
		this.requestHandler = new ServerRequestHandler(this.port);
		this.marshaller = new Marshaller();
	}
	
	public void instantiateQueues() {
		queues.put("publish", new Queue()); // publishers enviam para essa fila
		queues.put("subscribe", new Queue()); // subscribers enviam para essa fila
		queues.put("list", new Queue()); // list requests
		queues.put("send", new Queue()); // usada pelo broker para enviar mensagens para os 
										//subscribers (principalmente) e publishers
	}

	public void send(Message message) throws IOException {
		
		byte[] bytes = marshaller.marshallMessage(message);
		
		requestHandler.send(bytes);
		
	}

	public void receive() throws IOException, ClassNotFoundException {
		
		byte[] bytes = requestHandler.receive();
		RequestPacket requestPacket = marshaller.unmarshallRequestPacket(bytes);
		
		Operation operation = (Operation) requestPacket.getHeader().getOperation();
		Message message = requestPacket.getBody().getMessage();
		
		switch(operation) {
			case LIST:
				queues.get("list").enqueue(message);
				break;
			case PUBLISH:
				queues.get("publish").enqueue(message);
				break;
			case SUBSCRIBE:
				queues.get("subscribe").enqueue(message);
				break;
		}
	}
	
	public void run() {
		try {
			receive();
		} catch (ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true) {
			try {
				if(queues.get("send").queueSize() == 0 
						&& requestHandler.connectionSocket.isClosed())
					receive();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
