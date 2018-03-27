package logger;

import java.io.*;
import java.net.*;
import java.util.*;
import peerThreads.*;
import dataModels.*;
import java.time.*;

public class Logger {
	private Node Node;
	private File file;
	private int NodeID;
	private BufferedWriter out;
	private String strNodeID;
	private boolean isSystemOut;
	private FileWriter tmp;
	
	public Logger(Node Node) throws FileNotFoundException {
		this.Node = Node;
		NodeID = Node.getNodeID();
		strNodeID = Node.getNodeID() + "";
		file = new File("log_peer_" + NodeID + ".log");
		//file = new File("peer_" + Node.getNodeID() + "/" + "log_node_" + NodeID + ".log");
		try {
			file.createNewFile();
			tmp = new FileWriter(file);
			out = new BufferedWriter(tmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// public void piecelog(int index, byte[] piece){
	// 	String path = "Node_" + Node.getNodeID() + "/"  + NodeID + "_" + index + ".log";
	// 	File piecefile = new File(path);
	// 	try {  piecefile.createNewFile();
	// 		   FileOutputStream pfos = new FileOutputStream(path);
	// 		   pfos.write(piece);
	// 		   pfos.close();
	// 		}catch (IOException e) {
	// 			e.printStackTrace();
	// 		}
	// }
	public synchronized void println(String log) throws IOException {
		String time = LocalTime.now().toString();
		log += "\n";
		if (isSystemOut)
			System.out.println(log);
		else {
			out.write(log);
			out.newLine();
			out.flush();
		}
	}
	
	public void connection(int connectionNodeID) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " is connected with " + connectionNodeID + ".";
        println(log);
	}

	public void changePrefer(ArrayList<Neighbor> list) throws IOException {
		String str="";
        for (Neighbor i : list) {
            str += ", " + i.getNeighborID();
        }
        if (str.length() != 0) {
        	String time = LocalTime.now().toString();
        	str = str.substring(2, str.length());
        	String log = time + ": Peer " + NodeID + " have preferred Neighbors " + str + ".";
            println(log);
        }
	}
	
	//changeOpt
	public void changeOpt(int NeighborID) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " has a optimistically unchoked Neighbor " + NeighborID + ".";
        println(log);
	}
	
	//unchoking
	public void changeUnchoke(int NeighborID) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " is unchoked by " + NeighborID + ".";
        println(log);
	}
	
	//choking
	public void changeChoke(int NeighborID) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " is choked by " + NeighborID + ".";
        println(log);
	}
	
	//notInterested
	public void receiveUninteresting(int NeighborID) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " receive uninteresting from " + NeighborID + ".";
        println(log);
	}
	
	//interested
	public void receiveInteresting(int NeighborID) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " receive interesting from " + NeighborID + ".";
        println(log);
	}

	public void sendInterestingLog(int NeighborID, boolean done) throws IOException {
		String time = LocalTime.now().toString();
		String str = "%%%%%%";
		if (done) {
			str = " has sent interesting to ";
		} else {
			str = " is sending interesting to ";
		}
		String log = time + ": Peer " + NodeID + str + NeighborID + ".";
        println(log);
	}

	public void sendNotInterestingLog(int NeighborID, boolean done) throws IOException {
		String time = LocalTime.now().toString();
		String str = "%%%%%%";
		if (done) {
			str = " has sent not-interesting to ";
		} else {
			str = " is sending not-interesting to ";
		}
		String log = time + ": Peer " + NodeID + str + NeighborID + ".";
        println(log);
	}

	public void sendRequestLog(int NeighborID, int index, boolean done) throws IOException {
		String time = LocalTime.now().toString();
		String str = "%%%%%%";
		if (done) {
			str = " has requested ";
		} else {
			str = " is requesting ";
		}
		String log = time + ": Peer " + NodeID + str + NeighborID + " for the piece " + index + ".";
        println(log);
	}

	public void receiveRequest(int NeighborID, int index) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " receive request from " + NeighborID + " for the piece " + index + ".";
        println(log);
	}
	
	//have
	public void receiveHave(int NeighborID, int index) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " receive have from " + NeighborID + " for the piece " + index + ".";
        println(log);
	}

	public void sendHave(int NeighborID, int index, boolean done) throws IOException {
		String time = LocalTime.now().toString();
		String str = "%%%%%%%";
		if (done) {
			str = " has sent have to ";
		} else {
			str = " is sending have to ";
		}
		String log = time + ": Peer " + NodeID + str + NeighborID + " for the piece " + index + ".";
        println(log);
	}

	//receive bitfield
	public void receiveBitfield(int NeighborID) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " receive bitfield from " + NeighborID + ".";
        println(log);	
	}
	
	//piece
	public void piece(int NeighborID, int number, int index) throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " has downloaded the piece " + index + " from " + NeighborID + "." + " Now the number of pieces it has is " + number +".";
        println(log);
	}

	public void sendPieceLog(int NeighborID, int index, boolean done) throws IOException {
		String time = LocalTime.now().toString();
		String str = "%%%%%%%";
		if (done) {
			str = " has sent piece ";
		} else {
			str = " is sending piece ";
		}
		String log = time + ": Peer " + NodeID + str + index + " to " + NeighborID + ".";
        println(log);
	}
	
	//completion
	public void downloadOver() throws IOException {
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + " download file is completed.";
        println(log);
	}

	public void loginit(Node node) throws IOException
    {
    	String log = node.getNodeFiled() + " " + node.getPieceNumber() + " " + node.getPieceSize() + " "+ node.getPieceRemain() +"."; 
    	println(log);
    }
	public void threadfinished (int NeighborID) throws IOException{
		String time = LocalTime.now().toString();
		String log = time + ": Peer " + NodeID + ": For thread "+ NodeID + ", " + NeighborID + " has finished";
		println(log);
	}
	public void loggeroff() throws IOException {
		out.flush();
		out.close();
		tmp.close();
	}
}