package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import distribution.Message;
import distribution.MessageHeader;
import distribution.MessageOptionalHeader;
import distribution.Operation;
import distribution.MessagePayload;
import distribution.QueueManagerProxy;

public class Publisher {
	private static QueueManagerProxy publishQueueManagerProxy = new QueueManagerProxy("publish");
	private static Scanner in;
	
	public static void publish(Message message) throws UnknownHostException, IOException {
		publishQueueManagerProxy.send(message, Operation.PUBLISH);
	}
	
	public static ArrayList<String> list() throws UnknownHostException, IOException, ClassNotFoundException {
		publishQueueManagerProxy.send(null, Operation.LIST);
		
		Message listMessage = publishQueueManagerProxy.receive(true);
		
		ArrayList<String> topicList = listMessage.getPayload().getList();
		
		return topicList;
	}
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		in = new Scanner(System.in);
		
		while(true) {
			System.out.println("Comandos:\n1- Listar Localizações\n2- Publicar medição de temperatura");
			
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
				System.out.println("Digite a localização da medição:");
				
				in.nextLine();
				String topic = in.nextLine();
				
				
				Date date = new Date(System.currentTimeMillis());
				
				System.out.println("Digite a temperatura:");
				double temp = in.nextDouble();
				
				MessagePayload payload = new MessagePayload();
				payload.addField(date.toString());
				payload.addField(Double.toString(temp));
				
				MessageOptionalHeader optionalHeader = new MessageOptionalHeader();
				optionalHeader.addField(topic);
				optionalHeader.addField("1"); // ID
				int headerLength = payload.length() + optionalHeader.length();
				MessageHeader header = new MessageHeader(Operation.PUBLISH, headerLength);
				
				Message message = new Message(header);
				message.setOptionalHeader(optionalHeader);
				message.setPayload(payload);
				
				publish(message);
			}
		}
	}
}
