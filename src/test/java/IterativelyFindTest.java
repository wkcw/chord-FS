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
        for (int i = 0;i < 100;i++) {

            System.out.println("Iteration " + i);

            int id = random.nextInt(1 << this.ringSizeExp);

            FindSuccessorIterativelyResponse response = nodeClient.findSuccessorIteratively(id);

            while (response.getIsCompleted()) {
                assertTrue(response.getIdentifier().getID() != -1);

                nodeClient = new ChordNodeClient(response.getIdentifier().getIP(), response.getIdentifier().getPort());
                response = nodeClient.findSuccessorIteratively(id);
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) / 1000.0 + "s");


        startTime = System.currentTimeMillis();
        for (int i = 0;i < 100;i++) {

            System.out.println("Iteration " + i);

            int id = random.nextInt(1 << this.ringSizeExp);

            Identifier identifier = nodeClient.findSuccessor(id);
        }

        endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) / 1000.0 + "s");
    }


}
