package application;

import java.io.IOException;
import java.util.Scanner;


public class SubscriberRunner {
	private static Scanner in;

	public static void main(String[] args) {
		Subscriber subscriber = new Subscriber();
		
		in = new Scanner(System.in);
		
		//String id = in.nextLine();
		//subscriber.connect(id);
		
		subscriber.connect("sub");
		
		subscriber.sentMessage = true;
		
		subscriber.start();
		
		while(true) {
			
			System.out.println("Comandos:\n1- Subscribe");
			
			int num = in.nextInt();
			
			if(num == 1) {
				System.out.println("Digite a localização que vocẽ tem interesse");
				in.nextLine();
				String topic = in.nextLine();
				try {
					if(subscriber.keepRunning)
						subscriber.subscribe(topic);
					else
						System.out.println("Invalid Operation - Server has fallen.");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
