package selectionControl;

import java.util.*;
import dataModels.*;
import peerThreads.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import logger.*;

public class OptNeighborSelection implements Runnable {
	private final int optUnchokeInterval;
    ArrayList<Neighbor> allNeighbors;
    ArrayList<Neighbor> tobeChosenNeighbors;
    Neighbor prevOptNeighbor;
    Neighbor currOptNeighbor;
    Random random;
    Logger logger;

    public OptNeighborSelection() throws FileNotFoundException {
        optUnchokeInterval = PeerProcess.node.getOptimisticUnchokingInterval();
        allNeighbors = PeerProcess.node.getNeighbors();
        tobeChosenNeighbors = new ArrayList<Neighbor>();
        prevOptNeighbor = null;
        currOptNeighbor = null;
        random = new Random(System.currentTimeMillis());
        logger = PeerProcess.logger;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("OptimisticNeighborSelection...");
                choseOpt();
                tobeChosenNeighbors.clear();
                Thread.sleep(optUnchokeInterval * 1000);
                if (PeerProcess.node.getNodeBitfield().Checkfiled()) {
                    boolean flag = false;
                    for (Neighbor peer : allNeighbors) {
                        if (!peer.getBitField().Checkfiled()) {
                            flag = true;
                        }
                    }
                    if (!flag){
                        PeerProcess.node.changeNodeCompleted(true);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            //e.printStackTrace();
            return;
        }
    }

    public synchronized void choseOpt() throws IOException {
        for (Neighbor neighbor : allNeighbors) {
            if (neighbor.getChoke() && neighbor.getInterestedUp()) {
                tobeChosenNeighbors.add(neighbor);
            }
        }
        
        if (tobeChosenNeighbors.size() == 0){
            currOptNeighbor = allNeighbors.get(random.nextInt(allNeighbors.size()));
            int counter = 0;
            while (currOptNeighbor.getFiled() || currOptNeighbor == prevOptNeighbor) {
                currOptNeighbor = allNeighbors.get(random.nextInt(allNeighbors.size()));
                counter++;
                if (counter > allNeighbors.size() * 10) {
                    return;
                }
            }
            //return;
        } else {
            currOptNeighbor = tobeChosenNeighbors.get(random.nextInt(tobeChosenNeighbors.size()));
        }
        
        currOptNeighbor.setChoke(false);
        currOptNeighbor.setOptimistic(true);
        Message unchokeMsg = new Message((byte)1, Utilities.int2Bytes(1));
        unchokeMsg.writeOutput(currOptNeighbor.getSocket().getOutputStream());
        
        if (prevOptNeighbor != null) {
            if (!prevOptNeighbor.getPrefer()) {
                prevOptNeighbor.setChoke(true);
                prevOptNeighbor.setOptimistic(false);
                Message chokeMsg = new Message((byte)0, Utilities.int2Bytes(1));
                chokeMsg.writeOutput(prevOptNeighbor.getSocket().getOutputStream());
            }
            prevOptNeighbor.setTransmissionRate(prevOptNeighbor.getTransmissionNumber() / optUnchokeInterval);
            prevOptNeighbor.setTransmissionNumber(0);
        }

        prevOptNeighbor = currOptNeighbor;
        logger.changeOpt(currOptNeighbor.getNeighborID());
    }
}
