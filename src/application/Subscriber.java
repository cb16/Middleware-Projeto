package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import distribution.Message;
import distribution.MessageHeader;
import distribution.MessageOptionalHeader;
import distribution.Operation;
import distribution.MessagePayload;
import distribution.QueueManagerProxy;

public class Subscriber extends Thread {
	QueueManagerProxy subscribeQueueManagerProxy = new QueueManagerProxy("subscribe");
	boolean sentMessage;
	ArrayList<Message> receivedMessages;
	
	public Subscriber() {
		receivedMessages = new ArrayList<Message>();
	}
	
	public void connect(){
		MessagePayload payload = new MessagePayload();
		payload.addField("MQTT");

		MessageHeader header = new MessageHeader(Operation.CONNECT, payload.length());
		
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
		//sentMessage = true;
	}
	
	public ArrayList<String> list() throws UnknownHostException, IOException, ClassNotFoundException {
		subscribeQueueManagerProxy.send(null, Operation.LIST);
		
		Message listMessage = subscribeQueueManagerProxy.receive();
		
		ArrayList<String> topicList = listMessage.getPayload().getList();
		
		return topicList;
	}
	
	public Message receive() throws ClassNotFoundException, IOException {
		return subscribeQueueManagerProxy.receive();
	}
	
	public void run() {
		while(true) {
			try {
				if(this.sentMessage) {
					Message mes = receive();
					if(!mes.getPayload().getFields().isEmpty()) {
						System.out.println("MENSAGEM RECEBIDA!");
						System.out.println("Localização: " + mes.getOptionalHeader().getFields());
						System.out.println("Dados: " + mes.getPayload().getFields());
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
