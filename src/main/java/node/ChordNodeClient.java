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

    public boolean put(){
        PutRequest request = PutRequest.newBuilder().build();
        PutResponse putResponse;
        try {
            putResponse = blockingStub.put(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }

        return putResponse.getRet() == ReturnCode.SUCCESS;
    }

    public String get(){
        GetRequest request = GetRequest.newBuilder().build();
        GetResponse getResponse;
        try {
            getResponse = blockingStub.get(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }

        return getResponse.getValue();
    }

    public boolean notify(Identifier identifier){
        NotifyRequest request = NotifyRequest.newBuilder().setIdentifier(identifier).build();
        try {
            blockingStub.notify(request);
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

    public void leave() {
        LeaveRequest request = LeaveRequest.newBuilder().build();

        try {
            blockingStub.leave(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
        }
    }

    public boolean transferData(String hashmapJsonString){
        TransferDataRequest request = TransferDataRequest.newBuilder().setDataJson(hashmapJsonString).build();
        TransferDataResponse response;
        try{
            response = blockingStub.transferData(request);
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
