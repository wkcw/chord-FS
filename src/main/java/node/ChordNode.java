package node;

import chord.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;

public class ChordNode<K, V> extends ChordNodeServiceGrpc.ChordNodeServiceImplBase {

    private HashMap<K, V> hashMap;
    private int[] fingerTable;
    private int successor;
    private int predecessor;

    public ChordNode(int lenFingerTable){
        hashMap = new HashMap<>();
        fingerTable = new int[lenFingerTable];
    }

    public V get(K key){
        return hashMap.get(key);
    }

    public void put(K key, V value){
        hashMap.put(key, value);
    }

    @Override
    public void notify(Identifier request, StreamObserver<NotifyResponse> responseObserver) {

    }

    @Override
    public void findSuccessor(FindSuccessorRequest request, StreamObserver<FindSuccessorResponse> responseObserver) {
    }

    public void create(){

    }

    public void join(){

    }

    public void stablize(){}

    public void checkPredecessor(){

    }




}
