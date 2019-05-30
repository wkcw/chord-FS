package node;

import net.grpc.chord.Identifier;

import java.util.Comparator;
import java.util.TreeSet;

class NodeStatus {
    private Identifier node; // Chord node
    private boolean status; // true -> online, false -> offline
    public NodeStatus(Identifier node, boolean status) {
        this.node = node;
        this.status = status;
    }

    public Identifier getNode() {
        return this.node;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}


public class ChordNodeManager {
    NodeStatus[] manager;
    private static int ringSizeExp = 5;

    public ChordNodeManager() {
        manager = new NodeStatus[(int) Math.pow(2, ringSizeExp)];

        // Set all nodes to offline mode when initializing the manager
        for (int i = 0; i < Math.pow(2, ringSizeExp); i++) {
            Identifier node = Identifier.newBuilder().setID(i).build();
            manager[i] = new NodeStatus(node, false);
        }
    }

    // when joining the network, Chord node should call ChordManager.join(id, ip, port) to notify its existence
    // And manager would return the first available successor to the joining node
    public int join(int id, String ip, int port) {
        manager[id].setStatus(true); // mark the joining node's ID as a online node
        manager[id].getNode().toBuilder().setIP(ip).setPort(port).build(); // ??? Is this necessary?

        int startPoint = id + 1; // start point for searching the successor
        // if the joining id is the last ID, starting from 0
        if (id == manager.length - 1) {
            startPoint = 0;
        }

        for (; startPoint < manager.length - 1; startPoint++) {
            if (manager[startPoint].getStatus()) {
                break;
            }
        }
        return startPoint; // return the successor ID to the joining node
    }
}
