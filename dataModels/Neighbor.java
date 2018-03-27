package dataModels;

import java.io.*;
import java.net.Socket;

public class Neighbor {
	private Socket socket;
	private int neighborID;
	private String IPAddress;
	private int portNumber;
	private BitField bitfield;
	private Boolean choke;//I choke neighbor
	private Boolean chokeUp;//neighbor chokes me
	private Boolean interested;//I interested in neighbor
	private Boolean interestedUp;//neighbor interested in me
	private Boolean prefer;
	private Boolean optimistic;
	private int transmissionNumber;
	private int transmissionRate;
	private Boolean filed;
	
	Neighbor(String[] NeighborInformation) {

		if (NeighborInformation.length == 4) {
			try {
				neighborID = Integer.parseInt(NeighborInformation[0]);
			} catch(NumberFormatException e) {
			    System.out.print("invalid neighborID." + NeighborInformation[0]);
	            e.printStackTrace();
			}
			IPAddress = NeighborInformation[1];
			try {
	            portNumber = Integer.parseInt(NeighborInformation[2]);
	        } catch (NumberFormatException e) {
	            System.out.print("invalid portNumber" + NeighborInformation[2]);
	            e.printStackTrace();
	        }
			Boolean filed = false;
	        try {
	             filed = Utilities.int2Boolean(Integer.parseInt(NeighborInformation[3]));
	        } catch (NumberFormatException e) {
	             System.out.print("invalid filed: " + NeighborInformation[3]);
	             e.printStackTrace();
	        }
	        this.filed = (filed == true);
	        bitfield = new BitField(this.filed, Node.pieceNumber);
		    choke = true; //
		    chokeUp = true; //
		    interested = false;
		    interestedUp = false;
		    prefer = false;
		    optimistic = false;
		    transmissionNumber = 0;
		    transmissionRate = 0; 
		}
	    else {
	         try {
	             throw new Exception("invalid Neighbor Information");
	         } catch (Exception e) {
	             e.printStackTrace();
	         }
	    }
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public Boolean setSocket() throws IOException {
        socket = new Socket(IPAddress, portNumber);
        return socket.isConnected();
    }
	
	public Boolean setSocket(Socket socket) throws IOException {
       if(socket != null){
    	   this.socket=socket;
    	   return true;
       }else
    	   return false;
    }
	
	public int getNeighborID() {
		return neighborID;
	}
		
	public String getIpAddress() {
		return IPAddress;
	}
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public BitField getBitField() {
		return bitfield;
	}
	
	public void setBitField(BitField bitfield) {
		this.bitfield = bitfield;
	}
		
	public Boolean getChoke() {
		return choke;
	}
	
	public synchronized void setChoke(Boolean choke) {
		this.choke = choke; 
	}
		
	public Boolean getChokeUp() {
		return chokeUp;
	}
	
	public synchronized void setChokeUp(Boolean chokeUp) {
		this.chokeUp = chokeUp;
	}
	
	public Boolean getInterested() {
		return interested;
	}
	
	public synchronized void setInterested(Boolean interested) {
		this.interested = interested; 
	}
		
	public boolean getInterestedUp() {
		return interestedUp;
	}
	
	public synchronized void setInterestedUp(Boolean interestedUp) {
		this.interestedUp = interestedUp; 
	}
	
	public boolean getPrefer() {
		return prefer;
	}
	
	public synchronized void setPrefer(Boolean prefer) {
		this.prefer = prefer; 
	}
	
	public boolean getOptimistic() {
		return optimistic;
	}
	
	public synchronized void setOptimistic(Boolean optimistic) {
		this.optimistic = optimistic; 
	}
	
	public int getTransmissionNumber() {
		return transmissionNumber;
	}
	
	public synchronized void setTransmissionNumber(int transmissionNumber) {
		this.transmissionNumber = transmissionNumber;
	}
	
	public int getTransmissionRate() {
		return transmissionRate;
	}

	public void setTransmissionRate(int transmissionRate) {
		this.transmissionRate = transmissionRate;
	}
	
	public boolean getFiled() {
		return filed;
	}
	
	public synchronized void setFiled(Boolean filed) {
		this.filed = filed; 
	}
}
