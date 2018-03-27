package dataModels;

import java.io.*;
import java.util.ArrayList;

public class Node {
	private int NodeID;
	private String NodeIPaddress;
	private int NodePort;
	private Boolean NodeFiled;
	private BitField NodeBitfield;
	private int NodeNumber;
	private int perferNeighborNumber;
	private int unchokingInterval;
	private int optimisticUnchokingInterval;
	private String fileName;
	private int fileSize;
	private int pieceSize;
	private ArrayList<Neighbor> Neighbors;
	static int pieceNumber=0;
	private int NeighborDownload;
	private Boolean NodeCompleted;
	private int pieceRemain;
	
	public Node(int NodeID) throws IOException {
		this.NodeID = NodeID;
		Neighbors = new ArrayList<Neighbor>();
		intializeNode();
		intializeNeighbor();
		NeighborDownload = 0;
		NodeCompleted = false;

	}
	void intializeNode() throws IOException{
		File input = new File("Common.cfg");
		InputStreamReader readin = new InputStreamReader(new FileInputStream(input));;
		BufferedReader buffer = new BufferedReader(readin);
		String tmp = buffer.readLine();
		String[] str = tmp.split(" ");
		perferNeighborNumber = Integer.parseInt(str[1]);
		
		tmp = buffer.readLine();
		str = tmp.split(" ");
		unchokingInterval = Integer.parseInt(str[1]);
		
		tmp = buffer.readLine();
		str = tmp.split(" ");
		optimisticUnchokingInterval = Integer.parseInt(str[1]);
		
		tmp = buffer.readLine();
		str = tmp.split(" ");
		fileName = str[1];
		
		tmp = buffer.readLine();
		str = tmp.split(" ");
		fileSize = Integer.parseInt(str[1]);
		
		tmp = buffer.readLine();
		str = tmp.split(" ");
		pieceSize = Integer.parseInt(str[1]);
		pieceNumber = (int)Math.ceil(fileSize * 1.0 / pieceSize);
		pieceRemain = fileSize % pieceSize; //== 0 ? pieceSize : fileSize % pieceSize;
		if(pieceRemain == 0){
			pieceRemain=pieceSize;
		}
		buffer.close();
	}
	
	void intializeNeighbor() throws IOException {
		File input = new File("PeerInfo.cfg");
		InputStreamReader readin = new InputStreamReader(new FileInputStream(input));
		BufferedReader buffer = new BufferedReader(readin);
		int peerID;
		String tmp = buffer.readLine();
		for (int i = 0; tmp != null; i++) {
			String[] str = tmp.split(" ");
			peerID = Integer.parseInt(str[0]);
			if (NodeID == peerID) {
				NodeIPaddress = str[1];
				NodePort = Integer.parseInt(str[2]);
				if (Integer.parseInt(str[3]) == 0) {
					NodeFiled = false;
				} else {
					NodeFiled = true;
				}
				NodeBitfield = new BitField(NodeFiled, pieceNumber);
				NodeNumber = i;
			} else {
				Neighbor neighbor = new Neighbor(str);
				Neighbors.add(neighbor);
			}
			tmp = buffer.readLine();
		}
		buffer.close();
	}
	
	public int getNodeID() {
		return NodeID;
	}
	
	public String getNodeIPaddress() {
		return NodeIPaddress;
	}
	
	public int getNodePort() {
		return NodePort;
	}
	
	public Boolean getNodeFiled() {
		return NodeFiled;
	}
	
	public BitField getNodeBitfield() {
		return NodeBitfield;
	}
	
	public int getNodeNumber() {
		return NodeNumber;
	}
	
	public int getPerferNeighborNumber() {
		return perferNeighborNumber;
	}
	
	public int getUnchokingInterval() {
		return unchokingInterval;
	}
	
	public int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	public int getPieceSize() {
		return pieceSize;
	}
	
	public ArrayList<Neighbor> getNeighbors() {
		return Neighbors;
	}
	
	public int getPieceNumber() {
		return pieceNumber;
	}
	
	public void changeNodeCompleted(Boolean Completed) {
		NodeCompleted = Completed;
	}
	
	public boolean getNodeCompleted() {
		return NodeCompleted;
	}
	
	public int getPieceRemain() {
		return pieceRemain;
	}
	
	public void increaseNeighborDownload() {
		NeighborDownload++;
	}
	
	public int getNeighborDownload() {
		return NeighborDownload;
	}
	public void setNodeFiled(){
		NodeFiled = true;
	}
}
