package application;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import distribution.Message;
import distribution.MessageHeader;
import distribution.MessageOptionalHeader;
import distribution.Operation;
import distribution.MessagePayload;
import distribution.QueueManagerProxy;

public class Publisher {
	private QueueManagerProxy publishQueueManagerProxy;
	
	public Publisher() {
		publishQueueManagerProxy = new QueueManagerProxy("publish");
	}
	
	public void publish(Date date, String topic, double temp) throws UnknownHostException, IOException {
		MessagePayload payload = new MessagePayload();
		payload.addField(date.toString());
		payload.addField(Double.toString(temp));
		
		MessageOptionalHeader optionalHeader = new MessageOptionalHeader();
		optionalHeader.addField(topic);
		int headerLength = payload.length() + optionalHeader.length();
		MessageHeader header = new MessageHeader(Operation.PUBLISH, headerLength);
		
		Message message = new Message(header);
		message.setOptionalHeader(optionalHeader);
		message.setPayload(payload);
		
		try {
			publishQueueManagerProxy.send(message, Operation.PUBLISH);
		} catch(SocketException e) {
			System.out.println("Server has fallen!");
			publishQueueManagerProxy.closeConnection();
		}
		
	}
	
	public void connect(String id){
		MessageOptionalHeader optionalHeader = new MessageOptionalHeader();
		optionalHeader.addField("MQTT");
		
		MessagePayload payload = new MessagePayload();
		payload.addField(id);

		int remainingLength = optionalHeader.length() + payload.length();
		MessageHeader header = new MessageHeader(Operation.CONNECT, remainingLength);
		
		Message message = new Message(header);
		message.setPayload(payload);
		
		try {
			publishQueueManagerProxy.send(message, Operation.CONNECT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public QueueManagerProxy getQueueManagerProxy() {
		return publishQueueManagerProxy;
	}
	
	
}
