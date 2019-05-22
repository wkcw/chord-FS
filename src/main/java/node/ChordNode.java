package node;

import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;

import java.util.HashMap;

public class ChordNode<V> extends ChordNodeServiceGrpc.ChordNodeServiceImplBase {

    private HashMap<Integer, V> hashMap;
    private int selfId;
    private String selfIp;
    private int selfPort;
    private int[] fingerTable;
    private Identifier successor;
    private Identifier predecessor;

    public ChordNode(int selfId, String selfIp, int selfPort, int lenFingerTable){
        hashMap = new HashMap<>();
        fingerTable = new int[lenFingerTable];
        this.selfId = selfId;
        this.selfIp = selfIp;
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
        int senderID = request.getIdentifier().getId();
        String address = request.getIdentifier().getAddress();
        if (predecessor == null || (predecessor.getId() <= senderID && senderID <= selfId)) {
            predecessor.toBuilder().setId(senderID).setAddress(address).build();
        }
    }

    @Override
    public void findSuccessor(FindSuccessorRequest request, StreamObserver<FindSuccessorResponse> responseObserver) {
        if(request.getId() > selfId && request.getId() <= successor.getId()){
            FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(successor).build();
            responseObserver.onNext(response);
        }else{
            int searchedId = request.getId();
            ChordNodeClient successorClient = new ChordNodeClient(successor.getIp(), successor.getPort());
            Identifier searchedIdentifier = successorClient.findSuccessor(searchedId);
            FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(searchedIdentifier).build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    public void create(){
        predecessor = null;
        successor = Identifier.newBuilder().setId(selfId).setIp(selfIp).setPort(selfPort).build();
    }

    public void join(Identifier knownNodeIdentifier){
        predecessor = null;
        ChordNodeClient knownNodeClient = new ChordNodeClient(knownNodeIdentifier.getIp(), knownNodeIdentifier.getPort());
        successor = knownNodeClient.findSuccessor(selfId);
    }

    public void stablize(){}

    public void checkPredecessor(){

    }




}
