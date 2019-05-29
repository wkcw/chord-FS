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

    public Hasher(int total){
        this.total = total;
    }

    public int hash(String key){
        return sha1Digest(key).hashCode() % total;
    }

    private String sha1Digest(String key){
        return DigestUtils.sha1Hex(key);
    }

}
