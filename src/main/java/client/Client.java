package client;


import common.JsonUtil;
import node.ChordNodeClient;

public class Client {

    private String IP = "127.0.0.1";
    private int port = 9700

    private boolean put(String key, String value){
        int keyID = JsonUtil.serilizable()
        ChordNodeClient knownClient = new ChordNodeClient(IP, port);
        while(true){
            knownClient.findSuccessor()
        }
    }

    public static void main(String[] args){
        while()
    }

}
