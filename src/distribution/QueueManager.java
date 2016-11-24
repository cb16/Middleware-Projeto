package distribution;

import infrastructure.ServerRequestHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueManager extends Thread implements IQueueManager {
	private int port;
	public static BlockingQueue<Invoker> queue =  new LinkedBlockingQueue<Invoker>(); 
	private ServerRequestHandler requestHandler;
	protected static volatile HashMap<String, ConnectionHandler> connections;

	public QueueManager(int port) {
		this.port = port;
		this.requestHandler = new ServerRequestHandler(this.port);
		QueueManager.connections = new HashMap<String, ConnectionHandler>();
	}
	
	public void enqueueSendMessage(ConnectionMessage conMessage) {
		System.out.println("QUEUE MANAGER - enqueuing send message for con id " + conMessage.getConnectionId());
		queue.offer(new Invoker("send", conMessage));
	}
	
	public ConnectionHandler getConnection(String conId){
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

	public void send(String conId, Message message) throws IOException {
		byte[] bytes = toByteArray(message.toBytes());
		
		connections.get(conId).setSendMessage(bytes);
	}

	public void receive() throws IOException, ClassNotFoundException {
		Socket socket = requestHandler.accept();
		String id;
		Message message;
		
		ConnectionHandler thread = new ConnectionHandler(socket);

		message = thread.connect();
		
		
		if(message != null){
			id = message.getPayload().getFields().get(0);
			thread.setId(id);
			
			System.out.println("Connection: "+connections.get(id));
			if(connections.get(id) == null){
				// doesn't set a new thread if is a returning user
				thread.start();
				connections.put(id, thread);
			} else {
				connections.get(id).setSocket(socket);
			}
			thread.setReceivedMessage(message);
		}
	}
	
	public static void addToQueue(Operation operation, String id, InetAddress inetAddress, Message message) {
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
				queue.offer(new Invoker("subscribe", new ConnectionMessage(id, message)));
				break;
		default:
			break;
		}
	}
	
	public void run() {
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
			String userConId = user.getId();
			if(userConId != "")
				enqueueSendMessage(new ConnectionMessage(userConId, message));
		}		
	}
}
