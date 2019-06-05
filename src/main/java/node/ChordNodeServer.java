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

    public void start(int knownID, String knownIP, int knownPort) throws IOException {
        server.start();
        chordNodeService.start();
        logger.info("Server started, listening on " + port);
    }



    public static void main(String[] args) {
        int id = Integer.valueOf(args[0]);
        String ip = args[1];
        int port = Integer.valueOf(args[2]);
        int knownID = Integer.valueOf(args[3]);
        String knownIP = null;
        int knownPort = -1;
        if(knownID != -1){
            knownIP = args[4];
            knownPort = Integer.valueOf(args[5]);
        }

        ChordNodeServer chordNodeServer = new ChordNodeServer(id, ip, port, "localhost", 9527, 13);

        try {
            if(knownID == -1) {
                chordNodeServer.start(-1, null, -1);

            } else{
                chordNodeServer.start(knownID, knownIP, knownPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "start server failed");
        }
    }



}