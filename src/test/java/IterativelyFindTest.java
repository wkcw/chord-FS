import common.ConfigGenerator;
import junit.framework.TestCase;
import net.grpc.chord.FindSuccessorIterativelyResponse;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class IterativelyFindTest extends TestCase {
    private ChordNodeClient nodeClient;
    private int ringSizeExp = 13;

    public void setUp() {
        // create client
        nodeClient = new ChordNodeClient("34.209.184.136", 9700);
    }

    public void tearDown() {
        // close client
        nodeClient.close();
    }

    @Test
    public void testSuccessorIteratively() {
        Random random = new Random(System.nanoTime());
        long startTime = System.currentTimeMillis();

        for (int i = 0;i < 1000;i++) {

            System.out.println("Iteration " + i);

            int id = random.nextInt(1 << this.ringSizeExp);

            FindSuccessorIterativelyResponse response = nodeClient.findSuccessorIteratively(id);

            while (!response.getIsCompleted()) {
                assertTrue(response.getIdentifier().getID() != -1);
                nodeClient.close();
                nodeClient = new ChordNodeClient(response.getIdentifier().getIP(), response.getIdentifier().getPort());
                response = nodeClient.findSuccessorIteratively(id);
            }

        }

        nodeClient.close();

        long endTime = System.currentTimeMillis();

        System.out.println("Iterative Total time: " + (endTime - startTime) / 1000.0 + "s");

        nodeClient = new ChordNodeClient("34.209.184.136", 9700);

        startTime = System.currentTimeMillis();
        for (int i = 0;i < 1000;i++) {

            System.out.println("Iteration " + i);

            int id = random.nextInt(1 << this.ringSizeExp);

            Identifier identifier = nodeClient.findSuccessor(id);
        }

        endTime = System.currentTimeMillis();

        System.out.println("Recursive Total time: " + (endTime - startTime) / 1000.0 + "s");
    }


}
