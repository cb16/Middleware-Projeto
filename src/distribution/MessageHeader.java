package distribution;

import java.util.ArrayList;

public class MessageHeader {
	private Byte mqttControlPacketType;
	private int remainingLength;
	
	public ArrayList<Byte> toBytes(){
		ArrayList<Byte> encoded = new ArrayList<Byte>();
		
		encoded.add(mqttControlPacketType);
		encoded.addAll(encodeLength());
		
		return encoded;
	}
	
	private ArrayList<Byte> encodeLength(){
		ArrayList<Byte> encoded = new ArrayList<Byte>();
		int length = this.remainingLength;
		Byte encodedByte;
		
		do{
			encodedByte = (byte) (length%128);
			
			length -= length%128;
			length /= 128;
			
			if(length > 0){
				// Set the least significant bit to 1
				// to indicate that the next byte is a length byte
				encodedByte = (byte) (encodedByte + 128); 
			}
			
			encoded.add(encodedByte);
			
		}while(length > 0);
		
		return encoded;
	}
	
	private int decodeLength(ArrayList<Byte> encoded){
		int length = 0;
		int i = 0;
		int power = 1;
		int encodedByte;
		
		do{
			encodedByte = Byte.toUnsignedInt(encoded.get(i));
			// Least significant bit is used to inform if there is another byte of length
			length += power*(encodedByte%128);
			power *= 128;
			i++;
		}while(encodedByte/128 > 0);
		
		return length;
	}
}