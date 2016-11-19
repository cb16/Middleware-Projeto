package distribution;

import java.util.ArrayList;

public class MessageOptionalHeader {
	private ArrayList<Byte> content = new ArrayList<Byte>();
	
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
	
	public int length(){
		return content.size();
	}

	public ArrayList<Byte> getContent() {
		return content;
	}

	public void setContent(ArrayList<Byte> content) {
		this.content = content;
	}
}
