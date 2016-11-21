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
	protected static volatile HashMap<Integer, ConnectionHandler> connections;
	private int idCounter;
	
	public QueueManager(String host, int port) {
		this.host = host;
		this.port = port;
		QueueManager.queues = new HashMap<String, Queue>();
		instantiateQueues();
		this.requestHandler = new ServerRequestHandler(this.port);
		this.connections = new HashMap<Integer, ConnectionHandler>();
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
		System.out.println("QUEUE MANAGER - enqueuing send message for con id " + conMessage.getConnectionId());
		queues.get("send").enqueue(conMessage);
	}
	
	public ConnectionHandler getConnection(int conId){
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
		byte[] bytes = toByteArray(message.toBytes());
		
		connections.get(conId).setSendMessage(bytes);
	}

	public void receive() throws IOException, ClassNotFoundException {
		Socket socket = requestHandler.receive();

		int id = idCounter;
		idCounter++;
		
		ConnectionHandler thread = new ConnectionHandler(id, socket);
		
		InetAddress ipAddress = socket.getInetAddress();

		connections.put(id, new ConnectionHandler(thread.getThreadId(), socket));
		
		thread = connections.get(id);
		thread.start();
		
		while(thread.getOperation() == null);
		Operation operation = thread.getOperation();
		
		thread.setOperation(null);
		
		int threadId = thread.getThreadId();
		Message message = null;
		
		addToQueue(operation, threadId, ipAddress, message);
		
		connections.put(id, thread);
		
	}
	
	public static void addToQueue(Operation operation, int id, InetAddress inetAddress, Message message) {
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
	
	public void deleteConnection(int conId) {
		System.out.println("removing connection " + conId);
		connections.remove(conId);
	}

	public void sendPublicationToSubscribers(Message message, ArrayList<SubscribeUser> users) {
		for(SubscribeUser user : users) {
			int userConId = getUserConId(user);
			if(userConId != -1)
				enqueueSendMessage(new ConnectionMessage(userConId, message));
		}		
	}
	
	public int getUserConId(SubscribeUser user) {
		for(Integer id : connections.keySet()) {
			ConnectionHandler thread = connections.get(id);
			if(thread.getSocket().getInetAddress() == user.getIP()) {
				return id;
			}
		}
		return -1;
	}
}
