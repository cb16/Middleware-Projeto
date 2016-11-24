package distribution;

import infrastructure.ServerRequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueManager extends Thread implements IQueueManager {
	private String host;
	private int port;
	public static BlockingQueue<Invoker> queue =  new LinkedBlockingQueue<Invoker>(); 
	private ServerRequestHandler requestHandler;
	protected static volatile HashMap<Integer, ConnectionHandler> connections;
	private int idCounter;
	
	public QueueManager(String host, int port) {
		this.host = host;
		this.port = port;
		this.requestHandler = new ServerRequestHandler(this.port);
		QueueManager.connections = new HashMap<Integer, ConnectionHandler>();
		this.idCounter = 0;
	}
	
	public void enqueueSendMessage(ConnectionMessage conMessage) {
		System.out.println("QUEUE MANAGER - enqueuing send message for con id " + conMessage.getConnectionId());
		queue.offer(new Invoker("send", conMessage));
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
				queue.offer(new Invoker("connect", new ConnectionMessage(id, null)));
				break;
			case LIST:
				System.out.println("QUEUE MANAGER - adding list connection");
				queue.offer(new Invoker("list", new ConnectionMessage(id, null)));
				break;
			case PUBLISH:
				System.out.println("QUEUE MANAGER - adding publish connection");
				queue.offer(new Invoker("publish", new ConnectionMessage(id, message)));
				break;
			case SUBSCRIBE:
				System.out.println("QUEUE MANAGER - adding subscribe connection");
				message.getPayload().addField(inetAddress.getHostAddress());
				queue.offer(new Invoker("subscribe", new ConnectionMessage(id, message)));
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
