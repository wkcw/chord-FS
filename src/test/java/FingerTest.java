import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;
import node.ChordNodeClient;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FingerTest extends TestCase {
    private ChordNodeClient nodeClient;

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

        // create client
        nodeClient = new ChordNodeClient("localhost", 9700);


    }

    public void tearDown() {
        // close client
        nodeClient.close();
    }

    @Test
    public void testFingerTableLength() {

    }

    @Test
    public void testFingerTableContent() {

    }

}
