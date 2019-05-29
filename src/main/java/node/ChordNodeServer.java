package node;

import common.Hasher;
import common.JsonUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.grpc.chord.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static common.JsonUtil.deserilizable;

public class ChordNodeServer {

    private static final Logger logger = Logger.getLogger(ChordNodeServer.class.getName());

    private final Server server;
    private ChordNodeService chordNodeService;
    private int port;

    public ChordNodeServer(int selfID, String selfIP, int selfPort) {
        this.port = selfPort;

        chordNodeService = new ChordNodeService(selfID, selfIP, selfPort);
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

        private HashMap<String, String> hashMap;
        private int selfID;
        private static int ringSizeExp = 5;
        private static int sucListSize = 3;
        private String selfIP;
        private int selfPort;
        private Identifier[] fingerTable;
        private Identifier[] successorsList;
        private Identifier predecessor;
        private int next;
        private Hasher hasher;


        public ChordNodeService(int selfID, String selfIP, int selfPort){
            hashMap = new HashMap<String, String>();
            this.fingerTable = new Identifier[ringSizeExp];
            this.successorsList = new Identifier[sucListSize];
            this.selfID = selfID;
            this.selfIP = selfIP;
            this.selfPort = selfPort;
            hasher = new Hasher(1 << ringSizeExp);
        }

        @Override
        public void notify(NotifyRequest request, StreamObserver<NotifyResponse> responseObserver) {
            int senderID = request.getIdentifier().getID();
            String address = request.getIdentifier().getIP();
            int port = request.getIdentifier().getPort();

            if (predecessor == null || inRange(senderID, predecessor.getID(), selfID)) {
                if (predecessor == null) predecessor = Identifier.newBuilder().build();
                predecessor = predecessor.toBuilder().setID(senderID).setIP(address).setPort(port).build();
                ChordNodeClient predecessorClient = new ChordNodeClient(predecessor.getIP(), predecessor.getPort());
                String dataJson = generateDataJsonAndDeleteLocal(predecessor.getID());
                predecessorClient.acceptMyData(dataJson);
            }
            NotifyResponse response = NotifyResponse.newBuilder().build();


            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void findSuccessor(FindSuccessorRequest request, StreamObserver<FindSuccessorResponse> responseObserver) {
            Identifier successor = this.getAliveSuccessor();

            if (predecessor != null && this.inRange(request.getID(), predecessor.getID(), selfID)) {
                FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(generateSelfIdentifier()).build();
                responseObserver.onNext(response);
            }
            else if (successor != null && (this.inRange(request.getID(), selfID, successor.getID())))
            {
                FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(successor).build();
                responseObserver.onNext(response);
            } else{
                int searchedID = request.getID();
                Identifier nextIdentifier = closestPrecedingFinger(searchedID);

                ChordNodeClient client = new ChordNodeClient(nextIdentifier.getIP(), nextIdentifier.getPort());
                Identifier searchedIdentifier = client.findSuccessor(searchedID);
                client.close();

                FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(searchedIdentifier).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }

        public void create(){
            predecessor = null;
            this.fingerTable[0] = Identifier.newBuilder().setID(selfID).setIP(selfIP).setPort(selfPort).build();
            this.successorsList[0] = this.fingerTable[0];

            for (int i = 1;i < ringSizeExp;i++) this.fingerTable[i] = Identifier.newBuilder().setID(-1).build();
            for (int i = 1;i < sucListSize;i++) this.successorsList[i] = Identifier.newBuilder().setID(-1).build();
        }

        public void join(Identifier knownNodeIdentifier){
            predecessor = null;
            logger.info("Creating client for join");
            ChordNodeClient knownNodeClient = new ChordNodeClient(knownNodeIdentifier.getIP(), knownNodeIdentifier.getPort());

            this.fingerTable[0] = knownNodeClient.findSuccessor(selfID);
            this.successorsList[0] = this.fingerTable[0];
            knownNodeClient.close();
        }

        public void stabilize() {
            Identifier successor = this.getAliveSuccessor();

            if (successor.getID() != this.fingerTable[0].getID()) {
                this.fingerTable[0] = successor;
                this.successorsList[0] = successor;
            }

            if (successor != null) {
                logger.info("Creating client for stabilize on successor");
                ChordNodeClient successorClient = new ChordNodeClient(successor.getIP(), successor.getPort());
                Identifier successorPredecessor = successorClient.inquirePredecessor();

                if (!successorPredecessor.getIP().equals("") && inRange(successorPredecessor.getID(), selfID, successor.getID())) {
                    successorClient.close();

                    successorClient = new ChordNodeClient(successorPredecessor.getIP(), successorPredecessor.getPort());
                    if (successorClient.ping()) {
                        this.fingerTable[0] = successorPredecessor;
                        this.successorsList[0] = this.fingerTable[0];
                    } else  {
                        successorClient = new ChordNodeClient(successor.getIP(), successor.getPort());
                    }
                }
                successorClient.notify(this.generateSelfIdentifier());

                successorClient.close();
            }

            updateSuccessorsList();
        }

        public Identifier getAliveSuccessor() {
            for (int i = 0;i < sucListSize;i++) {
                Identifier curSuccessor = this.successorsList[i];
                ChordNodeClient client = new ChordNodeClient(curSuccessor.getIP(), curSuccessor.getPort());
                if (client.ping()) {
                    client.close();

                    return curSuccessor;
                }

                client.close();
            }

            return generateSelfIdentifier();
        }

        public void checkPredecessor(){
            if (this.predecessor != null) {
                logger.info("Creating client for checkPredecessor");
                ChordNodeClient client = new ChordNodeClient(this.predecessor.getIP(), this.predecessor.getPort());
                if (!client.ping()) {
                    this.predecessor = null;
                }

                client.close();
            }
        }

        public boolean inRange(int id, int leftID, int rightID) {
            if (leftID < rightID) {
                if (id > leftID && id <= rightID) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (id > leftID || id <= rightID) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        public void updateSuccessorsList() {
            Identifier successor = this.getAliveSuccessor();
            ChordNodeClient successorClient = new ChordNodeClient(successor.getIP(), successor.getPort());
            List<Identifier> successorsList = new ArrayList<>(successorClient.inquireSuccessorsList());
            successorClient.close();

            successorsList.remove(successorsList.size() - 1);
            successorsList.add(0, successor);

            successorsList.toArray(this.successorsList);

            printSuccessorList();
        }

        @Override
        public void inquireSuccessorsList(InquireSuccessorsListRequest request, StreamObserver<InquireSuccessorsListResponse> responseObserver) {
            List<Identifier> sucList = Arrays.asList(this.successorsList);
            InquireSuccessorsListResponse response = InquireSuccessorsListResponse.newBuilder().addAllSuccessorsList(sucList).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {
            String key = request.getKey();
            String value = request.getValue();
            PutResponse response;

            if (!inRange(hasher.hash(key), predecessor.getID(), selfID)) {
                response = PutResponse.newBuilder().setRet(ReturnCode.FAILURE).build();
            } else {
                hashMap.put(key, value);
                response = PutResponse.newBuilder().setRet(ReturnCode.SUCCESS).build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {
            String key = request.getKey();

            if (!hashMap.containsKey(key)) {
                GetResponse response = GetResponse.newBuilder().setRet(ReturnCode.FAILURE).build();
                responseObserver.onNext(response);
            }
            else {
                String value = hashMap.get(key);
                GetResponse response = GetResponse.newBuilder().setValue(value).setRet(ReturnCode.SUCCESS).build();
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
        public void inquirePredecessor(InquirePredecessorRequest request, StreamObserver<InquirePredecessorResponse> responseObserver) {
            InquirePredecessorResponse response;
            if(predecessor == null){
                response = InquirePredecessorResponse.newBuilder().build();
            }else{
                response = InquirePredecessorResponse.newBuilder().setIdentifier(predecessor).build();
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }

        @Override
        public void transferData(TransferDataRequest request, StreamObserver<TransferDataResponse> responseObserver) {
            int requestID = request.getID();
            HashMap<String, String> hashMapToTransfer = new HashMap<>();
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if(hasher.hash(entry.getKey()) <= requestID){
                    hashMapToTransfer.put(entry.getKey(), entry.getValue());
                }
            }
            String dataJson = JsonUtil.serilizable(hashMapToTransfer);
            TransferDataResponse response = TransferDataResponse.newBuilder().setDataJson(dataJson).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void acceptMyData(AcceptMyDataRequest request, StreamObserver<AcceptMyDataResponse> responseObserver) {
            String dataJson = request.getDataJson();
            HashMap<String, String> gotHashMap = JsonUtil.deserilizable(dataJson);
            for (Map.Entry<String, String> entry : gotHashMap.entrySet()) {
                hashMap.put(entry.getKey(), entry.getValue());
            }
            AcceptMyDataResponse response = AcceptMyDataResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }


        public void start(int id, String ip, int port){
            create();
            if (id != -1) {
                Identifier identifier = Identifier.newBuilder().setID(id).setIP(ip).setPort(port).build();
                join(identifier);
            }

            this.next = 0;
            Timer timer = new Timer();

            StabilizeTask stabilizeTask = new StabilizeTask();
            timer.schedule(stabilizeTask, 1000, 1000);

            CheckPredecessorTask checkPredecessorTask = new CheckPredecessorTask();
            timer.schedule(checkPredecessorTask, 1000, 1000);

            FixFingersTask fixFingersTask = new FixFingersTask();
            timer.schedule(fixFingersTask, 1000, 500);
        }

        private void printFingerTable() {
            logger.info("||index || value");

            StringBuilder sb = new StringBuilder();
            for (int i = 0;i < ringSizeExp;i++) {
                sb.append(String.format("||   %d   || %d\n", i, fingerTable[i].getID()));
            }
            System.out.println(sb.toString());
        }

        private void printSuccessorList() {
            logger.info("||index || value");

            StringBuilder sb = new StringBuilder();
            for (int i = 0;i < sucListSize;i++) {
                sb.append(String.format("||   %d   || %d\n", i, successorsList[i].getID()));
            }
            System.out.println(sb.toString());
        }


        public Identifier closestPrecedingFinger(int id) {
            for (int i = ringSizeExp - 1;i >= 0;i--) {
                if (fingerTable[i].getID() == -1) {
                    continue;
                }

                if (inRange(fingerTable[i].getID(), selfID, id)) {
                    return fingerTable[i];
                }
            }

            return generateSelfIdentifier();
        }

        public void fixFingers() {
            this.next = (this.next + 1) % ringSizeExp;

            ChordNodeClient selfClient = new ChordNodeClient(selfIP, selfPort);
            Identifier searchedIdentifier = selfClient.findSuccessor((selfID + (1 << this.next)) % (1 << ringSizeExp));

            selfClient.close();

            this.fingerTable[this.next] = searchedIdentifier;

            printFingerTable();
        }


        class StabilizeTask extends TimerTask {
            public void run() {
                stabilize();
                logger.info(String.format("Successor : %d", getAliveSuccessor().getID()));
            }
        }

        class CheckPredecessorTask extends TimerTask {
            public void run() {
                checkPredecessor();
                logger.info(String.format("Predecessor : %d", predecessor == null ? -1 : predecessor.getID()));
            }
        }

        class FixFingersTask extends TimerTask {
            public void run() {
                fixFingers();
                logger.info(String.format("Predecessor : %d", predecessor == null ? -1 : predecessor.getID()));
            }
        }


        private Identifier generateSelfIdentifier(){
            Identifier identifier = Identifier.newBuilder().setID(selfID).setIP(selfIP)
                    .setPort(selfPort).build();

            return identifier;
        }

        private String generateDataJsonAndDeleteLocal(int predecessorID){
            HashMap<String, String> hashMapToTransfer = new HashMap<>();
//            prepare keys to transfer
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if(hasher.hash(entry.getKey()) <= predecessorID){
                    hashMapToTransfer.put(entry.getKey(), entry.getValue());
                }
            }
//            remove local keys
            for (Map.Entry<String, String> entry : hashMapToTransfer.entrySet()) {
                if(hasher.hash(entry.getKey()) <= predecessorID){
                    hashMap.remove(entry.getKey());
                }
            }
            String dataJson = JsonUtil.serilizable(hashMapToTransfer);
            return dataJson;
        }
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

        ChordNodeServer chordNodeServer = new ChordNodeServer(id, ip, port);

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