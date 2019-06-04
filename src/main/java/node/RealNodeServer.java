package node;

import common.Hasher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RealNodeServer {
    int[] virtualIDs;
    String ip;
    int[] virtualNodePorts;
    ChordNodeServer[] virtualNodeServer;
    private static final Logger logger = Logger.getLogger(ChordNodeServer.class.getName());

    public RealNodeServer(int ringSizeExp, String ip, int port, int nodeNum) {
        this.virtualIDs = new int[nodeNum];
        this.virtualNodePorts = new int[nodeNum];
        this.ip = ip;
        Hasher hasher = new Hasher(1 << ringSizeExp);

        for (int i = 0;i < nodeNum;i++) {
            this.virtualIDs[i] = hasher.hash(ip + (port + i));
            this.virtualNodePorts[i] = port + i;
        }

        this.virtualNodeServer = new ChordNodeServer[nodeNum];

        for (int i = 0;i < nodeNum;i++) {
            this.virtualNodeServer[i] = new ChordNodeServer(virtualIDs[i], ip, virtualNodePorts[i], ringSizeExp);
        }

    }

    public void start(int knownID, String knownIP, int knownPort) throws IOException {
        for (int i = 0;i < virtualNodeServer.length;i++) {
            if (knownID == -1) {
                if (i == 0) {
                    virtualNodeServer[i].start(knownID, knownIP, knownPort);
                } else {
                    virtualNodeServer[i].start(virtualIDs[0], ip, virtualNodePorts[0]);
                }
            } else {
                virtualNodeServer[i].start(knownID, knownIP, knownPort);
            }
            logger.info("dabaole Server started, listening on " + virtualNodePorts[i]);
        }
    }


    public static void main(String[] args) {
        String ip = args[0];
        int port = Integer.valueOf(args[1]);
        int knownID = Integer.valueOf(args[2]);
        String knownIP = null;
        int knownPort = -1;
        if(knownID != -1){
            knownIP = args[3];
            knownPort = Integer.valueOf(args[4]);
        }

        RealNodeServer realNodeServer = new RealNodeServer(13, ip, port, 50);


        try {
            if(knownID == -1) {
                realNodeServer.start(-1, null, -1);
            } else {
                realNodeServer.start(knownID, knownIP, knownPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "start server failed");
        }
    }
}
