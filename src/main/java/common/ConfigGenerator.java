package common;

import net.grpc.chord.Identifier;
import java.io.*;
import java.util.*;

public class ConfigGenerator {
    public static List<Identifier> generateRingList() {
        Properties prop = new Properties();
        InputStream input = null;
        List<Identifier> ret = new ArrayList<>();

        try {
            input = new FileInputStream("src/main/resources/Config.properties");
            prop.load(input);

            int ringSize = Integer.valueOf(prop.get("ringSize").toString());

            Hasher hasher = new Hasher(1 << ringSize);

            for (int i = 0;i < 5;i++) {
                String ip = prop.get("ip" + i).toString();
                int port = Integer.valueOf(prop.get("port" + i).toString());

                for (int j = 0;j < 10;j++) {
                    ret.add(Identifier.newBuilder().setID(hasher.hash(ip + (port + j))).setIP(ip).setPort(port + j).build());
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ret.sort(Comparator.comparing(Identifier::getID));

        return ret;
    }

    public static void generateProperties(String fileName, String content){
        try {
            System.out.println("generating file");
            File writeName = new File("./"+fileName+".properties");
            writeName.createNewFile();
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(content);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        System.out.println(ConfigGenerator.generateRingList());
    }
}
