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
//        List<Identifier> nodeList = ConfigGenerator.generateRingList();
//
//        ChordNodeClient client = new ChordNodeClient("localhost", 9700);
//        client.kill();

        Random random = new Random();
        List<Identifier> nodeList = ConfigGenerator.generateRingList();

        int index = random.nextInt(nodeList.size());
        Identifier node = nodeList.get(index);

        ChordNodeClient client = new ChordNodeClient(node.getIP(), node.getPort());

        Identifier predecessor = client.inquirePredecessor();

        index = Collections.binarySearch(nodeList, node, comparator);
        assertTrue(index >= 0);

        nodeList.remove(index);
        System.out.println(node.getID());
        System.out.println(node.getIP());
        client.kill();

        client.close();

        try {
            Thread.sleep(13000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Identifier> sucList = generateSuccessorList(nodeList, predecessor, 3);

        client = new ChordNodeClient(predecessor.getIP(), predecessor.getPort());

        List<Identifier> realSucList = client.inquireSuccessorsList();

        client.close();

        assertEquals(sucList, realSucList);
    }

    @Test
    public void testRSuccessorsKilled() {
        Random random = new Random();
        List<Identifier> nodeList = ConfigGenerator.generateRingList();

        int index = random.nextInt(nodeList.size());
        Identifier node = nodeList.get(index);

        ChordNodeClient client = new ChordNodeClient(node.getIP(), node.getPort());

        List<Identifier> successorList = new ArrayList<>(client.inquireSuccessorsList());

        successorList.add(0, node);

        String key = "luxuhui";
        String value = "123";
        boolean res = client.put(key, value);

        client.close();

        if(res){
            System.out.printf("Put key:%s, value:%s Succeeded: \n", key, value);
        }else{
            System.out.println("Put Failed");
        }

        for (int i = 0;i < successorList.size() - 1;i++) {
            client = new ChordNodeClient(successorList.get(i).getIP(), successorList.get(i).getPort());
            client.kill();
            client.close();
        }

        Identifier lastElement = successorList.get(successorList.size() - 1);

        client = new ChordNodeClient(lastElement.getIP(), lastElement.getPort());

        String ret = client.get(key);

        assertEquals(ret, value);
    }

}
