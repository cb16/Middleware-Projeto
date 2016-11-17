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
				System.out.println("answer from list");
				System.out.println(sendMessage.getBody());
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
				
			}
			
			if(queueManager.queues.get("send").queueSize() > 0) {
				message = queueManager.queues.get("send").dequeue();
				System.out.println("broker sending response message");
				System.out.println(message.getBody().getLocation());
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
		
		if(topicRepo.getTopics().contains(topic)) {
			ArrayList<Message> topicPub = topicRepo.getTopicPublicationsRepo().get(topic);
			topicPub.add(message);
			topicRepo.getTopicPublicationsRepo().put(topic, topicPub);
		} else {
			topicRepo.addTopic(topic);
			ArrayList<Message> topicPub = new ArrayList<Message>();
			topicPub.add(message);
			topicRepo.getTopicPublicationsRepo().put(topic, topicPub);
		}
	}
	
	private static void repoSubscribe(MessageBody body) {
		
	}
}
