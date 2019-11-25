package vn.elite.fundamental.xtel.thread.sync;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class Input {

    public int[] load(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        if (Files.exists(path)) {
            String contents = new String(Files.readAllBytes(path));
            String[] split = contents.split("\\D+");
            int length = split.length;
            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = Integer.parseInt(split[i]);
            }
            return array;
        }
        return null;
    }
}
