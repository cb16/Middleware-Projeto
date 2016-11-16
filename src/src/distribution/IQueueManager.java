package distribution;

import java.io.IOException;

public interface IQueueManager {
	public void send(Message message); // coloca na fila mensagens que vão ser enviadas para subscribers
	
	public RequestPacket receive() throws IOException, ClassNotFoundException; // coloca na fila de mensagens recebida (vindas de publishers e subscribers) 

}
