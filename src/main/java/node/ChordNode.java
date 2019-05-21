package node;

import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;

import java.util.HashMap;

public class ChordNode<V> extends ChordNodeServiceGrpc.ChordNodeServiceImplBase {

    private HashMap<Integer, V> hashMap;
    private int selfId;
    private int[] fingerTable;
    private Identifier successor;
    private Identifier predecessor;

    public ChordNode(int lenFingerTable){
        hashMap = new HashMap<>();
        fingerTable = new int[lenFingerTable];
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
            FindSuccessorResponse findSuccessorResponse = FindSuccessorResponse.newBuilder().setIdentifier(successor).build();
            responseObserver.onNext(findSuccessorResponse);
        }else{
            int searchedId = request.getId();
            ChordNodeClient successorClient = new ChordNodeClient(successor.getIp(), successor.getPort());
            Identifier successorClient.findSuccessor(searchedId);
        }
    }

    public void create(){

    }

    public void join(){

    }

    public void stablize(){}

    public void checkPredecessor(){

    }




}
