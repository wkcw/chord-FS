package client;

import manager.ChordManagerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ManagerBasedClient {

    private String IP = "localhost";
    private int port = 9527;

    public boolean put(String key, String value){
        ChordManagerClient managerClient = new ChordManagerClient(IP, port);
        managerClient.put(key, value);
        managerClient.close();
        return true;
    }

    public String get(String key) {
        ChordManagerClient managerClient = new ChordManagerClient(IP, port);
        String value = managerClient.get(key);
        managerClient.close();
        return value;
    }


    public static void main(String[] args){
        ManagerBasedClient client = new ManagerBasedClient();
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