package application;

import java.io.IOException;
import java.net.UnknownHostException;

import distribution.Message;
import distribution.QueueManagerProxy;

public class Publisher {
	QueueManagerProxy publishQueueManagerProxy = new QueueManagerProxy("publish");
	
	public void publish(Message message) throws UnknownHostException, IOException {
		publishQueueManagerProxy.send(message);
	}
	
	public static void main(String[] args) {
		
	}
}
