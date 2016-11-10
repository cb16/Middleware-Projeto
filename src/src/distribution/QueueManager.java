package distribution;

import java.util.HashMap;
import java.util.Map;

public class QueueManager implements IQueueManager {
	private String host;
	private int port;
	Map<String, Queue> queues;
	
	public QueueManager(String host, int port) {
		this.host = host;
		this.port = port;
		this.queues = new HashMap<String, Queue>();
		instantiateQueues();
	}
	
	public void instantiateQueues() {
		queues.put("publish", new Queue()); // publishers enviam para essa fila
		queues.put("subscribe", new Queue()); // subscribers enviam para essa fila
		queues.put("send", new Queue()); // usada pelo broker para enviar mensagens para os 
										//subscribers (principalmente) e publishers
	}

	public void send() {
		// TODO Auto-generated method stub
		
	}

	public void receive() {
		// TODO Auto-generated method stub
		
	}
}
