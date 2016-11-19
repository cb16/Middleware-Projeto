package infrastructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientRequestHandler {
	private String host;
	private int port;
	private boolean expectedReply;
	
	int sentMessageSize;
	int receivedMessageSize;
	
	Socket clientSocket = null;
	DataOutputStream outToServer = null;
	DataInputStream inFromServer = null;
	
	public ClientRequestHandler(String host, int port, boolean expectedReply) {
		this.host = host;
		this.port = port;
		this.expectedReply = expectedReply;
	}
	
	public void send(byte[] message) throws UnknownHostException, IOException {
		clientSocket = new Socket(this.host, this.port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new DataInputStream(clientSocket.getInputStream());
		
		sentMessageSize = message.length;
		outToServer.writeInt(sentMessageSize);
		outToServer.write(message, 0, sentMessageSize);
		outToServer.flush();
		
		if(!isExpectedReply()) {
			System.out.println("closed");
			clientSocket.close();
			outToServer.close();
			inFromServer.close();
			return;
		}
	}
	
	public byte[] receive() throws IOException {
		byte[] message = null;
		
		receivedMessageSize = inFromServer.readInt();
		message = new byte[receivedMessageSize];
		inFromServer.read(message, 0, receivedMessageSize);
		
		clientSocket.close();
		outToServer.close();
		inFromServer.close();
		
		return message;
	}
	
	public boolean isExpectedReply() {
		return this.expectedReply;
	}
}
