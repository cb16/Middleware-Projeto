package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import distribution.Message;
import distribution.MessageBody;
import distribution.MessageHeader;
import distribution.Operation;
import distribution.QueueManagerProxy;

public class Subscriber {
	static QueueManagerProxy subscribeQueueManagerProxy = new QueueManagerProxy("subscribe");
	
	public static void subscribe(String topic) throws UnknownHostException, IOException {
		//formating message
		MessageHeader header = new MessageHeader();
		MessageBody body = new MessageBody(topic, InetAddress.getLocalHost());
		Message message = new Message(header, body);
		
		//sending message
		subscribeQueueManagerProxy.send(message, Operation.SUBSCRIBE);
	}
	
	public static ArrayList<String> list() throws UnknownHostException, IOException, ClassNotFoundException {
		subscribeQueueManagerProxy.send(null, Operation.LIST);
		
		Message listMessage = subscribeQueueManagerProxy.receive(true);
		
		ArrayList<String> topicList = listMessage.getBody().getList();
		
		return topicList;
	}
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		Scanner in = new Scanner(System.in);
		
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

}
