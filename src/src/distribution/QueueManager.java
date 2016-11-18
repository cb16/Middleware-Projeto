package distribution;

import infrastructure.ServerRequestHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utils.Config;

public class QueueManager extends Thread implements IQueueManager {
	private String host;
	private int port;
	public static Map<String, Queue> queues;
	private ServerRequestHandler requestHandler;
	private Marshaller marshaller;
	private ArrayList<ServerSocketThread> connections;
	
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
	
	public void enqueueSendMessage(Message message) {
		Queue q = queues.get("send");
		System.out.println("antes");
		System.out.println(q.queue);
		queues.get("send").enqueue(message);
		q = queues.get("send");
		System.out.println("after");
		System.out.println(q.queue);
	}

	public void send(Message message) throws IOException {
		
		int connectionId = message.getBody().getConnectionId();
		
		byte[] bytes = marshaller.marshallMessage(message);
		
		requestHandler.send(bytes);
		
	}

	public void receive() throws IOException, ClassNotFoundException {
		
		connections.add(requestHandler.receive());
		
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
				receive();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
