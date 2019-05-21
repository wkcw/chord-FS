package node;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import net.grpc.chord.*;

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
        FindSuccessorRequest request = FindSuccessorRequest.newBuilder().setId(id).build();
        FindSuccessorResponse findSuccessorResponse;
        try {
            findSuccessorResponse = blockingStub.findSuccessor(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }
        return findSuccessorResponse.getIdentifier();
    }

    public void notify(Identifier identifier){
        NotifyRequest request = NotifyRequest.newBuilder().setIdentifier(identifier).build();
        NotifyResponse notifyResponse;
        try {
            notifyResponse = blockingStub.notify(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        return;
    }
}
