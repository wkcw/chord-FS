package node;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import net.grpc.chord.ChordManagerServiceGrpc;
import net.grpc.chord.Identifier;
import net.grpc.chord.JoinRequest;
import net.grpc.chord.JoinResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ChordManagerClient {
    private static final Logger logger = Logger.getLogger(ChordManagerClient.class.getName());

    private final ManagedChannel channel;
    private final ChordManagerServiceGrpc.ChordManagerServiceBlockingStub blockingStub;
    private final ChordManagerServiceGrpc.ChordManagerServiceStub asyncStub;

    public ChordManagerClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public ChordManagerClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = ChordManagerServiceGrpc.newBlockingStub(channel);
        asyncStub = ChordManagerServiceGrpc.newStub(channel);
    }

    public Identifier join(int ID, String IP, int port) {
        JoinRequest joinRequest = JoinRequest.newBuilder().setID(ID)
                .setAddress(IP).setPort(port).build();
        JoinResponse joinResponse;

        try {
            joinResponse = blockingStub.join(joinRequest);
        } catch (StatusRuntimeException e){
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }

        int successorID = joinResponse.getID();
        String successorIP = joinResponse.getAddress();
        int successorPort = joinResponse.getPort();


        Identifier successorIdentifier = Identifier.newBuilder()
                                         .setID(successorID).setIP(successorIP).setPort(successorPort).build();

        return successorIdentifier;

    }

    public void close() {
        this.channel.shutdownNow();
    }


}