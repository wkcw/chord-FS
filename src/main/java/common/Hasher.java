package common;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;

public class Hasher {

    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Hasher(int total){
        this.total = total;
    }

    public int hash(String key){
        return Math.abs(sha1Digest(key).hashCode()) % total;
    }

    public String sha1Digest(String key){
        return DigestUtils.sha1Hex(key);
    }



    public static void main (String[] args) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("ls");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
