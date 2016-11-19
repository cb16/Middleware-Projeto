package distribution;

import java.util.ArrayList;

public class Queue {
	public ArrayList<ConnectionMessage> queue;
	
	public Queue() {
		this.queue = new ArrayList<ConnectionMessage>();
	}
	
	public void enqueue(ConnectionMessage conMessage) {
		this.queue.add(conMessage);
	}
	
	public ConnectionMessage dequeue() {
		if(!this.queue.isEmpty())
			return this.queue.remove(0);
		return null;
	}
	
	public int queueSize() {
		return this.queue.size();
	}

}
