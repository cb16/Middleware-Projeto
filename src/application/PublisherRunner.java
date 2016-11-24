package application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

public class PublisherRunner {
	private static Scanner in;

	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		in = new Scanner(System.in);
		
		Publisher pub = new Publisher();
		
		pub.connect("pub");
		
		while(true) {
			if(pub.getQueueManagerProxy().getRequestHandler().getSocket().isClosed()) {
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
				
				pub.publish(date, topic, temp);
					
			}
		}
	}
}
