import common.Hasher;
import common.IdentifierWithHop;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class KeyDistributionTest extends TestCase {
    private ChordNodeClient nodeClient;
    public Hasher hasher;
    private int ringSizeExp = 10;
    private int ringSize = 8192;


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
        nodeClient = new ChordNodeClient("34.209.184.136", 9700);
        hasher = new Hasher(ringSize);

        System.out.println("Preperation done!");

    }

    public void tearDown() {
        // close client
        nodeClient.close();
        System.out.println("All test done!");
    }

    @Test
    public void testKeyDistribution() {
        int index = 0;

        long startTime = System.currentTimeMillis();


        Map<Integer, Integer> keyDistribution = new HashMap<>();
        while (index < 10 * Math.pow(2, ringSizeExp)) {
            String key = String.valueOf(System.currentTimeMillis());
            String value = key;
            int nodeID = hasher.hash(String.valueOf(System.currentTimeMillis()));
            Identifier identifier = nodeClient.findSuccessor(nodeID);
            if(!keyDistribution.containsKey(identifier.getID())){
                keyDistribution.put(identifier.getID(), 0);
            }
            keyDistribution.put(identifier.getID(), keyDistribution.get(identifier.getID()) + 1);
            index++;
            System.out.println("done " + index);
        }



        long endTime = System.currentTimeMillis();

        long elapsed = endTime - startTime;

        System.out.println(keyDistribution);
        System.out.println("Time Elapsed: " + elapsed);
    }

}
