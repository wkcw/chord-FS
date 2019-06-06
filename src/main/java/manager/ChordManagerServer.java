package manager;

import common.Hasher;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;
import node.ChordNodeClient;

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
        private static int ringSizeExp = 13;
        private Hasher hasher = new Hasher(1 << ringSizeExp);
        public ChordManagerService() {
            manager = new NodeStatus[1 << ringSizeExp];

            // Set all nodes to offline mode when initializing the manager
            for (int i = 0; i < 1 << ringSizeExp; i++) {
                Identifier node = Identifier.newBuilder().setID(i).build();
                manager[i] = new NodeStatus(node, false);
            }
        }

        public void start() {
            System.out.println("started");
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


            JoinResponse joinResponse;
            int retID = findNextAvailableNode(startPoint, id + manager.length);
            if (retID == -1) {
                joinResponse = JoinResponse.newBuilder().setID(id).setAddress(ip).setPort(port).build();
            }
            else {
                joinResponse = JoinResponse.newBuilder().setID(retID).setAddress(manager[retID].getIP())
                        .setPort(manager[retID].getPort()).build();
            }

            System.out.println("returning ID to calling server " + retID);
            responseObserver.onNext(joinResponse);
            responseObserver.onCompleted();
        }

        public int findNextAvailableNode(int startPoint, int endPoint) {

            boolean found = false;
            for (; startPoint < endPoint; startPoint++) {
                int index = startPoint % this.manager.length;
                if (this.manager[index].getStatus()) {
                    System.out.println("Found available node: " + index);
                    found = true;
                    break;
                }
            }
            if (found) {
                return startPoint % this.manager.length;
            }
            return -1;
        }

        @Override
        public void putManager(PutRequest putRequest, StreamObserver<PutResponse> responseObserver) {
            String key = putRequest.getKey();
            String value = putRequest.getValue();
            PutResponse response;
            int hash = hasher.hash(key);
            int nodeID = hash;
            System.out.println("The hash of the key is: " + nodeID);
            int nextNode = findNextAvailableNode(nodeID, nodeID + manager.length);
            System.out.println("The next available node for put is: " + nextNode);
            PutResponse putResponse;
            if (nextNode == -1) {
                putResponse = PutResponse.newBuilder().setRet(ReturnCode.FAILURE).build();
            }
            else {
                ChordNodeClient nodeClient = new ChordNodeClient(manager[nextNode].getIP(),
                        manager[nextNode].getPort());
                nodeClient.put(key, value);
                nodeClient.close();
                putResponse = PutResponse.newBuilder().setRet(ReturnCode.SUCCESS).build();
            }
            responseObserver.onNext(putResponse);
            responseObserver.onCompleted();
        }

        @Override
        public void findSuccessor(FindRequest findRequest, StreamObserver<FindResponse> responseObserver) {
            int id = findRequest.getID();
            int nextNode = findNextAvailableNode(id, id + manager.length);
            FindResponse findResponse = FindResponse.newBuilder().setID(nextNode).setAddress(manager[nextNode].getIP()).setPort(manager[nextNode].getPort()).build();
            responseObserver.onNext(findResponse);
            responseObserver.onCompleted();
        }
        @Override
        public void getManager(GetRequest getRequest, StreamObserver<GetResponse> responseStreamObserver) {
            String key = getRequest.getKey();
            int hash = hasher.hash(key);
            int nodeID = hash;

            int nextNode = findNextAvailableNode(nodeID, nodeID + manager.length);
            GetResponse getResponse;
            if (nextNode == -1) {
                getResponse = GetResponse.newBuilder().setRet(ReturnCode.FAILURE).build();
            }
            else {
                ChordNodeClient nodeClient = new ChordNodeClient(manager[nextNode].getIP(),
                        manager[nextNode].getPort());
                String value = nodeClient.get(key);
                nodeClient.close();
                getResponse = GetResponse.newBuilder().setValue(value).setRet(ReturnCode.SUCCESS).build();
            }

            responseStreamObserver.onNext(getResponse);
            responseStreamObserver.onCompleted();
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