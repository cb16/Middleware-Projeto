package distribution;

import java.util.ArrayList;

public class Queue {
	public ArrayList<Message> queue;
	
	public Queue() {
		this.queue = new ArrayList<Message>();
	}
	
	public void enqueue(Message message) {
		this.queue.add(message);
	}
	
	public Message dequeue() {
		if(!this.queue.isEmpty())
			return this.queue.remove(0);
		return null;
	}
	
	public int queueSize() {
		return this.queue.size();
	}

}
