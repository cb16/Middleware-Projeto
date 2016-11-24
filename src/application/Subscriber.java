package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import distribution.Message;
import distribution.MessageHeader;
import distribution.MessageOptionalHeader;
import distribution.Operation;
import distribution.MessagePayload;
import distribution.QueueManagerProxy;

public class Subscriber extends Thread {
	QueueManagerProxy subscribeQueueManagerProxy = new QueueManagerProxy("subscribe");
	public boolean sentMessage;
	ArrayList<Message> receivedMessages;
	public boolean keepRunning;
	
	public Subscriber() {
		receivedMessages = new ArrayList<Message>();
		keepRunning = true;
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
			subscribeQueueManagerProxy.send(message, Operation.CONNECT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void subscribe(String topic) throws UnknownHostException, IOException {
		//formating message
		MessagePayload payload = new MessagePayload();
		payload.addField(topic);
		MessageOptionalHeader optionalHeader = new MessageOptionalHeader();
		optionalHeader.addField("0"); // ID
		int length = payload.length() + optionalHeader.length();
		MessageHeader header = new MessageHeader(Operation.SUBSCRIBE, length);
		
		Message message = new Message(header);
		message.setOptionalHeader(optionalHeader);
		message.setPayload(payload);
		
		//sending message
		subscribeQueueManagerProxy.send(message, Operation.SUBSCRIBE);
		this.sentMessage = true;
	}
	
	public Message receive() throws ClassNotFoundException, IOException {
		return subscribeQueueManagerProxy.receive();
	}
	
	public void finish() {
		keepRunning = false;
	}
	
	public void run() {
		while(keepRunning) {
			try {
				if(this.sentMessage) {
					Message mes = receive();
					if(mes != null && !mes.getPayload().getFields().isEmpty()) {
						System.out.println("MENSAGEM RECEBIDA!");
						System.out.println("Localização: " + mes.getOptionalHeader().getFields());
						System.out.println("Dados: " + mes.getPayload().getFields());
					} else {
						if(subscribeQueueManagerProxy.getRequestHandler().getSocket().isClosed())
							finish();
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
