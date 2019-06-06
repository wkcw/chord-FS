import common.ConfigGenerator;
import common.Hasher;
import junit.framework.TestCase;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RedundancyTest extends TestCase {
    private ChordNodeClient nodeClient;
    private int ringSizeExp = 13;


    public void setUp() {

        // create client
        nodeClient = new ChordNodeClient("localhost", 9700);

        System.out.println("Preperation done!");

    }

    public void tearDown() {
        // close client
        nodeClient.close();
        System.out.println("All test done!");
    }

    @Test
    public void testRedundancy() {
        Hasher hasher = new Hasher(1<<ringSizeExp);

        System.out.println("0 to 29 start done");

        // read config
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("./start9700-30.properties");
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] ipArray = new String[50];
        int[] portArray = new int[50];
        for(int i=0; i<30; i++){
            ipArray[i] = prop.getProperty("ip"+i);
            portArray[i] = Integer.valueOf(prop.getProperty("port"+i));
        }

        double attemptNum = 1 << (ringSizeExp-3);
        int dataNum = 0;

        for(int index=0; index < attemptNum; index++) {
            String value = String.valueOf(index);
            String key = hasher.sha1Digest(value);
            int nodeID = hasher.hash(key);
            System.out.println("Key ID: "+nodeID);
            Identifier identifier = nodeClient.findSuccessor(nodeID);
            ChordNodeClient putClient = new ChordNodeClient(identifier.getIP(), identifier.getPort());
            putClient.put(key, value);
            putClient.close();
            dataNum++;
            System.out.println("done " + index);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }




        int primaryKeyCnt = 0;
        int totalKeyNumOn30Nodes = 0;
        for(int i=0; i<30; i++){
            ChordNodeClient nodeClient = new ChordNodeClient(ipArray[i], portArray[i]);
            int replicaKeyNumber = nodeClient.tellmeReplicaKeyNumber();
            int primaryKeyNumber = nodeClient.tellmeKeyNumber();
            System.out.println(portArray[i]+" has primary Key "+primaryKeyNumber+" and ReplicaKey "+replicaKeyNumber);
            nodeClient.close();
            totalKeyNumOn30Nodes += replicaKeyNumber;
            totalKeyNumOn30Nodes += primaryKeyNumber;
            primaryKeyCnt += primaryKeyNumber;
        }
        System.out.println("dataNum: "+dataNum + " " + "primaryKeyCnt: " + primaryKeyCnt);
        System.out.println("Ratio on 30 nodes: " + totalKeyNumOn30Nodes * 1.0 / dataNum);



    }

    @Test
    public void testRedundancyPhase2() {
        Hasher hasher = new Hasher(1<<ringSizeExp);

        System.out.println("0 to 29 start done");

        // read config
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("./start9700-30.properties");
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] ipArray = new String[50];
        int[] portArray = new int[50];
        for(int i=0; i<30; i++){
            ipArray[i] = prop.getProperty("ip"+i);
            portArray[i] = Integer.valueOf(prop.getProperty("port"+i));
        }

        // read config
        prop = new Properties();
        input = null;

        try {

            input = new FileInputStream("./start9800-20.properties");
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for(int i=30; i<50; i++){
            ipArray[i] = prop.getProperty("ip"+(i-30));
            portArray[i] = Integer.valueOf(prop.getProperty("port"+(i-30)));
        }
//
//
        Set<String> dataSet = new HashSet<>();
        int dataNum = 0;
        double attemptNum = 1 << (ringSizeExp-3);
        for(int index=0; index < attemptNum; index++) {
            String value = String.valueOf(index);
            String key = hasher.sha1Digest(value);
            int nodeID = hasher.hash(key);
            System.out.println("Key ID: "+nodeID);
            dataNum ++;
        }


        int primaryKeyCnt = 0;
        int totalKeyNumOn50Nodes = 0;
        for(int i=0; i<50; i++){
            ChordNodeClient nodeClient = new ChordNodeClient(ipArray[i], portArray[i]);
            int replicaKeyNumber = nodeClient.tellmeReplicaKeyNumber();
            int primaryKeyNumber = nodeClient.tellmeKeyNumber();
            System.out.println(portArray[i]+" has primary Key "+primaryKeyNumber+" and ReplicaKey "+replicaKeyNumber);
            nodeClient.close();
            totalKeyNumOn50Nodes += replicaKeyNumber;
            totalKeyNumOn50Nodes += primaryKeyNumber;
            primaryKeyCnt += primaryKeyNumber;
        }

        System.out.println("dataNum: "+dataNum + " " + "primaryKeyCnt: " + primaryKeyCnt);
        System.out.println("Ratio on 50 nodes: " + totalKeyNumOn50Nodes * 1.0 / dataNum);


    }

}
