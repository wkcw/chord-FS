package common;

import common.Hasher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigGenerator {

    public static void main(String[] args) {
        Hasher hasher = new Hasher(1 << Integer.valueOf(args[0]));
        List<Integer> list = new ArrayList<>();

        while(true){
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            try {
                String lineInput = br.readLine();
                String[] ops = lineInput.split(" ");

                if (ops[0].equals("exit")) break;

                if (ops.length != 2) continue;

                for (int i = 0; i < Integer.valueOf(args[0]); i++) {
                    int id = hasher.hash(ops[0] + (Integer.valueOf(ops[1]) + i));

                    list.add(id);
                    System.out.println(args[0] + " " + (Integer.valueOf(ops[1]) + i) + " " + id);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(list);

        System.out.println(list);
    }
}
