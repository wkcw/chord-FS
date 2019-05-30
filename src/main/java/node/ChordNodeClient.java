package node;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import net.grpc.chord.*;

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
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }
        return findSuccessorResponse.getIdentifier();
    }

    public boolean ping(){
        PingRequest request = PingRequest.newBuilder().build();
        PingResponse pingResponse;
        try {
            pingResponse = blockingStub.ping(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
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

    public void close() {
        this.channel.shutdownNow();
    }
}
