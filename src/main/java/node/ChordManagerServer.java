package node;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;
import common.Hasher;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

class NodeStatus {
    private Identifier node; // Chord node
    private boolean status; // true -> online, false -> offline

    public NodeStatus(Identifier node, boolean status) {
        this.node = node;
        this.status = status;
    }

    public String getIP() { return node == null? null : node.getIP(); }

    public int getPort() { return node == null? null : node.getPort(); }

    public Identifier getNode() {
        return this.node;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}


public class ChordManagerServer {

    private static final Logger logger = Logger.getLogger(ChordManagerServer.class.getName());

    private Server server;

    private ChordManagerService chordManagerService;

    private ChordManagerServer(String selfIP, int selfPort) {
        chordManagerService = new ChordManagerService();
        server = ServerBuilder.forPort(selfPort).addService(chordManagerService)
                .build();
    }

    private void start() throws IOException {
        server.start();
        chordManagerService.start();
    }

    private static class ChordManagerService extends ChordManagerServiceGrpc.ChordManagerServiceImplBase {

        NodeStatus[] manager;
        private static int ringSizeExp = 5;
        private Hasher hasher = new Hasher(1 << ringSizeExp);
        public ChordManagerService() {
            manager = new NodeStatus[(int) Math.pow(2, ringSizeExp)];

            // Set all nodes to offline mode when initializing the manager
            for (int i = 0; i < Math.pow(2, ringSizeExp); i++) {
                Identifier node = Identifier.newBuilder().setID(i).build();
                manager[i] = new NodeStatus(node, false);
            }
        }

        public void start() {
            Timer timer = new Timer();
            PingTask pingTask = new PingTask();
            timer.schedule(pingTask, 1000, 1000);
        }

        public void pingNodes() {
            for (NodeStatus node : manager) {
                if (node.getStatus() == true) {
                    ChordNodeClient client = new ChordNodeClient(node.getIP(), node.getPort());

                    if (!client.ping()) {
                        node.setStatus((false));
                    }

                    client.close();
                }
            }
        }

        class PingTask extends TimerTask {
            @Override
            public void run() {
                pingNodes();
            }
        }

        @Override
        public void join(JoinRequest joinRequest, StreamObserver<JoinResponse> responseObserver) {
            int id = joinRequest.getID();
            String ip = joinRequest.getAddress();
            int port = joinRequest.getPort();

            manager[id].setStatus(true); // mark the joining node's ID as a online node
            manager[id].getNode().toBuilder().setIP(ip).setPort(port).build(); // this is necessary

            int startPoint = id + 1; // start point for searching the successor
            // if the joining id is the last ID, starting from 0
            if (id == manager.length - 1) {
                startPoint = 0;
            }

            for (; startPoint < manager.length - 1; startPoint++) {
                if (manager[startPoint].getStatus()) {
                    break;
                }
            }

            int retID= startPoint;
            String retIP = manager[startPoint].getIP();
            int retPort = manager[startPoint].getPort();

            // To Chuping: is ID sufficient? Do we need addr and port?
            JoinResponse joinResponse = JoinResponse.newBuilder()
                                        .setID(retID).setAddress(retIP).setPort(retPort).build();
            responseObserver.onNext(joinResponse);
            responseObserver.onCompleted();
        }


        @Override
        public void put(PutRequest putRequest, StreamObserver<PutResponse> responseObserver) {
            String key = putRequest.getKey();
            String value = putRequest.getValue();
            PutResponse response;
            int hash = hasher.hash(key);
            int nodeID = hash;
            for (; nodeID < manager.length; nodeID++) {
                if (manager[nodeID].getStatus()) {
                    ChordNodeClient nodeClient = new ChordNodeClient(manager[nodeID].getIP(), manager[nodeID].getPort());

                }
            }
        }
    }
}

//    NodeStatus[] manager;
//    private static int ringSizeExp = 5;
//
//    public ChordManagerServer() {
//
//        manager = new NodeStatus[(int) Math.pow(2, ringSizeExp)];
//
//        // Set all nodes to offline mode when initializing the manager
//        for (int i = 0; i < Math.pow(2, ringSizeExp); i++) {
//            Identifier node = Identifier.newBuilder().setID(i).build();
//            manager[i] = new NodeStatus(node, false);
//        }
//    }
//
//    // when joining the network, Chord node should call ChordManager.join(id, ip, port) to notify its existence
//    // And manager would return the first available successor to the joining node
//    public int join(int id, String ip, int port) {
//        manager[id].setStatus(true); // mark the joining node's ID as a online node
//        manager[id].getNode().toBuilder().setIP(ip).setPort(port).build(); // this is necessary
//
//        int startPoint = id + 1; // start point for searching the successor
//        // if the joining id is the last ID, starting from 0
//        if (id == manager.length - 1) {
//            startPoint = 0;
//        }
//
//        for (; startPoint < manager.length - 1; startPoint++) {
//            if (manager[startPoint].getStatus()) {
//                break;
//            }
//        }
//        return startPoint; // return the successor ID to the joining node
//    }

