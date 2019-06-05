import common.ConfigGenerator;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordManagerClient;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.*;

public class FindSuccessorTest extends TestCase {
    private ChordManagerClient managerClient;
    private int ringSizeExp = 13;

    public void setUp() {
        // create client
        managerClient = new ChordManagerClient("localhost", 9527);
    }

    public void tearDown() {
        // close client
        managerClient.close();
    }

    @Test
    public void testSuccessor() {
        Random random = new Random();
        List<Identifier> nodeList = ConfigGenerator.generateRingList();
        long startTime = System.currentTimeMillis();
        for (int i = 0;i < 100;i++) {

            System.out.println("Iteration " + i);

            int id = random.nextInt(1 << this.ringSizeExp);

            Identifier identifier = managerClient.findSuccessor(id);

            int index = Collections.binarySearch(nodeList,
                    Identifier.newBuilder().setID(id).build(), Comparator.comparing(Identifier::getID));

            if (index < 0) index = -(index + 1);

            if (index == 50) index = 0;

            Identifier destNode = nodeList.get(index);

            assert(destNode.getID() == identifier.getID());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total Time: " + (endTime - startTime) / (1000.0) + "s");
    }

    @Test
    public void testFingerTableContent() {

    }

}
