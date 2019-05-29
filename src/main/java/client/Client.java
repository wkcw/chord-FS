package client;


import com.sun.org.apache.xpath.internal.operations.Bool;
import common.Hasher;
import common.JsonUtil;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Client {

    private String IP = "127.0.0.1";
    private int port = 9700;
    private int ringSizeExp=5;
    private Hasher hasher = new Hasher(1 << ringSizeExp);

    private Identifier findSuccessor(String key) {
        int keyID = hasher.hash(key);
        ChordNodeClient knownClient = new ChordNodeClient(IP, port);

        Identifier successor = knownClient.findSuccessor(keyID);
        knownClient.close();
        return successor;
    }


    private boolean put(String key, String value){
        while (true){
            Identifier successor = findSuccessor(key);

            ChordNodeClient destClient = new ChordNodeClient(successor.getIP(), successor.getPort());

            if (destClient.put(key, value)) {
                destClient.close();
                break;
            }
            destClient.close();
        }

        return true;
    }

    private String get(String key) {
        Identifier successor = findSuccessor(key);
        ChordNodeClient destClient = new ChordNodeClient(successor.getIP(), successor.getPort());
        String response = destClient.get(key);
        destClient.close();
        return response;
    }


    public static void main(String[] args){
        Client client = new Client();
        while(true){
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String lineInput = br.readLine();
                String[] ops = lineInput.toString().split(" ");
                if(ops[0].equals("get") && ops.length == 2){
                    String key = ops[1];
                    String res = client.get(key);
                    System.out.println("Get result: " + res);
                }else if(ops[0].equals("put") && ops.length == 3){
                    String key = ops[1];
                    String value = ops[2];
                    boolean res = client.put(key, value);
                    if(res){
                        System.out.printf("Put key:%s, value:%s Succeeded: \n", key, value);
                    }else{
                        System.out.println("Put Failed");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
