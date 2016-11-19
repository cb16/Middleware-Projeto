package distribution;

import java.util.ArrayList;
import java.util.Arrays;

public class Payload {
	private ArrayList<Byte> content = new ArrayList<>();
	private String listDelimiter = "\r\n";
	
	public ArrayList<String> getList(){
		ArrayList<String> decoded;
		String content = this.content.toString();
		
		decoded = (ArrayList<String>) Arrays.asList(content.split(listDelimiter));
		
		return decoded;
	}
	
	public int length(){
		return content.size();
	}
	
	public void addField(ArrayList<Byte> field){
		content.add((byte) 0);
		content.add((byte) field.size());
		content.addAll(field);
	}
	
	public void addField(String field){
		ArrayList<Byte> list = new ArrayList<>();
		byte[] temp = field.getBytes();
		
		for(byte b : temp){
			list.add(b);
		}
		
		addField(list);
	}
	
	public ArrayList<Byte> getContent() {
		return content;
	}

	public void setContent(ArrayList<Byte> content) {
		this.content = content;
	}
}
