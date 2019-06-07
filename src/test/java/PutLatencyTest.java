import common.ConfigGenerator;
import common.Hasher;
import common.IdentifierWithHop;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PutLatencyTest extends TestCase {
    public Hasher hasher;
    private int ringSizeExp = 13;


    public void setUp() {
        hasher = new Hasher(1 << ringSizeExp);
        System.out.println("Preperation done!");

    }

    public void tearDown() {
        // close client
        System.out.println("All test done!");
    }

    @Test
    public void testPutLatency() {

        long startTime = System.currentTimeMillis();

        int index = 0;
        while (index < 100) {
            String key = String.valueOf(System.currentTimeMillis());
            String value = String.valueOf(index);
            int nodeID = hasher.hash(key);
            ChordNodeClient nodeClient = new ChordNodeClient("34.209.184.136", 9700);
            Identifier identifier = nodeClient.findSuccessor(nodeID);
            nodeClient.close();
            ChordNodeClient putClient = new ChordNodeClient(identifier.getIP(), identifier.getPort());
            if (putClient.put(key, value)) {
                System.out.println("Put success");
            }
            else {
                System.err.println("Put failed");
            }
            index++;
            putClient.close();
            System.out.println("done " + index);
        }

        long endTime = System.currentTimeMillis();

        long elapsed = endTime - startTime;

        System.out.println("Time Elapsed: " + elapsed);
    }

}
