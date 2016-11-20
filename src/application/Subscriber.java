package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import distribution.Message;
import distribution.MessageHeader;
import distribution.MessageOptionalHeader;
import distribution.Operation;
import distribution.Payload;
import distribution.QueueManagerProxy;

public class Subscriber extends Thread {
	static QueueManagerProxy subscribeQueueManagerProxy = new QueueManagerProxy("subscribe");
	private static Scanner in;
	
	public static void subscribe(String topic) throws UnknownHostException, IOException {
		//formating message
		Payload payload = new Payload();
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
	}
	
	public static ArrayList<String> list() throws UnknownHostException, IOException, ClassNotFoundException {
		subscribeQueueManagerProxy.send(null, Operation.LIST);
		
		Message listMessage = subscribeQueueManagerProxy.receive(true);
		
		ArrayList<String> topicList = listMessage.getPayload().getList();
		
		return topicList;
	}
	
	public static Message receive() throws ClassNotFoundException, IOException {
		return subscribeQueueManagerProxy.receive(false);
	}
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		in = new Scanner(System.in);
		
		while(true) {
			System.out.println("Comandos:\n1- Listar localizações\n2- Subscribe");
			
			int num = in.nextInt();
			
			if(num == 1) {
				ArrayList<String> tops = list();
				if(tops.size() == 0)
					System.out.println("Não existem tópicos listados");
				else {
					for(String t : tops) {
						System.out.println("- " + t);
					}	
				}
				
			} else if(num == 2) {
				System.out.println("Digite a localização que vocẽ tem interesse");
				in.nextLine();
				String topic = in.nextLine();
				subscribe(topic);
			}
		}
	}
	
	public void run() {
		while(true) {
			try {
				receive();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
