package distribution;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import utils.Config;

public class ServerSocketThread extends Thread {
	private Socket socket;
	private int id;
	
	QueueManager qm;
	
	int sentMessageSize;
	int receivedMessageSize;
	DataOutputStream outToClient = null;
	DataInputStream inFromClient = null;
	
	byte[] messageToSend;
	
	byte[] receivedMessage;
	
	volatile boolean keepRunning;
	
	Marshaller marshaller;
	
	public ServerSocketThread(int id, Socket socket) {
		this.socket = socket;
		this.receivedMessage = null;
		this.id = id;
		this.marshaller = new Marshaller();
		this.qm = new QueueManager("localhost", Config.port);
		keepRunning = true;
		messageToSend = null;
	}
	
	public byte[] getReceivedMessage() {
		return this.receivedMessage;
	}
	
	public void run() {
		try {
			inFromClient = new DataInputStream(socket.getInputStream());
			outToClient = new DataOutputStream(socket.getOutputStream());
			
			receivedMessageSize = inFromClient.readInt();
			receivedMessage = new byte[receivedMessageSize];
			inFromClient.read(receivedMessage, 0, receivedMessageSize);
			
			RequestPacket requestPacket = marshaller.unmarshallRequestPacket(receivedMessage);
			
			Operation operation = (Operation) requestPacket.getHeader().getOperation();
			Message message = requestPacket.getBody().getMessage();
			
			System.out.println("IN OPERATION");
			
			switch(operation) {
				case LIST:
					qm.queues.get("list").enqueue(message);
					break;
				case PUBLISH:
					System.out.println("adding");
					Queue q= qm.queues.get("publish");
					q.enqueue(message);
					qm.queues.put("publish", q);
					break;
				case SUBSCRIBE:
					qm.queues.get("subscribe").enqueue(message);
					break;
			}
			
			while(keepRunning) {
				if(messageToSend != null) {
					//send
					
					sentMessageSize = messageToSend.length;
					outToClient.writeInt(sentMessageSize);
					outToClient.write(messageToSend, 0, sentMessageSize);
					
					socket.close();
					outToClient.close();
					inFromClient.close();
					
					keepRunning = false;
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void stopRunning() {
		keepRunning = false;
	}
	
	public void setSendMessage(byte[] bytesMessage) {
		messageToSend = bytesMessage;
	}
}
