package client;

import common.Hasher;
import common.IdentifierWithHop;
import common.JsonUtil;
import net.grpc.chord.Identifier;
import node.ChordNodeClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

public class Client {

    private String[] addressList = {"localhost:9700"};//, "127.0.0.1:9704", "127.0.0.1:9708", "127.0.0.1:9712", "127.0.0.1:9716", "127.0.0.1:9720", "127.0.0.1:9724", "127.0.0.1:9728"};
    private int ringSizeExp=5;
    private Hasher hasher = new Hasher(1 << ringSizeExp);

    private String chooseRandomAddress(){
        Random rand = new Random();
        int n = rand.nextInt(this.addressList.length);
        return addressList[n];
    }

    private ChordNodeClient newClientToRandomAddress(){
        String address = chooseRandomAddress();
        String ip = address.split(":")[0];
        int port = Integer.valueOf(address.split(":")[1]);
        ChordNodeClient knownClient = new ChordNodeClient(ip, port);
        return knownClient;
    }

    private Identifier findSuccessor(String key) {
        int keyID = hasher.hash(key);
        Identifier successor;
        while(true){
            ChordNodeClient knownClient = newClientToRandomAddress();
            successor = knownClient.findSuccessor(keyID);
            if(successor != null){
                knownClient.close();
                break;
            }else{
                knownClient.close();
            }
        }
        return successor;
    }

    private IdentifierWithHop findSuccessorWithHop(String key) {
        int keyID = hasher.hash(key);
        IdentifierWithHop successorWithHop;
        while(true){
            ChordNodeClient knownClient = newClientToRandomAddress();
            successorWithHop = knownClient.findSuccessorWithHop(keyID, 0);
            if(successorWithHop.identifier.getID() != -1){
                knownClient.close();
                break;
            }else{
                knownClient.close();
            }
        }
        return successorWithHop;
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
