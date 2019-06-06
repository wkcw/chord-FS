import common.ConfigGenerator;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.*;

public class FindSuccessorTest extends TestCase {
    private ChordNodeClient nodeClient;
    private int ringSizeExp = 13;

    public void setUp() {
        // create client
        nodeClient = new ChordNodeClient("localhost", 10000);
    }

    public void tearDown() {
        // close client
        nodeClient.close();
    }

    @Test
    public void testSuccessor() {
        Random random = new Random();
        List<Identifier> nodeList = ConfigGenerator.generateRingList();
        for(Identifier iden : nodeList){
            System.out.println(iden.getID());
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0;i < 100;i++) {

            System.out.println("Iteration " + i);

            int id = random.nextInt(1 << this.ringSizeExp);

            Identifier identifier = nodeClient.findSuccessor(id);

            int index = Collections.binarySearch(nodeList,
                    Identifier.newBuilder().setID(id).build(), Comparator.comparing(Identifier::getID));

            if (index < 0) index = -(index + 1);

            if (index == 50) index = 0;

            Identifier destNode = nodeList.get(index);

            assert(destNode.getID() == identifier.getID());
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) / 1000.0 + "s");
    }

    @Test
    public void testSuccessorAfterKilling() {
        Random random = new Random();
        int index = 0;

        List<Identifier> nodeList = ConfigGenerator.generateRingList();

        while (index == 0) index = random.nextInt(nodeList.size());

        ChordNodeClient client = new ChordNodeClient(nodeList.get(index).getIP(), nodeList.get(index).getPort());
        client.kill();
        client.close();

        nodeList.remove(index);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0;i < 100;i++) {

            System.out.println("Iteration " + i);

            int id = random.nextInt(1 << this.ringSizeExp);

            Identifier identifier = nodeClient.findSuccessor(id);

            index = Collections.binarySearch(nodeList,
                    Identifier.newBuilder().setID(id).build(), Comparator.comparing(Identifier::getID));

            if (index < 0) index = -(index + 1);

            if (index == 49) index = 0;

            Identifier destNode = nodeList.get(index);

            assert(destNode.getID() == identifier.getID());
        }
    }

}
