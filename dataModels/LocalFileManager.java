package dataModels;

import java.io.*;
import java.nio.file.Files;

public class LocalFileManager {
	public Node node;
	private RandomAccessFile file;
	
	public LocalFileManager(Node node) throws IOException{
		this.node=node;
		//sub dir for a node
		String directory = "peer_"+node.getNodeID()+"/";
		File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        if (node.getNodeFiled()) {
            File temp1 = new File(node.getFileName());
            //copy file to node's sub dir if it has the file at the beginning
            File temp2 = new File(directory + node.getFileName());
            Files.copy(temp1.toPath(), temp2.toPath());
            file = new RandomAccessFile(temp2, "rw");
        } else {
            file = new RandomAccessFile(directory + node.getFileName(), "rw");
            try {
                file.setLength(node.getFileSize());
            } catch (IOException err) {
                //err.printStackTrace();
            }
        }
	}
	
	public synchronized byte[] ReadPiece (int index) throws IOException{
		int length;
		int i=0;
		if (node.getPieceRemain()!=0 && index==node.getPieceNumber()-1){
			length=node.getPieceRemain();
		}
		else
			length=node.getPieceSize();
		
		byte[] payload = new byte[length];
		int fileoffset = index * node.getPieceSize();
		try {
            file.seek(fileoffset);
            for (i=0; i<length; i++){
    			payload[i]=file.readByte();
    		}
        } catch (IOException err) {
            //err.printStackTrace();
        }finally{
        }
		
		return payload;
	}
	
	public synchronized void SavePiece (Message piece) throws IOException{
		int fileoffset = piece.getIndex() * node.getPieceSize();
		byte[] payload = piece.getPiece();
		try {
            file.seek(fileoffset);
            file.write(payload);
            if ((piece.getIndex() & 15) == 0) {
            	System.out.println("Saved pieces: " + piece.getIndex() + ", piece length: " + payload.length + " bytes.");////
            }
        } catch (IOException err) {
            //err.printStackTrace();
        }
	}
	
	 public void closeLocalFile () throws IOException {
	        file.close();
	    }
}
