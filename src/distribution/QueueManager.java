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
	private HashMap<Integer, ServerSocketThread> connections;
	
	public QueueManager(String host, int port) {
		this.host = host;
		this.port = port;
		this.queues = new HashMap<String, Queue>();
		instantiateQueues();
		this.requestHandler = new ServerRequestHandler(this.port);
		this.marshaller = new Marshaller();
		this.connections = new HashMap<Integer, ServerSocketThread>();
	}
	
	public void instantiateQueues() {
		queues.put("publish", new Queue()); // publishers enviam para essa fila
		queues.put("subscribe", new Queue()); // subscribers enviam para essa fila
		queues.put("list", new Queue()); // list requests
		queues.put("send", new Queue()); // usada pelo broker para enviar mensagens para os 
										//subscribers (principalmente) e publishers
	}
	
	public void enqueueSendMessage(ConnectionMessage conMessage) {
		queues.get("send").enqueue(conMessage);
	}

	public void send(int conId, Message message) throws IOException {
		System.out.println("QUEUE MANAGER send with connection " + conId);
		byte[] bytes = marshaller.marshallMessage(message);
		
		connections.get(conId).setSendMessage(bytes);
		
	}

	public void receive() throws IOException, ClassNotFoundException {
		ServerSocketThread thread = requestHandler.receive();
		connections.put(thread.getThreadId(), thread);
		
		connections.get(thread.getThreadId()).start();
		
		System.out.println("QUEUE MANAGER - waiting for operation and message received");
		
		while(thread.getOperation() == null);
		Operation operation = thread.getOperation();
		if(operation != Operation.LIST)
			while(thread.getReceivedMessage() == null);
		
		switch(operation) {
			case LIST:
				System.out.println("QUEUE MANAGER - adding list connection");
				queues.get("list").enqueue(new ConnectionMessage(thread.getThreadId(), null));
				break;
			case PUBLISH:
				System.out.println("QUEUE MANAGER - adding publish connection");
				queues.get("publish").enqueue(new ConnectionMessage(thread.getThreadId(), thread.getReceivedMessage()));
				break;
			case SUBSCRIBE:
				System.out.println("QUEUE MANAGER - adding subscribe connection");
				queues.get("subscribe").enqueue(new ConnectionMessage(thread.getThreadId(), thread.getReceivedMessage()));
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
				receive();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
