package node;

import common.Hasher;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
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

    public void setNode(Identifier identifier) { this.node = identifier; }

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
            System.out.print("started");
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
            this.manager[id].setNode(Identifier.newBuilder().setIP(ip).setPort(port).build());

            int startPoint = id + 1; // start point for searching the successor
            // if the joining id is the last ID, starting from 0
            if (id == manager.length - 1) {
                startPoint = 0;
            }

            boolean found = false;

            int indexToCheck = id + manager.length;


            for (; startPoint < indexToCheck; startPoint++) {
                int index = startPoint % manager.length;
                if (manager[index].getStatus()) {
                    found = true;
                    break;
                }
            }

            int retID= id;
            String retIP = ip;
            int retPort = port;

            if (found) {
                retID= startPoint;
                retIP = manager[startPoint].getIP();
                retPort = manager[startPoint].getPort();
            }
            System.out.println(retID);
            // To Chuping: is ID sufficient? Do we need addr and port?
            JoinResponse joinResponse = JoinResponse.newBuilder()
                                        .setID(retID).setAddress(retIP).setPort(retPort).build();
            responseObserver.onNext(joinResponse);
            responseObserver.onCompleted();
        }


        @Override
        public void putManager(PutRequest putRequest, StreamObserver<PutResponse> responseObserver) {
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

    public static void main(String[] args) {
        String ip = args[0];
        int port = Integer.valueOf(args[1]);

        ChordManagerServer chordManagerServer = new ChordManagerServer(ip, port);

        try {
            chordManagerServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "start server failed");
        }
    }
}