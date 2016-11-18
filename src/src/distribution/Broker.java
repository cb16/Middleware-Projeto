package distribution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import utils.Config;
import infrastructure.ServerRequestHandler;

public class Broker extends Thread {
	private static TopicRepository topicRepo;
	private static QueueManager queueManager;
	
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
		
		while(true) {
			
			if(queueManager.queues.get("list").queueSize() > 0) {
				message = queueManager.queues.get("list").dequeue();
				System.out.println("broker received list request");
				Message sendMessage = formatListingMessage();
				queueManager.queues.get("send").enqueue(sendMessage);
			}
			
			if(queueManager.queues.get("publish").queueSize() > 0) {
				message = queueManager.queues.get("publish").dequeue();
				System.out.println("broker received publish message");
				repoPublish(message);
			}
			
			if(queueManager.queues.get("subscribe").queueSize() > 0) {
				message = queueManager.queues.get("subscribe").dequeue();
				System.out.println("broker received subscribe message");
				repoSubscribe(message);
			}
			
			if(queueManager.queues.get("send").queueSize() > 0) {
				message = queueManager.queues.get("send").dequeue();
				System.out.println("broker sending response message");
				try {
					queueManager.send(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private static Message formatListingMessage() {
		MessageHeader header = new MessageHeader();
		
		ArrayList<String> topics = topicRepo.getTopics();
		
		MessageBody body = new MessageBody(topics);
		
		Message message = new Message(header, body);
		
		return message;
	}
	
	private static void repoPublish(Message message) {
		String topic = message.getBody().getLocation();
		
		if(!topicRepo.getTopics().contains(topic))
			createTopicInRepo(topic);
		
		topicRepo.addPublication(topic, message);
		
		checkTopicSubscribersAndSend(topic);
	}
	
	private static void repoSubscribe(Message message) {
		String topic = message.getBody().getTopic();
		
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
	
	private static void checkTopicSubscribersAndSend(String topic) {
		ArrayList<SubscribeUser> users = topicRepo.getTopicSubscribersRepo().get(topic);
		System.out.println(">Subscribers for " + topic);
		System.out.println(users);
	}
}
