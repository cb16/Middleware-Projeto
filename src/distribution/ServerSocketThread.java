package distribution;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	}
	
	public byte[] getReceivedMessageBytes() {
		return this.receivedMessageBytes;
	}
	
	public Message getReceivedMessage() {
		return this.receivedMessage;
	}
	
	public void setReceivedMessage(Message message) {
		this.receivedMessage = message;
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	public void run() {
		try {
			inFromClient = new DataInputStream(socket.getInputStream());
			outToClient = new DataOutputStream(socket.getOutputStream());
			
			receivedMessageSize = inFromClient.readInt();
			receivedMessageBytes = new byte[receivedMessageSize];
			inFromClient.read(receivedMessageBytes, 0, receivedMessageSize);
			
			Message message = new Message(receivedMessageBytes);
			
			this.operation = message.getHeader().getOperation();
			System.out.println("THREAD message > " + message);
			setReceivedMessage(message);
			
			System.out.println("THREAD IN OPERATION");
			
			while(keepRunning) {
				if(messageToSend != null) {
					//send
					
					System.out.println("THREAD will send");
					
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
		}
		
	}
	
	public void stopRunning() {
		keepRunning = false;
	}
	
	public void setSendMessage(byte[] bytesMessage) {
		messageToSend = bytesMessage;
	}
	
	public int getThreadId() {
		return this.id;
	}
	
	public Socket getSocket(){
		return socket;
	}
}
