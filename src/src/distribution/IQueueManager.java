package distribution;

public interface IQueueManager {
	public void send(); // coloca na fila mensagens que vão ser enviadas para subscribers
	
	public void receive(); // coloca na fila de mensagens recebida (vindas de publishers e subscribers) 

}
