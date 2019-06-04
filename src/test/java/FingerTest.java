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
