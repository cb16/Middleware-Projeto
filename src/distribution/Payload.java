package distribution;

import java.util.ArrayList;
import java.util.Arrays;

public class Payload {
	private ArrayList<Byte> content;
	private String listDelimiter = "\r\n";
	
	public ArrayList<String> getList(){
		ArrayList<String> decoded = new ArrayList<String>();
		String content = this.content.toString();
		
		decoded = (ArrayList<String>) Arrays.asList(content.split(listDelimiter));
		
		return decoded;
	}
	
	public ArrayList<Byte> getContent() {
		return content;
	}

	public void setContent(ArrayList<Byte> content) {
		this.content = content;
	}
}
