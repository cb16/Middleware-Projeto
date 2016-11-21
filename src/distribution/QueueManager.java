package distribution;

import infrastructure.ServerRequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueueManager extends Thread implements IQueueManager {
	private String host;
	private int port;
	public static Map<String, Queue> queues;
	private ServerRequestHandler requestHandler;
	private HashMap<Integer, ServerSocketThread> connections;
	private int idCounter;
	
	public QueueManager(String host, int port) {
		this.host = host;
		this.port = port;
		QueueManager.queues = new HashMap<String, Queue>();
		instantiateQueues();
		this.requestHandler = new ServerRequestHandler(this.port);
		this.connections = new HashMap<Integer, ServerSocketThread>();
		this.idCounter = 0;
	}
	
	public void instantiateQueues() {
		queues.put("connect", new Queue()); // conectar novos usuários
		queues.put("publish", new Queue()); // publishers enviam para essa fila
		queues.put("subscribe", new Queue()); // subscribers enviam para essa fila
		queues.put("list", new Queue()); // list requests
		queues.put("send", new Queue()); // usada pelo broker para enviar mensagens para os 
										//subscribers (principalmente) e publishers
	}
	
	public void enqueueSendMessage(ConnectionMessage conMessage) {
		queues.get("send").enqueue(conMessage);
	}
	
	public ServerSocketThread getConnection(int conId){
		return connections.get(conId); 
	}
	
	public static byte[] toByteArray(ArrayList<Byte> in) {
	    final int n = in.size();
	    byte ret[] = new byte[n];
	    for (int i = 0; i < n; i++) {
	        ret[i] = in.get(i).byteValue();
	    }
	    return ret;
	}

	public void send(int conId, Message message) throws IOException {
		System.out.println("QUEUE MANAGER send with connection " + conId);
		byte[] bytes = toByteArray(message.toBytes());
		
		connections.get(conId).setSendMessage(bytes);
	}

	public void receive() throws IOException, ClassNotFoundException {
		Socket socket = requestHandler.receive();

		int id = idCounter;
		idCounter++;
		
		ServerSocketThread thread = new ServerSocketThread(id, socket);
		
		InetAddress ipAddress = socket.getInetAddress();

		connections.put(id, new ServerSocketThread(thread.getThreadId(), socket));
		
		thread = connections.get(id);
		thread.start();
		
		System.out.println("QUEUE MANAGER - waiting for operation and message received");
		
		while(thread.getOperation() == null);
		Operation operation = thread.getOperation();
		
		thread.setOperation(null);
		
		int threadId = thread.getThreadId();
		Message message = null;
		
		addToQueue(operation, threadId, ipAddress, message);
		
		connections.put(id, thread);
		
	}
	
	public static void addToQueue(Operation operation, int id, InetAddress inetAddress, Message message) {
		System.out.println("QM - adding " + operation + " message to queue");
		switch(operation) {
			case CONNECT:
				System.out.println("QUEUE MANAGER - adding user connection");
				queues.get("connect").enqueue(new ConnectionMessage(id, null));
				break;
			case LIST:
				System.out.println("QUEUE MANAGER - adding list connection");
				queues.get("list").enqueue(new ConnectionMessage(id, null));
				break;
			case PUBLISH:
				System.out.println("QUEUE MANAGER - adding publish connection");
				queues.get("publish").enqueue(new ConnectionMessage(id, message));
				break;
			case SUBSCRIBE:
				System.out.println("QUEUE MANAGER - adding subscribe connection");
				message.getPayload().addField(inetAddress.getHostAddress());
				queues.get("subscribe").enqueue(new ConnectionMessage(id, message));
				break;
		default:
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
