import common.ConfigGenerator;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ScalabilityTest extends TestCase {
    ChordNodeClient nodeClient;
    int ringSize = 13;

    public void setUp() {
        // create client
        nodeClient = new ChordNodeClient("localhost", 9700);
    }

    public void tearDown() {
        // close client
        nodeClient.close();
    }

    @Test
    public void testScalability() {
        Random random = new Random(System.nanoTime());

        long startTime = System.currentTimeMillis();

        for (int i = 0;i < 100;i++) {

            System.out.println("Iteration " + i);

            int id = random.nextInt(1 << this.ringSize);

            Identifier identifier = nodeClient.findSuccessor(id);

            assertTrue(identifier.getID() !=  -1);
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) * 1.0 / 1000.0 + "s");
    }
}
