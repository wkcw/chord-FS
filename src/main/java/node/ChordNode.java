package node;

import common.JsonUtil;
import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ChordNode extends ChordNodeServiceGrpc.ChordNodeServiceImplBase {

    private static final String TAG = ChordNode.class.getName();

    private HashMap<Integer, String> hashMap;
    private int selfID;
    private static int ringSizeExp = 5;
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
        this.successor = Identifier.newBuilder().build();
    }

    public String get(int key){
        return hashMap.get(key);
    }

    public void put(int key, String value){
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
        successor = Identifier.newBuilder().setID(selfID).setIP(selfIP).setPort(selfPort).build();
    }

    public void join(Identifier knownNodeIdentifier){
        predecessor = null;
        ChordNodeClient knownNodeClient = new ChordNodeClient(knownNodeIdentifier.getIP(), knownNodeIdentifier.getPort());
        successor = knownNodeClient.findSuccessor(selfID);
    }

    public void stablize() {
        if (this.successor != null) {
            ChordNodeClient successorClient = new ChordNodeClient(successor.getIP(), successor.getPort());
            Identifier successorPredecessor = successorClient.tellmePredecessor();

            if (successorPredecessor != null && inRange(successorPredecessor.getID(), selfID, this.successor.getID())) {
                ChordNodeClient successorPredecessorClient = new ChordNodeClient(successorPredecessor.getIP(), successorPredecessor.getPort());
                if (successorPredecessorClient.ping()) {
                    this.successor = predecessor;
                    successorPredecessorClient.notify(this.generateSelfIdentifier());
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
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        PingResponse response = PingResponse.newBuilder().build();
        responseObserver.onNext(response);
    }

    @Override
    public void tellmePredecessor(TellmePredecessorRequest request, StreamObserver<TellmePredecessorResponse> responseObserver) {
        TellmePredecessorResponse response = TellmePredecessorResponse.newBuilder().setIdentifier(predecessor).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void transferData(TransferDataRequest request, StreamObserver<TransferDataResponse> responseObserver) {
        String dataJsonString = request.getDataJson();
        HashMap<Integer, String> dataHashMap = JsonUtil.deserilizable(dataJsonString);
        hashMap.putAll(dataHashMap);
        TransferDataResponse response = TransferDataResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Identifier generateSelfIdentifier(){
        Identifier identifier = Identifier.newBuilder().setID(selfID).setIP(selfIP)
                .setPort(selfPort).setPredecessor(predecessor).setSuccessor(successor).build();
        return identifier;
    }

    public void start(int id, String ip, int port){
        create();
        if (id != -1) {
            Identifier identifier = Identifier.newBuilder().setID(id).setIP(ip).setPort(port).build();
            join(identifier);
        }

        Timer timer = new Timer();

        StabilizeTask stabilizeTask = new StabilizeTask();
        timer.schedule(stabilizeTask, 1000, 1000);

        CheckPredecessorTask checkPredecessorTask = new CheckPredecessorTask();
        timer.schedule(checkPredecessorTask, 1000, 1000);

    }


    class StabilizeTask extends TimerTask {
        public void run() {
            stablize();
        }
    }

    class CheckPredecessorTask extends TimerTask {
        public void run() {
            stablize();
        }
    }

    public static void main(String[] args) {
        ChordNode chordNode = new ChordNode(0, "localhost", 9700, 5);

        chordNode.start(-1, null, -1);
    }


}
