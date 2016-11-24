package infrastructure;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRequestHandler {
	private int port;
	private ServerSocket welcomeSocket = null;
	
	public ServerRequestHandler(int port) {
		this.port = port;
		try {
			this.welcomeSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Socket accept() throws IOException {
		return welcomeSocket.accept();
	}
}
