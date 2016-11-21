package distribution;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
		while(keepRunning) {
			receive();
		}
	}
	
	public void receive() {
		try {
			inFromClient = new DataInputStream(socket.getInputStream());
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			
			receivedMessageSize = inFromClient.readInt();
			synchronized(this) {
				receivedMessageBytes = new byte[receivedMessageSize];
				inFromClient.read(receivedMessageBytes, 0, receivedMessageSize);
				
				Message message = new Message(receivedMessageBytes);
				
				this.operation = message.getHeader().getOperation();
				setReceivedMessage(message);
			}
			
		} catch (EOFException e) {
			stopRunning();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void send() {
		System.out.println("SERVER sending response");
		
		sentMessageSize = messageToSend.length;
		
		try {	
			outToClient.writeInt(sentMessageSize);
			outToClient.write(messageToSend, 0, sentMessageSize);
			outToClient.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setOperation(Operation op) {
		operation = op;
	}
	
	public void stopRunning() {
		System.out.println("BROKER - Connection " + this.id + " has been terminated");
		
		keepRunning = false;
		try {
			socket.close();
			outToClient.close();
			inFromClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		QueueManager.connections.remove(this.id);
		
	}
	
	public void setSendMessage(byte[] bytesMessage) {
		messageToSend = bytesMessage;
		send();
	}
	
	public int getThreadId() {
		return this.id;
	}
	
	public Socket getSocket(){
		return socket;
	}
}
