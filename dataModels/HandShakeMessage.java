package dataModels;

public class HandShakeMessage {
	private byte[] header;
	private byte[] zerobits;
	private int peerID;
	
	public HandShakeMessage(int peerID) {
		this.header = "P2PFILESHARINGPROJ".getBytes();
		this.zerobits = new byte[10];
		this.peerID = peerID;
	}

	public byte[] getHeader() {
		return header;
	}
	
	public byte[] getZerobits() {
		return zerobits;
	}
	
	public int getPeerID() {
		return peerID;
	}
}
