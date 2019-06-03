import common.Hasher;
import common.IdentifierWithHop;
import junit.framework.TestCase;
import node.ChordNodeClient;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class HopTest extends TestCase {
    private ChordNodeClient nodeClient;
    public Hasher hasher;
    private int ringSizeExp = 10;
    private int ringSize = 1024;
    public void setUp() {
        // read config
//        Properties prop = new Properties();
//        InputStream input = null;
//
//        try {
//
//            input = new FileInputStream("config.properties");
//            prop.load(input);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        hasher = new Hasher(ringSize);
        // create client
        nodeClient = new ChordNodeClient("localhost", 9700);
        hasher = new Hasher(ringSize);

    }

    public void tearDown() {
        // close client
        nodeClient.close();
    }

    @Test
    public void testHopLength() {
        // put a key to the ring
        // pick a random id, try to get the key, calculate how many hops needed
        // repeat 10 times, calculate the mean hop needed (path length)

        String key = String.valueOf(System.currentTimeMillis());

        int index = 0;

        long startTime = System.currentTimeMillis();


        Map<Integer, Integer> hopProb = new HashMap<>();
        while (index < 10 * Math.pow(2, ringSizeExp)) {
            int nodeID = hasher.hash(String.valueOf(System.currentTimeMillis()));
            IdentifierWithHop identifierWithHop = nodeClient.findSuccessorWithHop(nodeID, 0);
            int hop = identifierWithHop.hop;
            if(!hopProb.containsKey(hop)){
                hopProb.put(hop, 0);
            }
            hopProb.put(hop, hopProb.get(hop) + 1);
            index++;
            System.out.println("done " + index);
        }



        long endTime = System.currentTimeMillis();

        long elapsed = endTime - startTime;

        System.out.println(hopProb);
        System.out.println("Time Elapsed: " + elapsed);
    }

}
