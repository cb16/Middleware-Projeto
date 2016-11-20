package distribution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import utils.Config;
import infrastructure.ServerRequestHandler;

public class Broker extends Thread {
	private static TopicRepository topicRepo;
	private static QueueManager queueManager;
	private static String listDelimiter = "\r\n";
	
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
			
			if(queueManager.queues.get("list").queueSize() > 0) {
				conMessage = queueManager.queues.get("list").dequeue();
				message = conMessage.getMessage();
				System.out.println("broker received list request");
				Message sendMessage = formatListingMessage();
				queueManager.queues.get("send").enqueue(new ConnectionMessage(conMessage.getConnectionId(), sendMessage));
			}
			
			if(queueManager.queues.get("publish").queueSize() > 0) {
				conMessage = queueManager.queues.get("publish").dequeue();
				message = conMessage.getMessage();
				System.out.println("broker received publish message");
				repoPublish(conMessage.getConnectionId(), message);
			}
			
			if(queueManager.queues.get("subscribe").queueSize() > 0) {
				conMessage = queueManager.queues.get("subscribe").dequeue();
				message = conMessage.getMessage();
				System.out.println("broker received subscribe message");
				repoSubscribe(message);
			}
			
			if(queueManager.queues.get("send").queueSize() > 0) {
				conMessage = queueManager.queues.get("send").dequeue();
				message = conMessage.getMessage();
				System.out.println("broker enqueuing send response message");
				try {
					queueManager.send(conMessage.getConnectionId(), message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	
	private static void repoPublish(int conId, Message message) {
		String topic = message.getBody().getLocation();
		
		if(!topicRepo.getTopics().contains(topic))
			createTopicInRepo(topic);
		
		topicRepo.addPublication(topic, message);
		
		checkTopicSubscribersAndSend(topic, message, conId);
	}
	
	private static void repoSubscribe(Message message) {
		ArrayList<String> fields = message.getPayload().getFields();
		String topic = fields.get(0);
		
		SubscribeUser user = new SubscribeUser(message.getBody().getIP(), Config.port);
		
		if(!topicRepo.getTopics().contains(topic))
			createTopicInRepo(topic);
		
		topicRepo.addSubscriber(topic, user);
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
		
		for(SubscribeUser user : users) {
			queueManager.enqueueSendMessage(new ConnectionMessage(conId, message));
		}
	}
}
