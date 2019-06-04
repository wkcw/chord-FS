import static org.junit.Assert.assertEquals;

import common.ConfigGenerator;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Properties;

public class FingerTest extends TestCase {
    private ChordNodeClient nodeClient;
    private List<Identifier> nodeList;
    private HashMap<Identifier, List<Identifier>> fingerTableMap;
    Properties properties;
    public void setUp() throws FileNotFoundException {
        nodeList = ConfigGenerator.generateRingList();
        System.out.println("Node List is: " );
        for (int i = 0; i < nodeList.size(); i++) {
            System.out.print(nodeList.get(i).getID() + " ");
        }
        System.out.println("");
        properties = new Properties();
        InputStream input = new FileInputStream("src/main/resources/Config.properties");
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int ringSizeExp = Integer.valueOf(properties.getProperty("ringSize"));
        fingerTableMap = new HashMap<>();
        int index = 0;
        while (index < nodeList.size()) {
            int startPoint = nodeList.get(index).getID();
            fingerTableMap.put(nodeList.get(index), new ArrayList<>());
            // get all fingers for this node
            for (int i = 0; i < ringSizeExp; i++) {
                int nextItem = (startPoint + (1 << i)) % (1 << ringSizeExp);

                int nextPos = Collections.binarySearch(nodeList, Identifier.newBuilder().setID(nextItem).build(), Comparator.comparing(Identifier::getID));

                if (nextPos < 0) nextPos = -(nextPos + 1);

                if (nextPos == 50) nextPos = 0;

                fingerTableMap.get(nodeList.get(index)).add(nodeList.get(nextPos));
            }
            index++;
        }

    }

    public void tearDown() {
    }

    @Test
    public void testFingerTableContent() {
//        for (Identifier node : fingerTableMap.keySet()) {
//            System.out.print("Node " + node.getID() + " :");
//            List<Identifier> fingerTable = fingerTableMap.get(node);
//            for (Identifier fingers : fingerTable) {
//                System.out.print(fingers.getID() + " ");
//            }
//            System.out.println(" ");
//        }


        // create client
        for (Identifier node : nodeList) {
            nodeClient = new ChordNodeClient(node.getIP(), node.getPort());
            String[] fingerTable = nodeClient.tellMeFingerTable().split(" ");
            //System.out.print("Node " + node.getID() + " :");

            List<Identifier> realFingerTable = fingerTableMap.get(node);

            assertEquals(realFingerTable.size(), fingerTable.length);

            for (int i = 0; i < realFingerTable.size(); i++) {
                assertEquals(String.valueOf(realFingerTable.get(i).getID()), fingerTable[i]);
            }
            nodeClient.close();
        }
    }

}
