package peerThreads;

import dataModels.*;
import selectionControl.*;
import logger.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class PeerProcess {
	public static Node node;
	public static LocalFileManager localFile;
	public static Logger logger;

	public PeerProcess(int ID){
		try{
			node = new Node(ID);
			localFile = new LocalFileManager(node);
			logger = new Logger(node);
		}catch (IOException err)
		{
			err.printStackTrace();
		}
	}
	
	void run() throws Exception
	{
	    HandShake(node.getNodeID());
		//check If I have full file. If have, send bitfield message to everyone;
		if(node.getNodeFiled()){
			Message bitfield = new Message((byte)5, Utilities.int2Bytes(node.getNodeBitfield().ReturnBitfield().length + 1), node.getNodeBitfield());
			for(Neighbor neighbor : node.getNeighbors()){
				try{
					bitfield.writeOutput(neighbor.getSocket().getOutputStream());
				}catch(IOException err){
					err.printStackTrace();
				}
				neighbor.setInterested(false);
				neighbor.setInterestedUp(true);
			}
		}
		//logger.loginit(node);
		//starts threads for data transmission between me and neighbors
		//each thread is responsible for send/receive msgs to/from one neighbor
		ExecutorService ThreadPool = Executors.newFixedThreadPool(node.getNeighbors().size());
		for (Neighbor neighbor : node.getNeighbors())
		{
			ThreadPool.submit(new ConnectionThread(neighbor));
		}
		
		ExecutorService NeighbourSelector = Executors.newFixedThreadPool(2);
		NeighbourSelector.submit(new PreferNeighborSelection());
		NeighbourSelector.submit(new OptNeighborSelection());
		
		ThreadPool.shutdown();
		NeighbourSelector.shutdown();
		
		while (!ThreadPool.isTerminated() && !NeighbourSelector.isTerminated()) {}
		logger.println("Threads terminated.");
        
        ThreadPool.shutdownNow();
		NeighbourSelector.shutdownNow();
		
		try{
			localFile.closeLocalFile();
			for (Neighbor neighbor : node.getNeighbors()){
				neighbor.getSocket().close();
			}
			logger.loggeroff();
		}catch(IOException err){
			//err.printStackTrace();
		}
	}
	
	private void HandShake(int ID)
	{
		//first connecting to neighbors with smaller ID and send handshakes
		HandShakeMessage handshakemsg = new HandShakeMessage (ID);
		try{
			for(int i=0;i<node.getNodeNumber();i++)
			{
				Neighbor nei = node.getNeighbors().get(i);
				nei.setSocket();
				if (!nei.getSocket().isClosed()) {
            		logger.connection(nei.getNeighborID());
        		}
				nei.getSocket().setKeepAlive(true);
				sendMsg(nei.getSocket().getOutputStream(), handshakemsg);
				if(!readMsg(nei.getSocket().getInputStream(), nei.getNeighborID())){
					throw new Exception("wrong handshake msg!");
				}
			}
			//Then wait for connecting for neighbors with larger ID
			ServerSocket Listener = new ServerSocket(node.getNodePort());
                for(int i=node.getNodeNumber(); i < node.getNeighbors().size(); i++) {
                	Neighbor nei = node.getNeighbors().get(i);
                	while(!nei.setSocket(Listener.accept())){;}
                	if (!nei.getSocket().isClosed()) {
            			logger.connection(nei.getNeighborID());
        			}
                	nei.getSocket().setKeepAlive(true);
                	if(!readMsg(nei.getSocket().getInputStream(), nei.getNeighborID())){
                		Listener.close(); 
    					throw new Exception("wrong handshake msg!");
    				}
                	sendMsg(nei.getSocket().getOutputStream(),handshakemsg);
                }
                Listener.close();
		}catch(Exception err){
			//err.printStackTrace();
		}
	}
	
	private void sendMsg(OutputStream msgout, HandShakeMessage message) throws IOException{
		byte[] out = new byte[32];
		byte[] ID = Utilities.int2Bytes(message.getPeerID());
		System.arraycopy(message.getHeader() ,0 ,out ,0 , 18);
		System.arraycopy(message.getZerobits() ,0 ,out ,18 , 10);
		System.arraycopy(ID ,0 ,out ,28 , 4);
		msgout.write(out);
	}
	
	private Boolean readMsg(InputStream msgin, int peerID) throws IOException{
		byte[] header = new byte[18];
	    byte[] zeros=new byte[10];
	    byte[] ID = new byte[4];
	    HandShakeMessage message = new HandShakeMessage(peerID);
	    msgin.read(header, 0, 18);
	    if(Utilities.CompareByteArrary(header, message.getHeader())){
	    	msgin.read(zeros, 0, 10);
	    	if(Utilities.CompareByteArrary(zeros, message.getZerobits())){
	    		msgin.read(ID, 0, 4);
	    		if(peerID==Utilities.bytes2Int(ID)){
	    			return true;
	    		}else
	    			return false;
	    	}else
	    		return false;
	    }
	    else
	    	return false;
	}
	
	public static void main(String[] args) throws Exception {
		PeerProcess process = new PeerProcess(Integer.parseInt(args[0]));
		process.run();
	}

}
