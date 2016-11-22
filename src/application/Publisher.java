package application;

import java.io.IOException;
import java.net.SocketException;
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
	
	private static void connect(){
		MessagePayload payload = new MessagePayload();
		payload.addField("MQTT");

		MessageHeader header = new MessageHeader(Operation.CONNECT, payload.length());
		
		Message message = new Message(header);
		message.setPayload(payload);
		
		try {
			publishQueueManagerProxy.send(message, Operation.CONNECT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		in = new Scanner(System.in);
		
		connect();
		
		while(true) {
			if(publishQueueManagerProxy.getRequestHandler().getSocket().isClosed()) {
				System.out.println("Server has fallen!");
				break;
			}
			
			System.out.println("Comandos:\n1- Publicar medição de temperatura");
			
			int num = in.nextInt();
			
			if(num==1) {
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
				int headerLength = payload.length() + optionalHeader.length();
				MessageHeader header = new MessageHeader(Operation.PUBLISH, headerLength);
				
				Message message = new Message(header);
				message.setOptionalHeader(optionalHeader);
				message.setPayload(payload);
				
				try {
					publish(message);
				} catch(SocketException e) {
					System.out.println("Server has fallen!");
					publishQueueManagerProxy.closeConnection();
				}
					
			}
		}
	}
}
