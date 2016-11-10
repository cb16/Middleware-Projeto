package infrastructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRequestHandler {
	private int port;
	private ServerSocket welcomeSocket = null;
	Socket connectionSocket = null;
	
	int sentMessageSize;
	int receivedMessageSize;
	DataOutputStream outToClient = null;
	DataInputStream inFromClient = null;
	
	public ServerRequestHandler(int port) {
		this.port = port;
	}
	
	public byte[] receive() throws IOException {
		welcomeSocket = new ServerSocket(this.port);
		connectionSocket = welcomeSocket.accept();
		
		inFromClient = new DataInputStream(connectionSocket.getInputStream());
		outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
		receivedMessageSize = inFromClient.readInt();
		byte[] receivedMessage = new byte[receivedMessageSize];
		inFromClient.read(receivedMessage, 0, receivedMessageSize);
		
		return receivedMessage;
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
