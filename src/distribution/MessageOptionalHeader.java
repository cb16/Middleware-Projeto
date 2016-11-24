package distribution;

import java.util.ArrayList;
import java.util.List;

public class MessageOptionalHeader {
	private ArrayList<Byte> content = new ArrayList<Byte>();
	
	public MessageOptionalHeader(List<Byte> list, Operation op) {
		int numFields = 0;
		
		if(op == Operation.CONNECT || op == Operation.PUBLISH || op == Operation.SUBSCRIBE){
			numFields = 1;
		}
		
		fromArray(list, numFields);
	}
	
	public MessageOptionalHeader() {}

	private void fromArray(List<Byte> content, int numFields){
		//System.out.println(content.size());
		int toIndex = Byte.toUnsignedInt(content.get(1)) + 2;
		//System.out.println(toIndex);
		this.content = new ArrayList<Byte>(content.subList(0, toIndex));
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
