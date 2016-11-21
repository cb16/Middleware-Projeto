package distribution;

import java.util.ArrayList;
import distribution.Operation;

public class MessageHeader {
	private Byte mqttControlPacketType;
	private int remainingLength;
	private Operation op;
	
	public MessageHeader(Operation op, int remainingLength){
		this.op = op;
		switch(op){
			case CONNECT:
				// 0001 in the 4 least significant bits
				mqttControlPacketType = (byte) (1 << 4); 
				break;
			case CONNACK:
				// 0001 in the 4 least significant bits
				mqttControlPacketType = (byte) (2 << 4); 
				break;
			case PUBLISH:
				// 0011 in the 4 least significant bits
				mqttControlPacketType = (byte) (3 << 4); 
				break;
			case LIST:
				// 0010 in the 4 least significant bits
				mqttControlPacketType = (byte) (4 << 4); 
				break;
			case SUBSCRIBE:
				// 1000 in the 4 least significant bits
				mqttControlPacketType = (byte) (8 << 4);
				break;
		default:
			break;
		}
		
		this.remainingLength = remainingLength;
	}
	
	public MessageHeader(ArrayList<Byte> content){
		mqttControlPacketType = content.get(0);
		int opId = Byte.toUnsignedInt(mqttControlPacketType) >> 4;
		//System.out.println(opId);
		switch(opId){
			case 1:
				op = Operation.CONNECT;
				break;
			case 3:
				op = Operation.PUBLISH;
				break;
			case 8:
				op = Operation.SUBSCRIBE;
				break;
			default:
				break;
		}
		remainingLength = decodeLength(content);
	}
	
	public Operation getOperation(){
		return op;
	}
	
	public int getRemainingLength() {
		return remainingLength;
	}
	
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
		int i = 1;
		int power = 1;
		int encodedByte;
		
		do{
			encodedByte = Byte.toUnsignedInt(encoded.get(i));
			// Least significant bit is used to inform if there is another byte of length
			length += power*(encodedByte%128);
			power *= 128;
			i++;
		} while (encodedByte / 128 > 0);

		return length;
	}
}