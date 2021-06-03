
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class HuffmanCompressionBenchmarks {
    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("data/input");
        File[] data = file.listFiles();
        for (int i = 1; i < 6; i++) {
            assert data != null;
            long result = testCoding(data[i-1].getPath(), "data/coded/codedFile" + file.getName()+ i + ".txt");
            System.out.println("file " + data[i-1].getPath() + " coding time millis: " + result );
        }
        System.out.println("------------------------------------------------");
        File fileCoded = new File("data/coded");
        data = fileCoded.listFiles();
        for (int i = 1; i < 6; i++) {
            assert data != null;
            long result = testEncoding(data[i-1].getPath(), "data/encoded/encodedFile" + file.getName()+ i + ".txt");
            System.out.println("file " + data[i-1].getPath() + " encoding time millis: " + result );
        }
    }

    public static long testCoding (String filePath, String result) throws IOException {
        if (!Files.exists(Paths.get(result))) Files.createFile(Paths.get(result));
        long timeStart = System.currentTimeMillis();
        HuffmanCode.zipFile(filePath, result);
        long timeFinish = System.currentTimeMillis();
        return timeFinish - timeStart;
    }

    public static long testEncoding(String filePath, String result) throws IOException {
        if (!Files.exists(Paths.get(result))) Files.createFile(Paths.get(result));
        long timeStart = System.currentTimeMillis();
        HuffmanCode.unZipFile(filePath, result);
        long timeFinish = System.currentTimeMillis();
        return timeFinish - timeStart;
    }


}
