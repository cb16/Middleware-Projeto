package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import distribution.Message;
import distribution.MessageBody;
import distribution.MessageHeader;
import distribution.Operation;
import distribution.QueueManagerProxy;

public class Publisher {
	private static QueueManagerProxy publishQueueManagerProxy = new QueueManagerProxy("publish");
	
	public static void publish(Message message) throws UnknownHostException, IOException {
		publishQueueManagerProxy.send(message, Operation.PUBLISH);
	}
	
	public static ArrayList<String> list() throws UnknownHostException, IOException, ClassNotFoundException {
		publishQueueManagerProxy.send(null, Operation.LIST);
		
		Message listMessage = publishQueueManagerProxy.receive(true);
		
		ArrayList<String> topicList = listMessage.getBody().getList();
		
		return topicList;
	}
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		Scanner in = new Scanner(System.in);
		
		while(true) {
			System.out.println("Commands:\n1- list topics\n2- publish");
			
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
				
			} else if(num==2) {
				System.out.println("Type topic:");
				in.nextLine();
				String topic = in.nextLine();
				Date date = new Date(System.currentTimeMillis());
				System.out.println("Type temperature:");
				double temp = in.nextDouble();
				MessageBody body = new MessageBody(topic, date, temp);
				MessageHeader header = new MessageHeader();
				
				Message message = new Message(header, body);
				publish(message);
			}
		}
	}
}
