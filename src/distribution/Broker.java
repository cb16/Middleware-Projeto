package distribution;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import utils.Config;

public class Broker extends Thread {
	private static TopicRepository topicRepo;
	private static QueueManager queueManager;
	private static String listDelimiter = "\r\n";
	private static HashMap<InetAddress, SubscribeUser> userRepo = new HashMap<>();
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		topicRepo = new TopicRepository();
		
		queueManager = new QueueManager("localhost", Config.port);
		
		queueManager.start();
		
		Broker broker = new Broker();
		
		broker.start();
		
		System.out.println("Threads running");
	}
	
	public void run() {
		Message message;
		ConnectionMessage conMessage;
		
		while(true) {
			try {
				Invoker invoker = QueueManager.queue.take();
				String operation = invoker.getOp();
				conMessage = invoker.getConnectionMessage();

				if(operation == "connect") {
					int conId = conMessage.getConnectionId();
					Socket socket = queueManager.getConnection(conId).getSocket();
					InetAddress IPAdress = socket.getInetAddress();

					SubscribeUser user = new SubscribeUser(IPAdress, Config.port);
					userRepo.put(IPAdress, user);
					
					System.out.println("BROKER received connect request");
					Message sendMessage = formatConnectMessage();
					QueueManager.queue.offer(new Invoker("send", new ConnectionMessage(conMessage.getConnectionId(), sendMessage)));
				}
				
				if(operation == "list") {
					message = conMessage.getMessage();
					System.out.println("BROKER received list request");
					Message sendMessage = formatListingMessage();
					QueueManager.queue.offer(new Invoker("send", new ConnectionMessage(conMessage.getConnectionId(), sendMessage)));
				}
				
				if(operation == "publish") {
					message = conMessage.getMessage();
					System.out.println("BROKER received publish message");
					repoPublish(conMessage.getConnectionId(), message);
				}
				
				if(operation == "subscribe") {
					message = conMessage.getMessage();
					System.out.println("BROKER received subscribe message");
					repoSubscribe(message);
				}
				
				if(operation == "send") {
					message = conMessage.getMessage();
					System.out.println("BROKER enqueuing send response message");
					try {
						queueManager.send(conMessage.getConnectionId(), message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}
	}
	
	private static Message formatListingMessage() {
		ArrayList<String> topics = topicRepo.getTopics();
		MessagePayload payload = new MessagePayload();
		
		payload.addField(String.join(listDelimiter, topics));
		
		MessageHeader header = new MessageHeader(Operation.LIST, payload.length());
		
		Message message = new Message(header);
		message.setPayload(payload);
		
		return message;
	}
	
	public static Message formatConnectMessage(){
		MessageHeader header = new MessageHeader(Operation.CONNACK, 0);
		Message message = new Message(header);
		
		return message;
	}
	
	private static void repoPublish(int conId, Message message) {
		ArrayList<String> fields = message.getOptionalHeader().getFields();
		System.out.println(fields);
		String topic = fields.get(0);
		
		if(!topicRepo.getTopics().contains(topic))
			createTopicInRepo(topic);
		
		topicRepo.addPublication(topic, message);
		
		checkTopicSubscribersAndSend(topic, message, conId);
	}
	
	private static void repoSubscribe(Message message) {
		ArrayList<String> fields = message.getPayload().getFields();
		String topic = fields.get(0);
		InetAddress IPAdress;
		
		try {
			IPAdress = InetAddress.getByName(fields.get(1));
			
			SubscribeUser user = userRepo.get(IPAdress);
			
			if(user != null){
				if(!topicRepo.getTopics().contains(topic))
					createTopicInRepo(topic);
				
				topicRepo.addSubscriber(topic, user);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void createTopicInRepo(String topic) {
		topicRepo.addTopic(topic);
		topicRepo.addTopicSubscribe(topic);
		topicRepo.addTopicPublish(topic);
	}
	
	private static void checkTopicSubscribersAndSend(String topic, Message message, int conId) {
		ArrayList<SubscribeUser> users = topicRepo.getTopicSubscribersRepo().get(topic);
		System.out.println(">Subscribers for " + topic);
		System.out.println(users);
		
		queueManager.sendPublicationToSubscribers(message, users);
	}
}
