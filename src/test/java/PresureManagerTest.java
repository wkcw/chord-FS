import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import common.Hasher;
import junit.framework.TestCase;
import manager.ChordManagerClient;
import net.grpc.chord.FindResponse;
import net.grpc.chord.FindSuccessorResponse;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PresureManagerTest extends TestCase {
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


    class CallBackTask<T> implements FutureCallback<T> {

        public int count = 0;
        public long upperBound = 500000;
        private long startTime;

        public void setStartTime(long st){
            startTime = st;
        }
        @Override
        public void onSuccess(T result) {
            synchronized (this){
                count++;
                System.out.println("done "+count);
                if(count==upperBound){
                    System.out.println("Time Elapsed: "+(System.currentTimeMillis()-startTime));
                    System.exit(0);
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            t.printStackTrace();
        }
    }


    @Test
    public void testPressureManagerFindSuccessorLatency() {

        long startTime = System.currentTimeMillis();

        CallBackTask callBacker = new CallBackTask<FindResponse>();
        List<ListenableFuture<FindResponse>> futureList = new ArrayList<>();
        int index = 0;
        ChordManagerClient nodeClient = new ChordManagerClient("localhost", 9527);
        ChordNodeClient chordNodeClient = new ChordNodeClient("localhost", 10000);
        callBacker.setStartTime(System.currentTimeMillis());
        while (index < callBacker.upperBound+10) {
            String key = String.valueOf(System.nanoTime());
            String value = String.valueOf(index);
            int nodeID = hasher.hash(key);
            futureList.add(nodeClient.findSuccessorFuture(nodeID));
            Futures.addCallback(futureList.get(futureList.size()-1), callBacker, MoreExecutors.directExecutor());
//            nodeClient.close();
            index++;
        }

        try {
            int n = System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPressureFindSuccessorLatency() {

        long startTime = System.currentTimeMillis();

        CallBackTask callBacker = new CallBackTask<FindSuccessorResponse>();
        List<ListenableFuture<FindSuccessorResponse>> futureList = new ArrayList<>();
        int index = 0;
        ChordManagerClient nodeClient = new ChordManagerClient("localhost", 9527);
        ChordNodeClient chordNodeClient = new ChordNodeClient("localhost", 10000);
        callBacker.setStartTime(System.currentTimeMillis());
        while (index < callBacker.upperBound+10) {
            String key = String.valueOf(System.nanoTime());
            String value = String.valueOf(index);
            int nodeID = hasher.hash(key);
            futureList.add(chordNodeClient.findSuccessorFuture(nodeID));
            Futures.addCallback(futureList.get(futureList.size()-1), callBacker, MoreExecutors.directExecutor());
//            nodeClient.close();
            index++;
        }

        try {
            int n = System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
