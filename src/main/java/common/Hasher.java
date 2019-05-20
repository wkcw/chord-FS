package common;
import org.apache.commons.codec.digest.DigestUtils;

public class Hasher {

    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    public int hash(int key){
        return key % total;
    }

    public int hash(String key){
        return key.hashCode() % total;
    }

    private String sha1Digest(String key){
        return DigestUtils.sha1Hex(key);
    }

}
