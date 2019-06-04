import common.ConfigGenerator;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.*;

public class SuccessorListTest extends TestCase {
    private ChordNodeClient nodeClient;
    private int ringSizeExp = 13;
    Comparator<Identifier> comparator = Comparator.comparing(Identifier::getID);

    public void setUp() {
        // create client
//        nodeClient = new ChordNodeClient("localhost", 9700);
    }

    public void tearDown() {
        // close client
//        nodeClient.close();
    }

    private List<Identifier> generateSuccessorList(List<Identifier> list, Identifier identifier, int sucLen) {
        List<Identifier> ret = new ArrayList<>();

        int index = Collections.binarySearch(list, identifier, comparator);

        assertTrue(index >= 0);

        for (int i = 0;i < sucLen;i++) {
            ret.add(list.get((index + 1 + i) % list.size()));
        }

        return ret;
    }

    @Test
    public void testSuccessorList() {
        ChordNodeClient client = new ChordNodeClient("localhost", 9700);
        client.kill();

        client.close();

//        Random random = new Random();
//        List<Identifier> nodeList = ConfigGenerator.generateRingList();
//
//        int index = random.nextInt(nodeList.size());
//        Identifier node = nodeList.get(index);
//
//        ChordNodeClient client = new ChordNodeClient(node.getIP(), node.getPort());
//
//        Identifier predecessor = client.inquirePredecessor();
//
//        client.close();
//
//        nodeList.remove(node);
//        System.out.println(node.getID());
//        System.out.println(node.getIP());
//        client.kill();
//
//        try {
//            Thread.sleep(13000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        List<Identifier> sucList = generateSuccessorList(nodeList, predecessor, 3);
//
//        client = new ChordNodeClient(predecessor.getIP(), predecessor.getPort());
//
//        List<Identifier> realSucList = client.inquireSuccessorsList();
//
//        assertEquals(sucList, realSucList);
    }

}
