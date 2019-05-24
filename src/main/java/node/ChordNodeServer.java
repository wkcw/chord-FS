package node;

import common.JsonUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChordNodeServer {

    private static final Logger logger = Logger.getLogger(ChordNodeServer.class.getName());

    private final Server server;
    private ChordNodeService chordNodeService;
    private int port;

    public ChordNodeServer(int selfID, String selfIP, int selfPort, int lenFingerTable) {
        this.port = selfPort;

        chordNodeService = new ChordNodeService(selfID, selfIP, selfPort, lenFingerTable);
        server = ServerBuilder.forPort(selfPort).addService(chordNodeService)
                .build();
    }

    public void start(int knownID, String knownIP, int knownPort) throws IOException {
        server.start();
        chordNodeService.start(knownID, knownIP, knownPort);
        logger.info("Server started, listening on " + port);

    }



    private static class ChordNodeService extends ChordNodeServiceGrpc.ChordNodeServiceImplBase {

        private static final String TAG = ChordNodeService.class.getName();

        private HashMap<Integer, String> hashMap;
        private int selfID;
        private static int ringSizeExp = 5;
        private String selfIP;
        private int selfPort;
        private int[] fingerTable;
        private Identifier successor;
        private Identifier predecessor;

        public ChordNodeService(int selfID, String selfIP, int selfPort, int lenFingerTable){
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
            int port = request.getIdentifier().getPort();

            if (predecessor == null || inRange(senderID, predecessor.getID(), selfID)) {
                if (predecessor == null) predecessor = Identifier.newBuilder().build();

                predecessor = predecessor.toBuilder().setID(senderID).setIP(address).setPort(port).build();
            }
            NotifyResponse response = NotifyResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void findSuccessor(FindSuccessorRequest request, StreamObserver<FindSuccessorResponse> responseObserver) {
            if(this.inRange(request.getID(), selfID, successor.getID())){
                FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(successor).build();
                responseObserver.onNext(response);
            }else{
                int searchedID = request.getID();
                logger.info("Creating client for findSuccessor");
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
            logger.info("Creating client for join");
            ChordNodeClient knownNodeClient = new ChordNodeClient(knownNodeIdentifier.getIP(), knownNodeIdentifier.getPort());
            successor = knownNodeClient.findSuccessor(selfID);
        }

        public void stabilize() {
            if (this.successor != null) {
                logger.info("Creating client for stabilize on successor");
                ChordNodeClient successorClient = new ChordNodeClient(successor.getIP(), successor.getPort());
                Identifier successorPredecessor = successorClient.tellmePredecessor();

                if (!successorPredecessor.getIP().equals("") && inRange(successorPredecessor.getID(), selfID, this.successor.getID())) {
                    successorClient = new ChordNodeClient(successorPredecessor.getIP(), successorPredecessor.getPort());
                    if (successorClient.ping()) {
                        this.successor = successorPredecessor;
                    }
                }
                successorClient.notify(this.generateSelfIdentifier());
            }
        }

        public void checkPredecessor(){
            if (this.predecessor != null) {
                logger.info("Creating client for checkPredecessor");
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
        public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {
            int key = request.getKey();
            String value = request.getValue();
            hashMap.put(key, value);
            PutResponse response = PutResponse.newBuilder().setRet(ReturnCode.SUCCESS).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {
            int key = request.getKey();
            if (!hashMap.containsKey(key)) {
                GetResponse response = GetResponse.newBuilder().setRet(ReturnCode.FAILURE).build();
                responseObserver.onNext(response);
            }
            else {
                String value = hashMap.get(key);
                GetResponse response = GetResponse.newBuilder().setRet(ReturnCode.SUCCESS).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }



        @Override
        public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
            PingResponse response = PingResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void tellmePredecessor(TellmePredecessorRequest request, StreamObserver<TellmePredecessorResponse> responseObserver) {
            TellmePredecessorResponse response;
            if(predecessor == null){
                response = TellmePredecessorResponse.newBuilder().build();
            }else{
                response = TellmePredecessorResponse.newBuilder().setIdentifier(predecessor).build();
            }
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
                    .setPort(selfPort).build();
            if(predecessor != null){
                identifier.toBuilder().setPredecessor(predecessor).build();
            }
            identifier.toBuilder().setSuccessor(successor).build();
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
                stabilize();
                logger.info(String.format("Successor : %d", successor.getID()));
            }
        }

        class CheckPredecessorTask extends TimerTask {
            public void run() {
                checkPredecessor();
                logger.info(String.format("Predecessor : %d", predecessor == null ? -1 : predecessor.getID()));
            }
        }

    }

    public static void main(String[] args) {
        int id = Integer.valueOf(args[0]);
        String ip = args[1];
        int port = Integer.valueOf(args[2]);
        int lenFingerTable = Integer.valueOf(args[3]);
        int knownID = Integer.valueOf(args[4]);
        String knownIP = null;
        int knownPort = -1;
        if(knownID != -1){
            knownIP = args[5];
            knownPort = Integer.valueOf(args[6]);
        }


        ChordNodeServer chordNodeServer = new ChordNodeServer(id, ip, port, lenFingerTable);

        try {
            if(knownID==-1){
                chordNodeServer.start(-1, null, -1);

            }else{
                chordNodeServer.start(knownID, knownIP, knownPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "start server failed");
        }
    }



}

