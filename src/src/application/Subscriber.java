package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.sun.org.apache.xalan.internal.xsltc.runtime.MessageHandler;

import distribution.Message;
import distribution.MessageBody;
import distribution.MessageHeader;
import distribution.Operation;
import distribution.QueueManagerProxy;

public class Subscriber {
	QueueManagerProxy subscribeQueueManagerProxy = new QueueManagerProxy("subscribe");
	
	public void subscribe(String topic) throws UnknownHostException, IOException {
		//formating message
		MessageHeader header = new MessageHeader();
		MessageBody body = new MessageBody(topic);
		Message message = new Message(header, body);
		
		//sending message
		subscribeQueueManagerProxy.send(message, Operation.SUBSCRIBE);
	}
	
	public ArrayList<String> list() throws UnknownHostException, IOException {
		subscribeQueueManagerProxy.send(null, Operation.LIST);
		
		Message listMessage = subscribeQueueManagerProxy.receive();
		
		ArrayList<String> topicList = listMessage.getBody().getList();
		
		return topicList;
	}
	
	public static void main(String[] args) {
		
	}

}
