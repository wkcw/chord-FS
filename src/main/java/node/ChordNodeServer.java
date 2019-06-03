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
import java.util.logging.Logger;

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

//        private static final String TAG = ChordNodeService.class.getName();

        private Map<String, String> hashMap;
        private Map<Integer, Map<String, String>> replica;
        private int selfID;
        private static int ringSizeExp = 10;
        private static int sucListSize = 3;
        private String selfIP;
        private int selfPort;
        private Identifier[] fingerTable;
        private Identifier[] successorsList;
        private Identifier predecessor;
        private int next;
        private Hasher hasher;


        public ChordNodeService(int selfID, String selfIP, int selfPort){
            hashMap = new ConcurrentHashMap<>();
            replica = new ConcurrentHashMap<>();
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

            if (predecessor == null || (inRange(senderID, predecessor.getID(), selfID) && predecessor.getID() != senderID)) {
                if (predecessor == null) predecessor = Identifier.newBuilder().build();
                predecessor = predecessor.toBuilder().setID(senderID).setIP(address).setPort(port).build();
                ChordNodeClient predecessorClient = new ChordNodeClient(predecessor.getIP(), predecessor.getPort());
                List<String> keyList = generateExpiredKeyList(predecessor.getID());
                String dataJson = generateDataJsonAndDeleteLocal(keyList);
                predecessorClient.acceptMyData(dataJson);
                predecessorClient.close();
                removePartialDataFromReplicas(keyList);
                inheritFailedPredecessorsData(senderID);
            }
            NotifyResponse response = NotifyResponse.newBuilder().build();


            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }


        @Override
        public void findSuccessor(FindSuccessorRequest request, StreamObserver<FindSuccessorResponse> responseObserver) {
            Identifier successor = this.getAliveSuccessor();
            int newHop = request.getHop() + 1;

            if (predecessor != null && this.inRange(request.getID(), predecessor.getID(), selfID)) {
                FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(generateSelfIdentifier()).setHop(newHop).build();
                responseObserver.onNext(response);
            }
            else if (successor != null && (this.inRange(request.getID(), selfID, successor.getID())))
            {
                FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(successor).setHop(newHop).build();
                responseObserver.onNext(response);
            } else{
                int searchedID = request.getID();
                Identifier nextIdentifier = closestPrecedingFinger(searchedID);

                ChordNodeClient client = new ChordNodeClient(nextIdentifier.getIP(), nextIdentifier.getPort());
                IdentifierWithHop searchedIdentifierWithHop = client.findSuccessorWithHop(searchedID, newHop);
                client.close();

                FindSuccessorResponse response = FindSuccessorResponse.newBuilder().setIdentifier(searchedIdentifierWithHop.identifier).setHop(searchedIdentifierWithHop.hop).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }

        @Override
        public void measureDistance(MeasureDistanceRequest request, StreamObserver<MeasureDistanceResponse> responseObserver) {
            int searchingID = request.getID();
            int startPoint = request.getStartPoint();
            int distance;
            if(startPoint == selfID){
                distance = Integer.MAX_VALUE;
            }else if(searchingID == selfID){
                distance = 1;
            }else if(predecessor != null && inRange(searchingID, predecessor.getID(), selfID)) {
                distance = 0;
            }else if(predecessor != null){
                logger.info(String.format("In RPC measureDistance -> Creating client for measureDistance, to IP:%s Port:%d", predecessor.getIP(), predecessor.getPort()));
                ChordNodeClient predecessorClient = new ChordNodeClient(predecessor.getIP(), predecessor.getPort());
                distance = predecessorClient.measureDistance(searchingID);
                predecessorClient.close();
                if(distance != Integer.MAX_VALUE){
                    distance++;
                }
            }else{
                distance = Integer.MAX_VALUE;
            }
            MeasureDistanceResponse response = MeasureDistanceResponse.newBuilder().setDistance(distance).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        private void create(){
            predecessor = null;
            this.fingerTable[0] = Identifier.newBuilder().setID(selfID).setIP(selfIP).setPort(selfPort).build();

            this.successorsList[0] = this.fingerTable[0];

            for (int i = 1;i < ringSizeExp;i++) this.fingerTable[i] = Identifier.newBuilder().setID(-1).build();
            for (int i = 1;i < sucListSize;i++) this.successorsList[i] = Identifier.newBuilder().setID(-1).build();
        }

        private void join(Identifier knownNodeIdentifier){
            predecessor = null;
            logger.info("Creating client for join");
            ChordNodeClient knownNodeClient = new ChordNodeClient(knownNodeIdentifier.getIP(), knownNodeIdentifier.getPort());

            this.fingerTable[0] = knownNodeClient.findSuccessor(selfID);

            maintainFirstReplica(this.successorsList[0], this.fingerTable[0]);
            this.successorsList[0] = this.fingerTable[0];

            knownNodeClient.close();
        }

        private void stabilize() {
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

                        maintainFirstReplica(this.successorsList[0], this.fingerTable[0]);
                        this.successorsList[0] = this.fingerTable[0];
                    } else  {
                        successorClient = new ChordNodeClient(successor.getIP(), successor.getPort());
                    }
                }
                successorClient.notify(this.generateSelfIdentifier());

                successorClient.close();
            }

            updateSuccessorsList();

            printKeyValue();
            printReplica();
        }

        private Identifier getAliveSuccessor() {
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

        private void checkPredecessor(){
            if (this.predecessor != null && this.predecessor.getID() != -1) {
                logger.info("Creating client for checkPredecessor");
                ChordNodeClient predecessorClient = new ChordNodeClient(this.predecessor.getIP(), this.predecessor.getPort());
                boolean alive = predecessorClient.ping();
                predecessorClient.close();
                if (!alive) {
                    Identifier failedPredecessor = this.predecessor;
                    this.predecessor = null;
                    inheritPredecessorData(failedPredecessor.getID());
                }

            }
        }

        private boolean inRange(int id, int leftID, int rightID) {
            if (leftID < rightID) {
                return id > leftID && id <= rightID;
            } else {
                return id > leftID || id <= rightID;
            }
        }

        private void updateSuccessorsList() {
            Identifier successor = this.getAliveSuccessor();
            ChordNodeClient successorClient = new ChordNodeClient(successor.getIP(), successor.getPort());
            List<Identifier> successorsList = new ArrayList<>(successorClient.inquireSuccessorsList());
            successorClient.close();



            Identifier[] oldSuccessorList = Arrays.copyOf(this.successorsList, this.successorsList.length);

            successorsList.remove(successorsList.size() - 1);
            successorsList.add(0, successor);

            // deduplicate
            HashSet<Identifier> set = new HashSet<>();
            for (int i = 0; i < successorsList.size(); i++) {
                // encounter -1 indicating no valid node after this point
                if (successorsList.get(i).getID() == -1) {
                    break;
                }
                // add the current identifier to the hashset for deduplication
                if (set.add(successorsList.get(i))) {
                    continue;
                }
                // if adding failed, duplicate node occurs, set the identifier to -1 (null)
                else {
                    successorsList.set(i, Identifier.newBuilder().setID(-1).build());
                }
            }

            successorsList.toArray(this.successorsList);

            maintainSubsequentReplicas(oldSuccessorList, this.successorsList);

            printSuccessorList();
        }

        private void inspectRedundancy() {
            if(predecessor != null){
                logger.info(String.format("Creating client for measureDistance, to IP:%s Port:%d", predecessor.getIP(), predecessor.getPort()));
                ChordNodeClient predecessorClient = new ChordNodeClient(predecessor.getIP(), predecessor.getPort());
                for(int ID : replica.keySet()){
                    int distance = predecessorClient.measureDistance(ID);
                    if(distance != Integer.MAX_VALUE){
                        if(distance > sucListSize){
                            if(ID==4){
                                System.out.println("remove4");
                            }
                            replica.remove(ID);
                        }
                    }
                }
                predecessorClient.close();
            }
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

//                put to all successors
                for(Identifier identifier : successorsList){
//                    verify identifier
                    if(identifier.getID() == -1) continue;
                    ChordNodeClient successorClient = new ChordNodeClient(identifier.getIP(), identifier.getPort());
                    successorClient.addScatteredReplica(generateSelfIdentifier(), key, value);
                    successorClient.close();
                }
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
            HashMap<String, String> hashMapToTransfer = generateTransferredMap(requestID);

            String dataJson = JsonUtil.serilizable(hashMapToTransfer);
            TransferDataResponse response = TransferDataResponse.newBuilder().setDataJson(dataJson).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void acceptMyData(AcceptMyDataRequest request, StreamObserver<AcceptMyDataResponse> responseObserver) {
            String dataJson = request.getDataJson();
            Map<String, String> gotHashMap = JsonUtil.deserilizable(dataJson);
            for (Map.Entry<String, String> entry : gotHashMap.entrySet()) {
                hashMap.put(entry.getKey(), entry.getValue());
            }
            AcceptMyDataResponse response = AcceptMyDataResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void removeReplica(RemoveReplicaRequest request, StreamObserver<RemoveReplicaResponse> responseObserver) {
            int replicaTagID = request.getIdentifier().getID();

            this.replica.remove(replicaTagID);
            RemoveReplicaResponse response = RemoveReplicaResponse.newBuilder().build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void addReplica(AddReplicaRequest request, StreamObserver<AddReplicaResponse> responseObserver) {
            int requestTagID = request.getIdentifier().getID();
            String dataJson = request.getJsonData();
            Map<String, String> hashMapToAdd = JsonUtil.deserilizable(dataJson);
            this.replica.put(requestTagID, hashMapToAdd);

            AddReplicaResponse response = AddReplicaResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void addScatteredReplica(AddScatteredReplicaRequest request, StreamObserver<AddScatteredReplicaResponse> responseObserver){
            String key = request.getKey();
            String value = request.getValue();
            int requestTagID = request.getIdentifier().getID();
            if(replica.get(requestTagID) == null){
                replica.put(requestTagID, new HashMap<>());
            }
            replica.get(requestTagID).put(key, value);

            AddScatteredReplicaResponse response = AddScatteredReplicaResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void addMultipleScatteredReplica(AddMultipleScatteredReplicaRequest request, StreamObserver<AddMultipleScatteredReplicaResponse> responseObserver) {
            String dataJson = request.getJsonData();
            int requestTagID = request.getIdentifier().getID();
            Map<String, String> hashmapToAdd = JsonUtil.deserilizable(dataJson);
            if(replica.get(requestTagID) == null){
                replica.put(requestTagID, new HashMap<>());
            }
            replica.get(requestTagID).putAll(hashmapToAdd);

            AddMultipleScatteredReplicaResponse response = AddMultipleScatteredReplicaResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void removeMultipleScatteredReplica(RemoveMultipleScatteredReplicaRequest request, StreamObserver<RemoveMultipleScatteredReplicaResponse> responseObserver) {
            List<String> keyList = request.getKeyList();
            int requestTagID = request.getIdentifier().getID();
            for(String key : keyList){
                if(replica.get(requestTagID) == null) continue;
                replica.get(requestTagID).remove(key);
            }
            RemoveMultipleScatteredReplicaResponse response = RemoveMultipleScatteredReplicaResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }



        private void start(int id, String ip, int port){
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

            InspectRedundancyTask inspectRedundancyTask = new InspectRedundancyTask();
            timer.schedule(inspectRedundancyTask, 1000, 500);

            PrintStatusTask printStatusTask = new PrintStatusTask();
            timer.schedule(printStatusTask, 1000, 500);
        }

        private void printFingerTable() {
            logger.info("Current Node ID:" + selfID);
            logger.info("||index || value");

            StringBuilder sb = new StringBuilder();
            for (int i = 0;i < ringSizeExp;i++) {
                sb.append(String.format("||   %d   || %d\n", i, fingerTable[i].getID()));
            }
            System.out.println(sb.toString());
        }

        private void printKeyValue() {
            logger.info("||key || value");

            StringBuilder sb = new StringBuilder();
            for (String key : hashMap.keySet()) {
                sb.append(String.format("||%s  || %s\n", key, hashMap.get(key)));
            }
            System.out.println(sb.toString());
        }

        private void printReplica() {
//            logger.info("||ID || value");

            System.out.println("||ID || value\n"+this.replica);
        }

        private void printSuccessorList() {
//            logger.info("||index || value");

            StringBuilder sb = new StringBuilder();
            sb.append("||index || value\n");
            for (int i = 0;i < sucListSize;i++) {
                sb.append(String.format("||   %d   || %d\n", i, successorsList[i].getID()));
            }
            System.out.println(sb.toString());
        }

        private void printStatus(){
//            print all status related to this node
            String tabToken = "----";
            StringBuilder sb = new StringBuilder();
            sb.append("All status of this node\n");
            sb.append("Primary Storage\n");
            for (String key : this.hashMap.keySet()) {
                sb.append(String.format("||%s  || %s\n", key, this.hashMap.get(key)));
            }
            sb.append("Replica Storage\n");
            for (int tagID : this.replica.keySet()) {
                sb.append("replica_" + tagID + "\n");
                for (String key : this.replica.get(tagID).keySet()) {
                    sb.append(String.format("%s||%s  || %s\n", tabToken, key, this.replica.get(tagID).get(key)));
                }
            }
            sb.append("Predecessor: " + (this.predecessor == null ? "null" : this.predecessor.getID()) + "\n");
            sb.append("SuccessorsList:\n");
            sb.append("||index || value\n");
            for (int i = 0;i < sucListSize;i++) {
                sb.append(String.format("||   %d   || %d\n", i, successorsList[i].getID()));
            }
            System.out.println(sb.toString());
        }

        private Identifier closestPrecedingFinger(int id) {
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

        private void fixFingers() {
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

        class PrintStatusTask extends TimerTask {
            public void run() {
                printStatus();
                logger.info(String.format("Predecessor : %d", predecessor == null ? -1 : predecessor.getID()));
            }
        }

        class InspectRedundancyTask extends TimerTask {
            public void run() {
                inspectRedundancy();
            }
        }

        private void maintainFirstReplica(Identifier oldSuccessor, Identifier newSuccessor) {
//            if old and new are actually the same, do nothing
            if(oldSuccessor != null && newSuccessor != null && oldSuccessor.getID() == newSuccessor.getID()) return;

            if (newSuccessor != null && newSuccessor.getID() != -1 && newSuccessor.getID() != selfID) {
                ChordNodeClient newSuccessorClient = new ChordNodeClient(newSuccessor.getIP(), newSuccessor.getPort());
                String dataJson = JsonUtil.serilizable(hashMap);
                if(newSuccessorClient.ping()){
                    newSuccessorClient.addReplica(generateSelfIdentifier(), dataJson);
                }
                newSuccessorClient.close();
            }
        }


//        DEBUG successor 0 hasnt been replicated
        private void maintainSubsequentReplicas(Identifier[] oldList, Identifier[] newList){
            HashSet<Identifier> oldSet = new HashSet<>(Arrays.asList(oldList));
            HashSet<Identifier> newSet = new HashSet<>(Arrays.asList(newList));

//            System.out.println(oldSet);
//            System.out.println(newSet);

            HashSet<Identifier> tmp = new HashSet<>(oldSet);
            oldSet.removeAll(newSet);
            newSet.removeAll(tmp);

            System.out.println("Old to remove and New to add");
            System.out.println(oldSet);
            System.out.println(newSet);


            for(Identifier identifier : oldSet){
                if(identifier.getID() == -1 || identifier.getID() == selfID)continue;
                ChordNodeClient oldSuccessorClient = new ChordNodeClient(identifier.getIP(), identifier.getPort());
                if(oldSuccessorClient.ping()){
                    oldSuccessorClient.removeReplica(generateSelfIdentifier());
                }
                oldSuccessorClient.close();
            }

            for (Identifier identifier : newSet) {
                if(identifier.getID() == -1 || identifier.getID() == selfID)continue;
                ChordNodeClient newSuccessorClient = new ChordNodeClient(identifier.getIP(), identifier.getPort());
                String dataJson = JsonUtil.serilizable(hashMap);
                if(newSuccessorClient.ping()){
                    newSuccessorClient.addReplica(generateSelfIdentifier(), dataJson);
                }
                newSuccessorClient.close();
            }
        }

        private void removePartialDataFromReplicas(List<String> keyList){
            Identifier selfIdentifier = generateSelfIdentifier();

            for(Identifier identifier : successorsList){
                if(identifier.getID() == -1) continue;
                ChordNodeClient successorClient = new ChordNodeClient(identifier.getIP(), identifier.getPort());
                successorClient.removeMultipleScatteredReplica(selfIdentifier, keyList);
                successorClient.close();
            }
        }

        private void addHashMapReplicaToOneReplica(Identifier targetIdentifier, Map<String, String> addedHashMap){
            if(addedHashMap.size() != 0 && validIdentifier(targetIdentifier)){
                ChordNodeClient targetClient = new ChordNodeClient(targetIdentifier.getIP(), targetIdentifier.getPort());
                if(targetClient.ping()){
                    String dataJson = JsonUtil.serilizable(addedHashMap);
                    targetClient.addMultipleScatteredReplica(generateSelfIdentifier(), dataJson);
                }
                targetClient.close();
            }
        }



        private void inheritPredecessorData(int failedPredecessorID){
            logger.info(String.format("Inheriting data from %d", failedPredecessorID));
            hashMap.putAll(replica.get(failedPredecessorID));
            Map<String, String> addedHashMap = replica.get(failedPredecessorID);
            replica.remove(failedPredecessorID);

//            update last replica to have data of failed one
            Identifier lastSuccessor = successorsList[successorsList.length-1];
            addHashMapReplicaToOneReplica(lastSuccessor, addedHashMap);
        }

        private void inheritFailedPredecessorsData(int newPredecessorID){
            logger.info(String.format("Inheriting data since %d", newPredecessorID));
//            all data inherited from failed nodes
            Map<String, String> addedHashMap = new HashMap<>();
            for(int replicaTagID : replica.keySet()){
                if(inRange(replicaTagID, newPredecessorID, selfID)){
                    hashMap.putAll(replica.get(replicaTagID));
                    addedHashMap.putAll(replica.get(replicaTagID));
                    replica.remove(replicaTagID);
                }
            }
//            update last replica to have data of failed one
            Identifier lastSuccessor = successorsList[successorsList.length-1];
            addHashMapReplicaToOneReplica(lastSuccessor, addedHashMap);
        }

        private Identifier generateSelfIdentifier(){
            return Identifier.newBuilder().setID(selfID).setIP(selfIP)
                    .setPort(selfPort).build();
        }

        private List<String> generateExpiredKeyList(int predecessorID){
            List<String> keyList = new ArrayList<>();
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if(!inRange(hasher.hash(entry.getKey()), predecessorID, selfID)){
                    keyList.add(entry.getKey());
                }
            }
            return keyList;
        }

        private HashMap<String, String> generateTransferredMap(int id) {
            HashMap<String, String> hashMapToTransfer = new HashMap<>();
//            prepare keys to transfer
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if(hasher.hash(entry.getKey()) <= id){
                    hashMapToTransfer.put(entry.getKey(), entry.getValue());
                }
            }

            return hashMapToTransfer;
        }

        private HashMap<String, String> generateTransferredMap(List<String> keyList) {
            HashMap<String, String> hashMapToTransfer = new HashMap<>();
//            prepare keys to transfer
            for (String key : keyList) {
                hashMapToTransfer.put(key, hashMap.get(key));
            }

            return hashMapToTransfer;
        }

        private String generateDataJsonAndDeleteLocal(int predecessorID){
            HashMap<String, String> hashMapToTransfer = generateTransferredMap(predecessorID);

//            remove local keys
            for (Map.Entry<String, String> entry : hashMapToTransfer.entrySet()) {
                if(hasher.hash(entry.getKey()) <= predecessorID){
                    hashMap.remove(entry.getKey());
                }
            }

            return JsonUtil.serilizable(hashMapToTransfer);
        }

        private String generateDataJsonAndDeleteLocal(List<String> keyList){
            HashMap<String, String> hashMapToTransfer = generateTransferredMap(keyList);

//            remove local keys
            for (String key : keyList) {
                hashMap.remove(key);
            }

            return JsonUtil.serilizable(hashMapToTransfer);
        }

        private boolean validIdentifier(Identifier identifier){
            return identifier != null && identifier.getID() != -1;
        }











        @Override
        public void tellmeKeyNumber(TellmeKeyNumberRequest request, StreamObserver<TellmeKeyNumberResponse> responseObserver) {
            TellmeKeyNumberResponse response = TellmeKeyNumberResponse.newBuilder().setNumber(hashMap.size()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }




    }
//
//    public static void main(String[] args) {
//        int id = Integer.valueOf(args[0]);
//        String ip = args[1];
//        int port = Integer.valueOf(args[2]);
//        int knownID = Integer.valueOf(args[3]);
//        String knownIP = null;
//        int knownPort = -1;
//        if(knownID != -1){
//            knownIP = args[4];
//            knownPort = Integer.valueOf(args[5]);
//        }
//
//        ChordNodeServer chordNodeServer = new ChordNodeServer(id, ip, port);
//
//        try {
//            if(knownID == -1) {
//                chordNodeServer.start(-1, null, -1);
//
//            } else{
//                chordNodeServer.start(knownID, knownIP, knownPort);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.log(Level.WARNING, "start server failed");
//        }
//    }



}