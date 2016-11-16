package distribution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class MessageBody implements Serializable {
	private String location;
	private Date date;
	private double temperature;
	private ArrayList<String> list;
	private String topic;
	
	public MessageBody(String location, Date date, double temperature) {
		this.location = location;
		this.date = date;
		this.temperature = temperature;
	}
	
	public MessageBody(ArrayList<String> list) {
		this.list = list;
	}

	public MessageBody(String topic) {
		this.topic = topic;
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
}
