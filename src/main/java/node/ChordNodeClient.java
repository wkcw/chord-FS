package node;

import common.IdentifierWithHop;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import net.grpc.chord.*;

import java.net.ConnectException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChordNodeClient {

    private static final Logger logger = Logger.getLogger(ChordNodeClient.class.getName());

    private final ManagedChannel channel;
    private final ChordNodeServiceGrpc.ChordNodeServiceBlockingStub blockingStub;
    private final ChordNodeServiceGrpc.ChordNodeServiceStub asyncStub;

    private Random random = new Random();
    public ChordNodeClient(String host, int port){
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public ChordNodeClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = ChordNodeServiceGrpc.newBlockingStub(channel);
        asyncStub = ChordNodeServiceGrpc.newStub(channel);
    }

    public Identifier findSuccessor(int id){
        FindSuccessorRequest request = FindSuccessorRequest.newBuilder().setID(id).build();
        FindSuccessorResponse findSuccessorResponse;
        try {
            findSuccessorResponse = blockingStub.findSuccessor(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.INFO, "RPC failed: {0}", e.getStatus());
            System.out.println("Got here");
            return Identifier.newBuilder().setID(-1).build();
        }
        return findSuccessorResponse.getIdentifier();
    }

    public IdentifierWithHop findSuccessorWithHop(int id, int hop){
        FindSuccessorRequest request = FindSuccessorRequest.newBuilder().setID(id).setHop(hop).build();
        FindSuccessorResponse findSuccessorResponse;
        try {
            findSuccessorResponse = blockingStub.findSuccessor(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            Identifier identifier = Identifier.newBuilder().setID(-1).build();
            return new IdentifierWithHop(identifier, -1);
        }

        return new IdentifierWithHop(findSuccessorResponse.getIdentifier(), findSuccessorResponse.getHop());
    }

    public boolean ping(){
        PingRequest request = PingRequest.newBuilder().build();
        PingResponse pingResponse;
        try {
            pingResponse = blockingStub.ping(request);
        } catch (StatusRuntimeException e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }

    public boolean notify(Identifier identifier){
        NotifyRequest request = NotifyRequest.newBuilder().setIdentifier(identifier).build();
        NotifyResponse notifyResponse;
        try {
            notifyResponse = blockingStub.notify(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }

    public Identifier inquirePredecessor(){
        InquirePredecessorRequest request = InquirePredecessorRequest.newBuilder().build();
        InquirePredecessorResponse response;
        try {
            response = blockingStub.inquirePredecessor(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }
        return response.getIdentifier();
    }

    public List<Identifier> inquireSuccessorsList() {
        InquireSuccessorsListRequest request = InquireSuccessorsListRequest.newBuilder().build();
        InquireSuccessorsListResponse response;

        try {
            response = blockingStub.inquireSuccessorsList(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());

            return null;
        }

        return response.getSuccessorsListList();
    }

    public String transferData(int ID){
        TransferDataRequest request = TransferDataRequest.newBuilder().setID(ID).build();
        TransferDataResponse response;
        try{
            response = blockingStub.transferData(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }
        return response.getDataJson();
    }

    public boolean acceptMyData(String dataJson){
        AcceptMyDataRequest request = AcceptMyDataRequest.newBuilder().setDataJson(dataJson).build();
        AcceptMyDataResponse response;
        try{
            response = blockingStub.acceptMyData(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }

    public boolean addReplica(Identifier identifier, String dataJson){
        AddReplicaRequest request = AddReplicaRequest.newBuilder().setIdentifier(identifier).setJsonData(dataJson).build();
        AddReplicaResponse response;
        try {
            response = blockingStub.addReplica(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }

    public boolean addScatteredReplica(Identifier identifier, String key, String value){
        AddScatteredReplicaRequest request = AddScatteredReplicaRequest.newBuilder().setIdentifier(identifier).setKey(key).setValue(value).build();
        AddScatteredReplicaResponse response;
        try {
            response = blockingStub.addScatteredReplica(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }

    public boolean addMultipleScatteredReplica(Identifier identifier, String dataJson){
        AddMultipleScatteredReplicaRequest request = AddMultipleScatteredReplicaRequest.newBuilder().setIdentifier(identifier).setJsonData(dataJson).build();
        AddMultipleScatteredReplicaResponse response;
        try {
            response = blockingStub.addMultipleScatteredReplica(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }

    public boolean removeReplica(Identifier identifier){
        RemoveReplicaRequest request = RemoveReplicaRequest.newBuilder().setIdentifier(identifier).build();
        RemoveReplicaResponse response;
        try {
            response = blockingStub.removeReplica(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }

    public boolean removeMultipleScatteredReplica(Identifier identifier, List<String> keyList){
        RemoveMultipleScatteredReplicaRequest request = RemoveMultipleScatteredReplicaRequest.newBuilder().setIdentifier(identifier).addAllKey(keyList).build();
        RemoveMultipleScatteredReplicaResponse response;
        try {
            response = blockingStub.removeMultipleScatteredReplica(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }

    public int measureDistance(int ID, int count, int startPoint){
        MeasureDistanceRequest request = MeasureDistanceRequest.newBuilder().setID(ID).setCount(count).setStartPoint(startPoint).build();
        MeasureDistanceResponse response;
        try {
            response = blockingStub.measureDistance(request);

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return Integer.MAX_VALUE;
        }
        return response.getDistance();
    }
    public boolean put(String key, String val){
        PutRequest request = PutRequest.newBuilder().setKey(key).setValue(val).build();
        PutResponse putResponse;
        try {
            putResponse = blockingStub.put(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }

        return putResponse.getRet() == ReturnCode.SUCCESS;
    }

    public String get(String key){
        GetRequest request = GetRequest.newBuilder().setKey(key).build();
        GetResponse getResponse;
        try {
            getResponse = blockingStub.get(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }

        return getResponse.getRet() == ReturnCode.SUCCESS ? getResponse.getValue() : null;
    }

    public String tellMeFingerTable() {
        TellmeFingerTableRequest tellmeFingerTableRequest = TellmeFingerTableRequest.newBuilder().build();
        TellmeFingerTableResponse tellmeFingerTableResponse;
        try {
            tellmeFingerTableResponse = blockingStub.tellmeFingerTable(tellmeFingerTableRequest);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return "-1";
        }
        return tellmeFingerTableResponse.getFingerTable();
    }


    public int tellmeKeyNumber() {
        TellmeKeyNumberRequest request = TellmeKeyNumberRequest.newBuilder().build();
        TellmeKeyNumberResponse response;
        try {
            response = blockingStub.tellmeKeyNumber(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return 0;
        }
        return response.getNumber();
    }

    public int tellmeReplicaKeyNumber() {
        TellmeReplicaKeyNumberRequest request = TellmeReplicaKeyNumberRequest.newBuilder().build();
        TellmeReplicaKeyNumberResponse response;
        try {
            response = blockingStub.tellmeReplicaKeyNumber(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return 0;
        }
        return response.getNumber();
    }

    public FindSuccessorIterativelyResponse findSuccessorIteratively(int id) {
        FindSuccessorIterativelyRequest request = FindSuccessorIterativelyRequest.newBuilder().build();
        FindSuccessorIterativelyResponse response;

        try {
            response = blockingStub.findSuccessorIteratively(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return FindSuccessorIterativelyResponse.newBuilder().setIsCompleted(false).build();
        }
        return response;
    }

    public boolean kill(){
        KillRequest request = KillRequest.newBuilder().build();
        KillResponse response;
        try {
            response = blockingStub.kill(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return true;
    }



    public void close() {
        this.channel.shutdownNow();
    }
}
