package dataModels;
import java.io.*;
import peerThreads.*;
import java.net.Socket;
public class Message {
	private byte[] length;
	private byte   type;
	private byte[] payload;
	// 0-choke; 1-unchoke; 2-interested; 3-notinterested; 4-have; 5-bitfield; 6-request; 7-piece
	//for no payload msg
	public Message(byte type, byte[] length){
		this.type=type;
		this.length=length;
		this.payload=new byte[0];
	}
	//for have and request msg
	public Message(byte type, byte[] length, byte[] index){
		this.type=type;
		this.length=length;
		this.payload=index;
	}
	//for bitfield msg
	public Message(byte type, byte[] length, BitField bitfield){
		this.type=type;
		this.length=length;
		this.payload=bitfield.ReturnBitfield();
	}
	//for piece msg
	public Message(byte type, byte[] length, byte[] index, byte[] piecedata){
		this.type=type;
		this.length=length;
		this.payload=Utilities.mergeTwoByteArrays(index, piecedata);
	}
	//
	public static Message readInput(Socket insocket) throws IOException{
		InputStream inmsg = insocket.getInputStream();
        byte[] length = new byte[4];
        byte[] type=new byte[1];
        byte msgtype=8;
        Message return_message = null;
        byte[] msgpayload;
        try{
        int bytesReceived = 0;
        while (bytesReceived < 4) {
        	//System.out.println("Start read length: " + bytesReceived + " " +"bytes available:" + inmsg.available() + " " + n);
        	bytesReceived += inmsg.read(length, bytesReceived, 4 - bytesReceived);
        	//System.out.println("READ bytesReceived: " + bytesReceived + " " + n); ////
        }
        int msglength = Utilities.bytes2Int(length);
        //System.out.println("READ msglength: " + msglength + " " + n); ////
        bytesReceived = 0;
        while (bytesReceived < 1){
            bytesReceived += inmsg.read(type, bytesReceived, 1 - bytesReceived);
        }
        msgtype = type[0];
        //System.out.println("READ TYPE: " + msgtype + " " + n); ////
        msgpayload = new byte[msglength-1];
        if(msglength > 1)
        {
        	bytesReceived = 0;
        	while (bytesReceived < msglength - 1) {
            	bytesReceived += inmsg.read(msgpayload, bytesReceived, msglength - bytesReceived -1);
        	}
        }
        //System.out.println("READ PAYLOAD FINISHED: " + msgtype + " " + n); ////
        int type_int=Utilities.bytes2Int(new byte[] {0,0,0,msgtype});
        if(type_int<4){
        	return_message = new Message(msgtype, Utilities.int2Bytes(1));
        }else if(type_int == 4 || type_int == 5 || type_int == 6){
        	return_message = new Message(msgtype, Utilities.int2Bytes(1 + msgpayload.length), msgpayload);
        }else if(type_int ==7){
        	byte[] index_temp = new byte[4];
        	int i=0;
        	while(i<4){
        		index_temp[i]=msgpayload[i];
        		i++;
        	}
        	int data_length = msglength - 5;
        	//System.out.println("READ PIECE LENGTH: " + msglength + " " + n); ////
        	byte [] piece_temp = new byte[data_length];
        	for(int j=0; j < data_length; j++){
        		piece_temp[j]=msgpayload[i];
        		i++;
        	}
        	return_message = new Message(msgtype, Utilities.int2Bytes(1 + msgpayload.length), index_temp, piece_temp);
        }
        else{
        	throw new IOException("invalid type number when read input stream!");
        }
        }catch (Exception err){
        	//err.printStackTrace();
        }
        return return_message;
	}
	
	public void writeOutput(OutputStream outmsg) throws IOException{
		byte[] out = new byte[Utilities.bytes2Int(this.length)+4];
		byte[] out_temp = new byte[5];
		out_temp = Utilities.mergeTwoByteArrays(this.length, new byte[] {this.type});
		out = Utilities.mergeTwoByteArrays(out_temp, this.payload);
		try{
			outmsg.write(out);
			outmsg.flush();
		}catch (IOException err){
			//err.printStackTrace();
		}finally{
		}
	}
	
	 public byte getType(){
		 return type;
	 }
	
	 public int getIndex(){
		 if (payload.length == 4 && type!=7)
			 return Utilities.bytes2Int(payload);
		 else if (type==7){
			 byte[] index = new byte[4];
			 for(int i=0;i<4;i++){
				 index[i]=payload[i];
			 }
			 return Utilities.bytes2Int(index);
		 }
		 else 
			 return -1;
		}
	 
	 public byte[] getPiece(){
		 byte[] piece = new byte[payload.length-4];
		 for (int i=0; i < payload.length-4; i++){
			 piece[i] = payload[i+4];
		 }
		 return piece;
	 }
	
}
