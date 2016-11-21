package infrastructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import distribution.ServerSocketThread;

public class ServerRequestHandler {
	private int port;
	private ServerSocket welcomeSocket = null;
	private Socket connectionSocket = null;
	private static int idCounter;
	
	int sentMessageSize;
	int receivedMessageSize;
	DataOutputStream outToClient = null;
	DataInputStream inFromClient = null;
	
	public ServerRequestHandler(int port) {
		this.port = port;
		idCounter = 0;
		
		try {
			this.welcomeSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Socket receive() throws IOException {
		
		connectionSocket = welcomeSocket.accept();
		
		return connectionSocket;
	}
	
	public void send(byte[] message) throws IOException {
		
		sentMessageSize = message.length;
		outToClient.writeInt(sentMessageSize);
		outToClient.write(message, 0, sentMessageSize);
		
		connectionSocket.close();
		welcomeSocket.close();
		outToClient.close();
		inFromClient.close();
	}

}
