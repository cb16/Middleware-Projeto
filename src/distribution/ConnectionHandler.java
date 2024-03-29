package distribution;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionHandler{
	private Socket socket;
	private String id;
	
	int sentMessageSize;
	int receivedMessageSize;
	DataOutputStream outToClient = null;
	DataInputStream inFromClient = null;
	
	private BlockingQueue<byte[]> queue =  new LinkedBlockingQueue<byte[]>(); 
	
	byte[] receivedMessageBytes;
		
	volatile boolean keepRunning;

	Operation operation;
	
	public ConnectionHandler(Socket socket) {
		setSocket(socket);
		this.receivedMessageBytes = null;
		keepRunning = false;
		operation = null;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public byte[] getReceivedMessageBytes() {
		return this.receivedMessageBytes;
	}
	
	
	public void setReceivedMessage(Message message) {
		System.out.println("setting received message - op " + message.getHeader().getOperation());
		QueueManager.addToQueue(message.getHeader().getOperation(), id, socket.getInetAddress(), message);
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	private void startSendThread(){			
		Thread sendThread = (new Thread() {
			public void run() {
				byte[] toSend = null;
				while(keepRunning){
					try {
						toSend = queue.take();
						send(toSend);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						queue.offer(toSend);
						stopRunning();
					}
				}
			}
		});
		
		sendThread.start();
	}
	
	private void startReceiveThread(){
		Thread receiveThread = (new Thread() {
			public void run() {
				while(keepRunning) {
					receive();
				}
			}
		});
		
		receiveThread.start();
	}
	
	public void start() {
		if(!keepRunning){
			keepRunning = true;
			startSendThread();
			startReceiveThread();
		}
	}
	
	public void receive() {
		try {
			inFromClient = new DataInputStream(socket.getInputStream());
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
				System.out.println("operation new " + message.getHeader().getOperation());
				this.operation = message.getHeader().getOperation();
				setReceivedMessage(message);
			}
			
		} catch (EOFException e) {
			stopRunning();
		} catch (SocketException e){
			stopRunning();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	public Message connect(){
		try {
			inFromClient = new DataInputStream(socket.getInputStream());
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
				System.out.println("operation new " + message.getHeader().getOperation());
				this.operation = message.getHeader().getOperation();
				
				return message;
			}
			
		} catch (EOFException e) {
			stopRunning();
		} catch (SocketException e){
			stopRunning();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void send(byte[] messageToSend) throws IOException {
		System.out.println("SERVER sending response");
		
		sentMessageSize = messageToSend.length;
	
		outToClient.writeInt(sentMessageSize);
		outToClient.write(messageToSend, 0, sentMessageSize);
		outToClient.flush();
		
	}
	
	public void setOperation(Operation op) {
		operation = op;
	}
	
	public void stopRunning() {
		if(keepRunning){
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
		}
	}
	
	public void setSendMessage(byte[] bytesMessage) {
		queue.offer(bytesMessage);
	}
	
	public String getThreadId() {
		return this.id;
	}
	
	public Socket getSocket(){
		return socket;
	}

	public void setSocket(Socket socket) {
		if(!keepRunning){
			this.socket = socket;
			
			try {
				inFromClient = new DataInputStream(socket.getInputStream());
				outToClient = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
