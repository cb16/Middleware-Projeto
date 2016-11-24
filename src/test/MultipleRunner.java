package test;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import application.Publisher;
import application.Subscriber;

public class MultipleRunner {
	private static final int subscriberLimit = 100;
	private static final int publisherLimit = 1;
	
	public static void main(String[] args) {
		Subscriber subscribers[] = new Subscriber[subscriberLimit];
		
		Publisher publishers[] = new Publisher[publisherLimit];
		
		//INITIALIZING PUBLISHERS AND SUBSCRIBERS
		
		for(int i = 0; i < subscriberLimit; i++) {
			Subscriber sub = new Subscriber();
			sub.connect("sub-"+i);
			sub.start();
			sub.sentMessage = true;
			subscribers[i] = sub;
		}
		
		for(int i = 0; i < publisherLimit; i++) {
			Publisher pub = new Publisher();
			pub.connect("pub"+i);
			publishers[i] = pub;
		}
		
		//SUBSCRIBING
		
		for(int i = 0; i < subscriberLimit; i++) {
			try {
				if(subscribers[i].keepRunning)
					subscribers[i].subscribe("Recife");
				else
					System.out.println("Server has fallen!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//PUBLISHING
		
		System.out.println("WILL PUBLISH");
		
		for(int i = 0; i < publisherLimit; i++) {
			try {
				publishers[i].publish(new Date(System.currentTimeMillis()), "Recife", i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
