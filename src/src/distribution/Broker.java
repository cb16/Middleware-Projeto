package distribution;

import java.io.IOException;
import java.util.ArrayList;

import utils.Config;
import infrastructure.ServerRequestHandler;

public class Broker {
	private static TopicRepository topicRepo;
	private static QueueManager queueManager;
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		while(true) {
			RequestPacket packet = queueManager.receive();
			
			Operation operation = (Operation) packet.getHeader().getOperation();
			
			switch(operation) {
				case LIST:
					Message message = formatListingMessage();
					queueManager.send(message);
					break;
				case PUBLISH:
					MessageBody publishMessageBody = packet.getBody().getMessage().getBody();
					repoPublish(publishMessageBody);
					break;
				case SUBSCRIBE:
					MessageBody subscribeMessageBody = packet.getBody().getMessage().getBody();
					repoSubscribe(subscribeMessageBody);
					break;
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
	
	private static void repoPublish(MessageBody body) {
		
	}
	
	private static void repoSubscribe(MessageBody body) {
		
	}
}
