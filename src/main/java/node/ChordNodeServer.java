package node;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChordNodeServer {


    private static final Logger logger = Logger.getLogger(ChordNodeServer.class.getName());

    private final Server server;
    private ManagerBasedChordNodeService chordNodeService;
    private int port;

    public ChordNodeServer(int selfID, String selfIP, int selfPort, String mgrIP, int mgrPort) {
        this.port = selfPort;

        chordNodeService = new ManagerBasedChordNodeService(selfID, selfIP, selfPort, mgrIP, mgrPort);
        server = ServerBuilder.forPort(selfPort).addService(chordNodeService)
                .build();
    }

    private void start(int selfID, String selfIP, int selfPort) throws IOException {
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
//        int knownID = Integer.valueOf(args[3]);
//        String knownIP = null;
//        int knownPort = -1;
//        if(knownID != -1){
//            knownIP = args[4];
//            knownPort = Integer.valueOf(args[5]);
//        }

        ChordNodeServer chordNodeServer = new ChordNodeServer(id, ip, port, mgrIP, mgrPort);

        try {
            chordNodeServer.start(id, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "start server failed");
        }
    }
}