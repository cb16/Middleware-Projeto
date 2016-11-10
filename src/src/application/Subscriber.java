package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import distribution.Message;
import distribution.QueueManagerProxy;

public class Subscriber {
	QueueManagerProxy subscribeQueueManagerProxy = new QueueManagerProxy("subscribe");
	
	public void subscribe(String topic) throws UnknownHostException, IOException {
		//make subscribe message
		Message message = new Message();
		subscribeQueueManagerProxy.send(message);
	}
	
	public ArrayList<String> list() {
		//subscribeQueueManagerProxy.list()
		ArrayList<String> topicList = new ArrayList<String>();
		return topicList;
	}
	
	public static void main(String[] args) {
		
	}

}
