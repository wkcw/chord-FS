package node;

import io.grpc.StatusRuntimeException;
import net.grpc.chord.Identifier;
import net.grpc.chord.JoinRequest;
import net.grpc.chord.JoinResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagerBasedChordNode extends ChordNodeService {

    private static final Logger logger = Logger.getLogger(ManagerBasedChordNode.class.getName());

    private int selfID;
    private String selfIP;
    private int selfPort;
    private String mgrIP;
    private int mgrPort;

    public ManagerBasedChordNode(int selfID, String selfIP, int selfPort, String mgrIP, int mgrPort) {
        super(selfID, selfIP, selfPort);
        this.selfID = selfID;
        this.selfIP = selfIP;
        this.selfPort = selfPort;
        this.mgrIP = mgrIP;
        this.mgrPort = mgrPort;
    }


    public void join() {
        setPredecessor(null);
        logger.info("Creating client for join");
        ChordManagerClient chordManagerClient = new ChordManagerClient(mgrIP, mgrPort);

        Identifier successorIdentifier = null;
        try {
             successorIdentifier = chordManagerClient.join(this.selfID, this.selfIP, this.selfPort);
        } catch(StatusRuntimeException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "join failed");
        }

        setFingerTableEntry(0, successorIdentifier);

        maintainFirstReplica(getSuccessorsListEntry(0), getFingerTableEntry(0));
        setSuccessorListEntry(0, successorIdentifier);

        chordManagerClient.close();
    }

}
