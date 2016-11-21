package infrastructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientRequestHandler {
	private String host;
	private int port;
	
	int sentMessageSize;
	int receivedMessageSize;
	
	Socket clientSocket = null;
	DataOutputStream outToServer = null;
	DataInputStream inFromServer = null;
	
	public ClientRequestHandler(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			clientSocket = new Socket(this.host, this.port);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new DataInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized void send(byte[] message) throws UnknownHostException, IOException {
		sentMessageSize = message.length;
		outToServer.writeInt(sentMessageSize);
		outToServer.write(message, 0, sentMessageSize);
		outToServer.flush();
	}
	
	public byte[] receive() throws IOException {
		byte[] message = null;
		
		receivedMessageSize = inFromServer.readInt();
		message = new byte[receivedMessageSize];
		inFromServer.read(message, 0, receivedMessageSize);

		return message;
	}
}
