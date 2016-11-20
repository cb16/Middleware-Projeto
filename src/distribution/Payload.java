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
	
	public String toString(){
		String r = new String();
		
		for(byte b:content){
			r += (char) b;
		}
		
		return r;
	}
	
	public ArrayList<String> getFields(){
		ArrayList<String> fields = new ArrayList<>();
		String field, temp;
		int index = 0, j, k;
		
		temp = this.toString();
		
		while(index < content.size()){
			j = content.get(index);
			k = content.get(index + 1);
			index += 2;
					
			field = temp.substring(j+index, k+index);

			fields.add(field);
			
			index += k;
		}
		
		return fields;
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
