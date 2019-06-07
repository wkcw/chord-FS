import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import common.Hasher;
import junit.framework.TestCase;
import manager.ChordManagerClient;
import manager.ChordManagerServer;
import net.grpc.chord.ChordNodeProto;
import net.grpc.chord.FindResponse;
import net.grpc.chord.FindSuccessorResponse;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilesysManagerTest extends TestCase {
    public Hasher hasher;
    private int ringSizeExp = 13;


    public void setUp() {
        hasher = new Hasher(1 << ringSizeExp);
        System.out.println("Preperation done!");

    }

    public void tearDown() {
        // close client
        System.out.println("All test done!");
    }


    @Test
    public void testFilesysManager() {

        long startTime = System.currentTimeMillis();
        List<ListenableFuture<FindSuccessorResponse>> futureList = new ArrayList<>();
        ChordManagerClient managerClient = new ChordManagerClient("localhost", 9527);
        ChordNodeClient chordNodeClient = new ChordNodeClient("localhost", 10000);


        String movieContent = "this is a spiderman2 movie";
        String absPath = "movie/spiderman2.mp4";

        String value = movieContent;
        String key = hasher.sha1Digest(value);
        int nodeID = hasher.hash(key);
        Identifier identifier = chordNodeClient.findSuccessor(nodeID);
        ChordNodeClient putClient = new ChordNodeClient(identifier.getIP(), identifier.getPort());
        if (putClient.put(key, value)) {
            putClient.close();
            System.out.println("Put success");
            managerClient.writeFileKey(absPath, key);
        }
        else {
            putClient.close();
            System.err.println("Put failed");
        }

        String searchingKey = managerClient.readFileKey(absPath);
        int getNodeID = hasher.hash(searchingKey);
        Identifier getIdentifier = chordNodeClient.findSuccessor(getNodeID);
        ChordNodeClient getClient = new ChordNodeClient(getIdentifier.getIP(), getIdentifier.getPort());
        String searchingContent = getClient.get(searchingKey);

        System.out.println("Searching Content: "+searchingContent);
        System.out.println("Movie Content: "+movieContent);
        assert(searchingContent.equals(movieContent));

    }

}
