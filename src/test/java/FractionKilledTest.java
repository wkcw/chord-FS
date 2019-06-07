import common.ConfigGenerator;
import common.Hasher;
import common.IdentifierWithHop;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FractionKilledTest extends TestCase {
    private ChordNodeClient nodeClient;
    private int ringSizeExp = 13;
    private Hasher hasher;

    public void setUp() {
        // create client
        hasher = new Hasher(1 << ringSizeExp);
        nodeClient = new ChordNodeClient("localhost", 9700);
    }

    public void tearDown() {
        // close client
        nodeClient.close();
    }

    @Test
    public void testFractionKillingNode() {
        List<Identifier> nodeList = ConfigGenerator.generateRingList();
        Random random = new Random(System.nanoTime());
        int fraction = 3;

        for (int j = 0; j < fraction * 5; j++) {
            int killedNodeIndex = 0;
            while (killedNodeIndex == 0) killedNodeIndex = random.nextInt(nodeList.size());
            Identifier killedNode = nodeList.get(killedNodeIndex);
            ChordNodeClient killedClient = new ChordNodeClient(killedNode.getIP(), killedNode.getPort());
            killedClient.kill();
            nodeList.remove(killedNodeIndex);
            killedClient.close();
        }
        int index = 0;

        Map<Integer, Integer> hopProb = new HashMap<>();
        while (index < Math.pow(2, ringSizeExp - 3)) {
            int nodeID = hasher.hash(String.valueOf(System.currentTimeMillis()));
            IdentifierWithHop identifierWithHop = nodeClient.findSuccessorWithHop(nodeID, 0);
            int hop = identifierWithHop.hop;
            if (!hopProb.containsKey(hop)) {
                hopProb.put(hop, 0);
            }
            hopProb.put(hop, hopProb.get(hop) + 1);
            index++;
            System.out.println("done " + index);
        }

        int meanHop = 0;
        int successCount = 0;
        int failCount = 0;

        for (int key : hopProb.keySet()) {
            if (key == -1) failCount += hopProb.get(key);
            else {
                meanHop += key * hopProb.get(key);
                successCount += hopProb.get(key);
            }
        }

        System.out.println(hopProb);
        System.out.println("Failure Count: " + failCount);
        System.out.println("Current killed fraction " + (fraction * 10) + "%, Mean Hop is " + ((meanHop * 1.0) / successCount));
    }


}
