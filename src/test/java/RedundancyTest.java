import common.Hasher;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RedundancyTest extends TestCase {
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
        nodeClient = new ChordNodeClient("100.82.144.170", 9700);
        hasher = new Hasher(ringSize);

        System.out.println("Preperation done!");

    }

    public void tearDown() {
        // close client
        nodeClient.close();
        System.out.println("All test done!");
    }

    @Test
    public void testRedundancy() {
        Hasher hasher = new Hasher(2);

        int index = 0;

        long startTime = System.currentTimeMillis();



        while (index < 10 * Math.pow(2, ringSizeExp)) {
            String value = String.valueOf(System.currentTimeMillis());
            String key = hasher.sha1Digest(value);
            int nodeID = hasher.hash(key);
            Identifier identifier = nodeClient.findSuccessor(nodeID);
            ChordNodeClient putClient = new ChordNodeClient(identifier.getIP(), identifier.getPort());
            putClient.put(key, value);
            index++;
            System.out.println("done " + index);
        }



        long endTime = System.currentTimeMillis();

        long elapsed = endTime - startTime;


        System.out.println("Time Elapsed: " + elapsed);
    }

}
