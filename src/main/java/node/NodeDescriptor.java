package node;

public class NodeDescriptor {

    private int id;
    private String address;

    public NodeDescriptor(int id, String address){
        this.id = id;
        this.address = address;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }


}
