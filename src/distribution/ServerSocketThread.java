package distribution;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ServerSocketThread extends Thread {
	private Socket socket;
	private int id;
	
	int sentMessageSize;
	int receivedMessageSize;
	DataOutputStream outToClient = null;
	DataInputStream inFromClient = null;
	
	byte[] messageToSend;
	byte[] receivedMessageBytes;
	
	Message receivedMessage;
	
	volatile boolean keepRunning;

	Operation operation;
	
	public ServerSocketThread(int id, Socket socket) {
		this.socket = socket;
		this.receivedMessage = null;
		this.receivedMessageBytes = null;
		this.id = id;
		keepRunning = true;
		messageToSend = null;
		operation = null;

		try {
			inFromClient = new DataInputStream(socket.getInputStream());
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public byte[] getReceivedMessageBytes() {
		return this.receivedMessageBytes;
	}
	
	public Message getReceivedMessage() {
		return this.receivedMessage;
	}
	
	public void setReceivedMessage(Message message) {
		QueueManager.addToQueue(message.getHeader().getOperation(), id, socket.getInetAddress(), message);
		this.receivedMessage = message;
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	public void run() {
		while(true) {
			receive();
		}
	}
	
	public void receive() {
		System.out.println("in thread receive");
		try {
			inFromClient = new DataInputStream(socket.getInputStream());
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			
			receivedMessageSize = inFromClient.readInt();
			System.out.println("read int");
			synchronized(this) {
				receivedMessageBytes = new byte[receivedMessageSize];
				inFromClient.read(receivedMessageBytes, 0, receivedMessageSize);
				
				Message message = new Message(receivedMessageBytes);
				
				this.operation = message.getHeader().getOperation();
				System.out.println("new operation " + this.operation);
				System.out.println("THREAD message > " + message);
				setReceivedMessage(message);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void send() {
		System.out.println("THREAD will send");
		
		sentMessageSize = messageToSend.length;
		
		try {	
			outToClient.writeInt(sentMessageSize);
			outToClient.write(messageToSend, 0, sentMessageSize);
			outToClient.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("THREAD sent");
	}
	
	public void setOperation(Operation op) {
		operation = op;
	}
	
	public void stopRunning() {
		keepRunning = false;
		try {
			socket.close();
			outToClient.close();
			inFromClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setSendMessage(byte[] bytesMessage) {
		messageToSend = bytesMessage;
		send();
	}
	
	public int getThreadId() {
		return this.id;
	}
	
	public Socket getSocket(){
		System.out.println("is scoket null ? " + (socket==null? true:false));
		return socket;
	}
}
