package node;

import common.Hasher;
import common.IdentifierWithHop;
import common.JsonUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


public class ChordNodeServer {

    private static final Logger logger = Logger.getLogger(ChordNodeServer.class.getName());

    private final Server server;
    private ManagerBasedChordNodeService chordNodeService;
    private int port;

    public ChordNodeServer(int selfID, String selfIP, int selfPort, String mgrIP, int mgrPort, int ringSizeExp)  {
        this.port = selfPort;

        chordNodeService = new ManagerBasedChordNodeService(selfID, selfIP, selfPort, mgrIP, mgrPort, ringSizeExp);

        server = ServerBuilder.forPort(selfPort).addService(chordNodeService)
                .build();

       chordNodeService.setServer(server);
    }

    public void start(int selfID, String selfIP, int selfPort) throws IOException {
        server.start();
        chordNodeService.start();
        logger.info("Server started, listening on " + port);
    }



    public static void main(String[] args) {
        int id = Integer.valueOf(args[0]);
        String ip = args[1];
        int port = Integer.valueOf(args[2]);
        String mgrIP = args[3];
        int mgrPort = Integer.valueOf(args[4]);
        ChordNodeServer chordNodeServer = new ChordNodeServer(id, ip, port, mgrIP, mgrPort, 5);

        try {
            chordNodeServer.start(id, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "start server failed");
        }
    }



}