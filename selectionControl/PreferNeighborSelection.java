package selectionControl;

import java.util.*;
import dataModels.*;
import peerThreads.*;
import java.io.IOException;
import logger.*;

public class PreferNeighborSelection implements Runnable {
	private int interval;
    private ArrayList<Neighbor> preferedNeighbors;
    private int number;
    private Random random = new Random(System.currentTimeMillis());
    private Logger logger;

    public PreferNeighborSelection() throws Exception {
        this.interval = PeerProcess.node.getUnchokingInterval();
        this.number = PeerProcess.node.getPerferNeighborNumber();
        this.preferedNeighbors = new ArrayList<Neighbor>();
        logger = PeerProcess.logger;
    }
    
    @Override
    public void run() {
    	try {
            if(!firstSelect())
                return;
            while (true) {
                logger.changePrefer(preferedNeighbors);
                System.out.println("PreferNeighborSelection...");
                if (PeerProcess.node.getNodeFiled()) {
                    firstSelect();
                } else {
                    select();
                }
                Thread.sleep(interval * 1000);
                if (PeerProcess.node.getNodeBitfield().Checkfiled()) {
                    boolean flag = false;
                    for (Neighbor neighbor : PeerProcess.node.getNeighbors()) {
                        if (!neighbor.getBitField().Checkfiled()) {
                        	flag = true;
                        }
                    }

                    if (!flag) {
                        PeerProcess.node.changeNodeCompleted(true);
                        return;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            return;
        }
    }
    
    public void select() throws IOException {
        for (Neighbor preferedNeighbor : preferedNeighbors) {
        	Neighbor neighbor = PeerProcess.node.getNeighbors().get(PeerProcess.node.getNeighbors().indexOf(preferedNeighbor));
        	neighbor.setTransmissionRate(neighbor.getTransmissionNumber() / interval);
        	neighbor.setTransmissionNumber(0);
            // preferedNeighbor.setTransmissionRate(preferedNeighbor.getTransmissionNumber() / interval);
            // preferedNeighbor.setTransmissionNumber(0);
        }

        ArrayList<Neighbor> tempPreferedNeighbors = sortNeighbors();
        
        for (Neighbor neighbor : tempPreferedNeighbors) {
            if (preferedNeighbors.contains(neighbor)) {
            	// 
            } else if (neighbor.getOptimistic()) {
                PeerProcess.node.getNeighbors().get(PeerProcess.node.getNeighbors().indexOf(neighbor)).setPrefer(true);
            } else {
                PeerProcess.node.getNeighbors().get(PeerProcess.node.getNeighbors().indexOf(neighbor)).setPrefer(true);
                Message unchokeMsg = new Message((byte)1, Utilities.int2Bytes(1));
                PeerProcess.node.getNeighbors().get(PeerProcess.node.getNeighbors().indexOf(neighbor)).setChoke(false);
                unchokeMsg.writeOutput(neighbor.getSocket().getOutputStream());
            }
        }

        for (Neighbor neighbor : preferedNeighbors) {
            if (!tempPreferedNeighbors.contains(neighbor) && !neighbor.getOptimistic()){
                PeerProcess.node.getNeighbors().get(PeerProcess.node.getNeighbors().indexOf(neighbor)).setPrefer(false);
                PeerProcess.node.getNeighbors().get(PeerProcess.node.getNeighbors().indexOf(neighbor)).setChoke(true);
                Message chokeMsg = new Message((byte)0, Utilities.int2Bytes(1));
                chokeMsg.writeOutput(neighbor.getSocket().getOutputStream());
            }
        }

        preferedNeighbors.clear();
        preferedNeighbors = tempPreferedNeighbors;
    }

    public boolean firstSelect() throws IOException {
        if (PeerProcess.node.getNeighbors().size() < number) {
            for (Neighbor neighbor : PeerProcess.node.getNeighbors()) {
            	neighbor.setChoke(false);
            	neighbor.setPrefer(true);
                Message unchokeMsg = new Message((byte)1, Utilities.int2Bytes(1));
                unchokeMsg.writeOutput(neighbor.getSocket().getOutputStream());
                preferedNeighbors.add(neighbor);
                logger.changePrefer(preferedNeighbors);
            }
            return false;
        } else {
            preferedNeighbors.clear();
            for (int i = 0; i < number; i++) {
                int index = random.nextInt(PeerProcess.node.getNeighbors().size());//PeerProcess.node.getNeighbors().indexOf(PeerProcess.node.getNeighbors().get());
                Neighbor neighbor = PeerProcess.node.getNeighbors().get(index);
                while (preferedNeighbors.contains(neighbor)) {
                    index = (index + random.nextInt(number) + 1) % PeerProcess.node.getNeighbors().size();
                    neighbor = PeerProcess.node.getNeighbors().get(index);
                }
                preferedNeighbors.add(neighbor);
                neighbor.setChoke(false);
                neighbor.setPrefer(true);
                Message unchokeMsg = new Message((byte)1, Utilities.int2Bytes(1));
                unchokeMsg.writeOutput(neighbor.getSocket().getOutputStream());
                logger.changePrefer(preferedNeighbors);
            }
            return true;
        }
    }

    public ArrayList<Neighbor> sortNeighbors() {
        PriorityQueue<Neighbor> minHeap = new PriorityQueue<Neighbor>(number + 1, new Comparator<Neighbor>() {
        	@Override
        	public int compare(Neighbor n1, Neighbor n2) {
        		return n1.getTransmissionRate() - n2.getTransmissionRate();
        	}
        });
        for (Neighbor neighbor : PeerProcess.node.getNeighbors()) {
            minHeap.offer(neighbor);
            if (minHeap.size() > number) {
            	minHeap.poll();
            }
        }
        return new ArrayList<Neighbor>(minHeap);
    }
}
