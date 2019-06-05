package node;

import common.ConfigGenerator;
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

        StringBuilder sb = new StringBuilder();
        sb.append("nodeNum="+nodeNum+"\n\n");
        for (int i = 0;i < nodeNum;i++) {
            sb.append("ip"+i+"="+"localhost\n");
            this.virtualNodePorts[i] = port + i;
            sb.append("port"+i+"="+this.virtualNodePorts[i]+"\n");
        }
        ConfigGenerator.generateProperties("start"+port+"-"+nodeNum, sb.toString());

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
        int nodeNum;
        if(knownID != -1){
            knownIP = args[3];
            knownPort = Integer.valueOf(args[4]);
            nodeNum = Integer.valueOf(args[5]);
        }else{
            nodeNum = Integer.valueOf(args[3]);
        }

        RealNodeServer realNodeServer = new RealNodeServer(13, ip, port, nodeNum);


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