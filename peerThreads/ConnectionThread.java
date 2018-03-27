package peerThreads;

import dataModels.*;
import logger.*;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.net.Socket;

public class ConnectionThread implements Runnable {
	private Neighbor connectedneighbor;
	private Node me;
	public Logger logger;
	// private ArrayList<Neighbor> neighbors;
	//private Log log;
	public ConnectionThread(Neighbor neighbor){
		connectedneighbor=neighbor;
		me=PeerProcess.node;
		logger = PeerProcess.logger;
	}
	
	public void run(){
		try{
			while(true){
				if(connectedneighbor.getSocket().isClosed())
					break;
				Message msgin = Message.readInput(connectedneighbor.getSocket());
				switch (msgin.getType()){
				case 0:
					//Been choked by neighbor
					connectedneighbor.setChokeUp(true);
					logger.changeChoke(connectedneighbor.getNeighborID());
					break;
				case 1:
					//Been unchoked by neighbor, set a request message
					connectedneighbor.setChokeUp(false);
					logger.changeUnchoke(connectedneighbor.getNeighborID());
					if(connectedneighbor.getInterested()){
						int indexCase1 = me.getNodeBitfield().selectpiece(connectedneighbor);
						Message request_1 = new Message ((byte)6,new byte[]{0,0,0,5}, Utilities.int2Bytes(indexCase1));
						logger.sendRequestLog(connectedneighbor.getNeighborID(), indexCase1, false);
						request_1.writeOutput(connectedneighbor.getSocket().getOutputStream());
						logger.sendRequestLog(connectedneighbor.getNeighborID(), indexCase1, true);
					}
					break;
				case 2:
					//Been interested by neighbor
					connectedneighbor.setInterestedUp(true);
					logger.receiveInteresting(connectedneighbor.getNeighborID());
                    break;
				case 3:
					//Been not interested by neighbor
					connectedneighbor.setInterestedUp(false);
					logger.receiveUninteresting(connectedneighbor.getNeighborID());
                    break;
				case 4:
					int index1 = msgin.getIndex();
					//first of all, record that this neighbor has this piece.
					connectedneighbor.getBitField().setbit(index1);
					//If I don't have this piece, set interested locally and send a interest msg
					logger.receiveHave(connectedneighbor.getNeighborID(), index1);
					if(!me.getNodeBitfield().CheckHave(index1) && !me.getNodeBitfield().CheckTransmitting(index1)){
						connectedneighbor.setInterested(true);
						//String havelog = "I don't have " + connectedneighbor.getNeighborID() + "'s piece " + index1 + ".";
						//logger.println(havelog);
						Message interested_4 = new Message((byte)2, new byte[]{0, 0, 0, 1});
						logger.sendInterestingLog(connectedneighbor.getNeighborID(), false);
						interested_4.writeOutput(connectedneighbor.getSocket().getOutputStream());
						logger.sendInterestingLog(connectedneighbor.getNeighborID(), true);
						//if I'am not choked by this neighbor, ask for this piece
						if(!connectedneighbor.getChokeUp()){
							me.getNodeBitfield().setTransimitting(index1);
							Message request_4 = new Message ((byte)6, new byte[]{0, 0, 0, 5}, Utilities.int2Bytes(index1));
							logger.sendRequestLog(connectedneighbor.getNeighborID(), index1, false);
							request_4.writeOutput(connectedneighbor.getSocket().getOutputStream());
							logger.sendRequestLog(connectedneighbor.getNeighborID(), index1, true);
						}
					}
					else{
						//if I have that piece, tell neighbor I am not interested in that
						connectedneighbor.setInterested(false);
						if(!connectedneighbor.getSocket().isClosed()){
							Message notinterested = new Message((byte)3, new byte[]{0,0,0,1});
							logger.sendNotInterestingLog(connectedneighbor.getNeighborID(), false);
							notinterested.writeOutput(connectedneighbor.getSocket().getOutputStream());
							logger.sendNotInterestingLog(connectedneighbor.getNeighborID(), true);
						}
					}
					break;
				case 5:
					//Bitfield message is only sent when the node has full file.
					logger.receiveBitfield(connectedneighbor.getNeighborID());
					if(!me.getNodeBitfield().Checkfiled()){
						//show i'am interested in this neighbor
						connectedneighbor.setInterested(true);
						Message interested_5 = new Message((byte)2, new byte[]{0, 0, 0, 1});
						logger.sendInterestingLog(connectedneighbor.getNeighborID(), false);
						interested_5.writeOutput(connectedneighbor.getSocket().getOutputStream());
						logger.sendInterestingLog(connectedneighbor.getNeighborID(), true);
						//if I'am not choked by this neighbor, ask for a piece
						if(!connectedneighbor.getChokeUp()){
							int index2 = me.getNodeBitfield().selectpiece(connectedneighbor);
							Message request_5 = new Message((byte)6, new byte[]{0, 0, 0, 5}, Utilities.int2Bytes(index2));
							//System.out.println("request sending..." + index2);
							logger.sendRequestLog(connectedneighbor.getNeighborID(), index2, false);
							request_5.writeOutput(connectedneighbor.getSocket().getOutputStream());
							logger.sendRequestLog(connectedneighbor.getNeighborID(), index2, true);
						}
					}
					break;
				case 6:
					//receiving REQUEST, first check if it is choked
					if(connectedneighbor.getChoke())
					{
						//System.out.println("No Piece for choked neighbor...");
						break;
					}
					else{
						//if not, sends the requested piece if I have it
						//TODO--read data and send
						int index3 = msgin.getIndex();
						logger.receiveRequest(connectedneighbor.getNeighborID(), index3);
						if(me.getNodeBitfield().CheckHave(index3)){
						byte[] piece = PeerProcess.localFile.ReadPiece(index3);
						Message piecemsg = new Message((byte)7, Utilities.int2Bytes(piece.length + 5), Utilities.int2Bytes(index3), piece);
						logger.sendPieceLog(connectedneighbor.getNeighborID(), index3, false);
						piecemsg.writeOutput(connectedneighbor.getSocket().getOutputStream());
						logger.sendPieceLog(connectedneighbor.getNeighborID(), index3, true);
						}
					}
					break;
				case 7:
					//receiving a piece
					int index4 = msgin.getIndex();
					if (me.getNodeBitfield().CheckHave(index4)) {
						//logger.println("RECEIVE PIECE " + index4 + ", I HAVE IT, SO BREAK");
						break;
					}
					PeerProcess.localFile.SavePiece(msgin);
					//TODO -- save data to file
					//setbit of bitfield
					me.getNodeBitfield().setbit(index4);
					logger.println("File has been transmitted completely: " + me.getNodeBitfield().Checkfiled());
					PeerProcess.node.increaseNeighborDownload();
					logger.piece(connectedneighbor.getNeighborID(), PeerProcess.node.getNeighborDownload(), msgin.getIndex());
                    if (PeerProcess.node.getNeighborDownload() == PeerProcess.node.getPieceNumber()){
                        logger.downloadOver();
                    }
					//send have messages to all neighbors
					Message have = new Message((byte)4, new byte[]{0,0,0,5}, Utilities.int2Bytes(index4));
					for(Neighbor neighbor : me.getNeighbors())
					{
						if(!neighbor.getSocket().isClosed())
						{
							logger.sendHave(neighbor.getNeighborID(), index4, false);
							have.writeOutput(neighbor.getSocket().getOutputStream());
							logger.sendHave(neighbor.getNeighborID(), index4, true);
						}
					}
					//if I am still interested in this neighbor and not choked by it
					//ask for a new piece
					if( !connectedneighbor.getChokeUp() && connectedneighbor.getInterested() && !me.getNodeBitfield().Checkfiled()){
						int index5 = me.getNodeBitfield().selectpiece(connectedneighbor);
						Message request_7 = new Message((byte) 6, new byte[]{0, 0, 0, 5}, Utilities.int2Bytes(index5));
						logger.sendRequestLog(connectedneighbor.getNeighborID(), index5, false);
						request_7.writeOutput(connectedneighbor.getSocket().getOutputStream());
						logger.sendRequestLog(connectedneighbor.getNeighborID(), index5, true);
					}
					break;
				default:
					break;
				}
				if (me.getNodeBitfield().Checkfiled() && connectedneighbor.getBitField().Checkfiled()){
					logger.threadfinished(connectedneighbor.getNeighborID());
					connectedneighbor.getSocket().close();
					break;
				} 
				if (connectedneighbor.getSocket().isClosed())
					break;
			}
			//logger.loginit(me);
			return;
		}catch (IOException err1) {
            	//err1.printStackTrace();
            try {
            	connectedneighbor.getSocket().close();
            } catch (IOException err2) {
                //err2.printStackTrace();
                return;
            }
            return;
        }
	}
}
