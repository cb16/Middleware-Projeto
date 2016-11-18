package distribution;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

public class MessageBody implements Serializable {
	private String location;
	private Date date;
	private double temperature;
	private ArrayList<String> list;
	private String topic;
	private InetAddress ip;
	private int connectionId;
	
	public MessageBody(String location, Date date, double temperature) {
		this.location = location;
		this.date = date;
		this.temperature = temperature;
	}
	
	public MessageBody(ArrayList<String> list) {
		this.list = list;
	}

	public MessageBody(String topic, InetAddress ip) {
		this.topic = topic;
		this.ip = ip;
	}
	
	public String getLocation() {
		return location;
	}

	public Date getDate() {
		return date;
	}

	public double getTemperature() {
		return temperature;
	}

	public ArrayList<String> getList() {
		return list;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public int getConnectionId() {
		return connectionId;
	}
	
	public void setConnectionId(int conId) {
		connectionId = conId;
	}
}
