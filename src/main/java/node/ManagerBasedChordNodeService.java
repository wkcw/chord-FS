package node;

import io.grpc.StatusRuntimeException;
import manager.ChordManagerClient;
import net.grpc.chord.Identifier;

import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagerBasedChordNodeService extends ChordNodeService {

    private static final Logger logger = Logger.getLogger(ManagerBasedChordNodeService.class.getName());

    private int selfID;
    private String selfIP;
    private int selfPort;
    private String mgrIP;
    private int mgrPort;

    public ManagerBasedChordNodeService(int selfID, String selfIP, int selfPort, int ringSizeExp, String mgrIP, int mgrPort) {
        super(selfID, selfIP, selfPort, ringSizeExp);
        this.selfID = selfID;
        this.selfIP = selfIP;
        this.selfPort = selfPort;
        this.mgrIP = mgrIP;
        this.mgrPort = mgrPort;
    }

    public void managerJoin() {
        this.predecessor = null;
        logger.info("Creating client for join at " + mgrIP + " " + mgrPort);
        ChordManagerClient chordManagerClient = new ChordManagerClient(mgrIP, mgrPort);

        Identifier successorIdentifier = null;
        try {
            successorIdentifier = chordManagerClient.join(this.selfID, this.selfIP, this.selfPort);
        } catch(StatusRuntimeException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "join failed");
        }

        this.fingerTable[0] = successorIdentifier;

        maintainFirstReplica(successorsList[0], fingerTable[0]);
        System.out.println("setting successor list entry " + successorIdentifier.getID());
        successorsList[0] = successorIdentifier;

        chordManagerClient.close();
    }

    public void managerStart() {
        create();
        this.next = 0;
        Timer timer = new Timer();

        this.stabilizeTask = new StabilizeTask();
        timer.schedule(stabilizeTask, 1000, 500);

        this.checkPredecessorTask = new CheckPredecessorTask();
        timer.schedule(checkPredecessorTask, 1000, 500);

        this.fixFingersTask = new FixFingersTask();
        timer.schedule(fixFingersTask, 1000, 500);

        this.inspectRedundancyTask = new InspectRedundancyTask();
        timer.schedule(inspectRedundancyTask, 1000, 2000);

        this.printStatusTask = new PrintStatusTask();
        timer.schedule(printStatusTask, 1000, 500);
        // start method of super class
        managerJoin();
    }

}