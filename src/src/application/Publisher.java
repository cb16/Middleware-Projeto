package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import distribution.Message;
import distribution.Operation;
import distribution.QueueManagerProxy;

public class Publisher {
	private static QueueManagerProxy publishQueueManagerProxy = new QueueManagerProxy("publish");
	
	public static void publish(Message message) throws UnknownHostException, IOException {
		publishQueueManagerProxy.send(message, Operation.PUBLISH);
	}
	
	public static ArrayList<String> list() throws UnknownHostException, IOException {
		publishQueueManagerProxy.send(null, Operation.LIST);
		
		Message listMessage = publishQueueManagerProxy.receive();
		
		ArrayList<String> topicList = listMessage.getBody().getList();
		
		return topicList;
	}
	
	public static void main(String[] args) {
		
	}
}
