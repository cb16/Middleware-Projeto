package infrastructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import distribution.Marshaller;
import distribution.Message;
import distribution.Operation;
import distribution.RequestPacket;
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
		this.idCounter = 0;
	}
	
	public ServerSocketThread receive() throws IOException {
		System.out.println("server waiting for request");
		welcomeSocket = new ServerSocket(this.port);
		connectionSocket = welcomeSocket.accept();
		
		byte[] receivedMessage;
		
		ServerSocketThread connectionThread = new ServerSocketThread(idCounter, connectionSocket);
		connectionThread.run();
		
		idCounter++;

		//while((receivedMessage = connectionThread.getReceivedMessage()) == null) continue;
		
		/*inFromClient = new DataInputStream(connectionSocket.getInputStream());
		outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
		receivedMessageSize = inFromClient.readInt();
		 = new byte[receivedMessageSize];
		inFromClient.read(receivedMessage, 0, receivedMessageSize);
		
		if(!messageHasResponse(receivedMessage)) {
			connectionSocket.close();
			welcomeSocket.close();
			outToClient.close();
			inFromClient.close();
		}*/
		
		return connectionThread;
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
	
	public boolean messageHasResponse(byte[] bytes) {
		Marshaller marsh = new Marshaller();
		
		RequestPacket p;
		try {
			p = marsh.unmarshallRequestPacket(bytes);
			
			Operation op = (Operation) p.getHeader().getOperation();
			if(op == Operation.LIST)
				return true;
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
