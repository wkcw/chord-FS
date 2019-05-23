package node;

import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;

import java.util.HashMap;

public class ChordNode<V> extends ChordNodeServiceGrpc.ChordNodeServiceImplBase {

    private HashMap<Integer, V> hashMap;
    private int selfID;
    private static int ringSize = 5;
    private String selfIP;
    private int selfPort;
    private int[] fingerTable;
    private Identifier successor;
    private Identifier predecessor;

    public ChordNode(int selfID, String selfIP, int selfPort, int lenFingerTable){
        hashMap = new HashMap<>();
        fingerTable = new int[lenFingerTable];
        this.selfID = selfID;
        this.selfIP = selfIP;
        this.selfPort = selfPort;
    }

    public V get(int key){
        return hashMap.get(key);
    }

    public void put(int key, V value){
        hashMap.put(key, value);
    }

    @Override
    public void notify(NotifyRequest request, StreamObserver<NotifyResponse> responseObserver) {
        int senderID = request.getIdentifier().getID();
        String address = request.getIdentifier().getIP();
        if (predecessor == null || (predecessor.getID() <= senderID && senderID <= selfID)) {
            if (predecessor == null) predecessor = Identifier.newBuilder().build();

            predecessor.toBuilder().setID(senderID).setIP(address).build();
        }
    }

    @Override
    public void findSuccessor(FindSuccessorRequest request, StreamObserver<FindSuccessorResponse> responseObserver) {
        if(this.inRange(request.getID(), selfID, successor.getID())){
            FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(successor).build();
            responseObserver.onNext(response);
        }else{
            int searchedID = request.getID();
            ChordNodeClient successorClient = new ChordNodeClient(successor.getIP(), successor.getPort());
            Identifier searchedIdentifier = successorClient.findSuccessor(searchedID);
            FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(searchedIdentifier).build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    public void create(){
        predecessor = null;
        successor = Identifier.newBuilder().setID(selfId).setIP(selfIP).setPort(selfPort).build();
    }

    public void join(Identifier knownNodeIdentifier){
        predecessor = null;
        ChordNodeClient knownNodeClient = new ChordNodeClient(knownNodeIdentifier.getIP(), knownNodeIdentifier.getPort());
        successor = knownNodeClient.findSuccessor(selfId);
    }

    public void stablize() {
        if (this.successor != null) {
            Identifier predecessor = this.successor.getPredecessor();

            if (predecessor != null && inRange(predecessor.getID(), selfID, this.successor.getID())) {
                ChordNodeClient successorClient = new ChordNodeClient(predecessor.getIP(), predecessor.getPort());
                if (successorClient.ping()) {
                    this.successor = this.predecessor;
                    successorClient.notify(predecessor);
                }

            }
        }
    }

    public void checkPredecessor(){
        if (this.predecessor != null) {
            ChordNodeClient client = new ChordNodeClient(this.predecessor.getIP(), this.predecessor.getPort());
            if (!client.ping()) {
                this.predecessor = null;
            }
        }
    }

    public boolean inRange(int id, int curID, int sucID) {
        if (curID < sucID) {
            if (id > curID && id <= sucID) {
                return true;
            } else {
                return false;
            }
        } else {
            if (id > curID || id <= sucID) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void ping(NullRequest request, StreamObserver<NullResponse> responseObserver) {
        NullResponse response = NullResponse.newBuilder().build();
        responseObserver.onNext(response);
    }

}
