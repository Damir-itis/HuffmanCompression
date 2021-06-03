package dataGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataGenerator {
    public static void main(String[] args) throws IOException {
        int size = 1000;
        for (int i = 1; i < 6; i ++ ) {
            generateDataFile(i, size);
            size*=10;
        }
        System.out.println("generated successful");
    }

    private static void generateDataFile(int indexFile, int size) throws IOException {
        File file = new File("data/input/" + indexFile + "dataset "+ size + ".txt");
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i = 0; i < size; i++) {
            int charAscii = rnd(97, 122);
            bufferedWriter.write(charAscii);
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static int rnd(int min, int max)
    {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }

}
