package dataModels;

import java.util.ArrayList;

public class BitField {
	private Boolean[] bitfield;
	private Boolean filed;
	private ArrayList<Integer> transmitting;
	private byte[] byte_bitfield;
	
	BitField( Boolean filed, int pieceNum)
	{
		this.filed=filed;
		bitfield=new Boolean[pieceNum];
		byte_bitfield=new byte[pieceNum];
		transmitting = new ArrayList<Integer>();
		if (filed){
			for (int i=0; i<pieceNum; i++){
				bitfield[i]=true;
				byte_bitfield[i]=1;
			}
		}
		else{
			for (int i=0; i<pieceNum; i++){
				bitfield[i]=false;
				byte_bitfield[i]=0;
			}
		}
	}
	//check if it has the piece with this index
	public Boolean CheckHave(int index){
		return bitfield[index];
	}
	public Boolean CheckTransmitting(int index){
		return transmitting.contains((Integer)index);
	}
	public void setTransimitting(int index){
		transmitting.add((Integer)index);
	}
	public Boolean Checkfiled(){
		return filed;
	}
	
	public byte[] ReturnBitfield(){
		return byte_bitfield;
	}
	
	public synchronized Boolean setbit(int index){
		bitfield[index]=true;
		byte_bitfield[index]=1;
		if(transmitting.contains((Integer)index)){
		transmitting.remove((Integer)index);
		}
		//every time updating the bitfield, whether the whole file is received is checked
		for (Boolean bit : bitfield){
			if (bit != true)
				return false;
		}
		filed=true;
		return true;
	}
	
	public synchronized void resetbit(int index){
		bitfield[index]=false;
		byte_bitfield[index]=0;
		if (filed==true)
			filed=false;
	}
	
	public synchronized int selectpiece(){
		//System.out.println("select piece...");
		for (int i=0;i<bitfield.length;i++){
			if(!bitfield[i] && !transmitting.contains(i)){
				transmitting.add(i);
				//System.out.println("select piece: " + i);
				return i;
			}
		}
		//System.out.println("select piece-default: " + 0);
		return 0;
	}
	public synchronized int selectpiece(Neighbor neighbor){
		//System.out.println(neighbor.getNeighborID() + ", select piece...");
		for (int i=0;i<bitfield.length;i++){
			//System.out.println("bitfield length: " + bitfield.length + ", i: " + i);
			boolean notBitField = !bitfield[i];
			//System.out.print("notBitField: " + notBitField + " ");
			boolean have = neighbor.getBitField().CheckHave(i);
			//System.out.print("have: " + have + " ");
			boolean notContains = !transmitting.contains((Integer)i);
			//System.out.println("notContains: " + notContains);
			if(notBitField && have && notContains) {
				transmitting.add(i);
				//System.out.println(neighbor.getNeighborID() + ", select piece: " + i);
				return i;
			}
		}
		//System.out.println(neighbor.getNeighborID() + "select piece-default: " + 0);
		return 0;
	}
}
