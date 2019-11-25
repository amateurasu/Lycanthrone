package vn.elite.snatcher.examples;

import org.jsoup.Connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.jsoup.helper.HttpConnection.connect;

public class DownloadMedia {
    public static void main(String[] args) throws URISyntaxException {
        String url = "";
        System.out.format("Fetching %s...", url);
//        HttpConnection.defaultProxy("10.61.60.2", 8888);
        try {
            Connection.Response response = (Connection.Response) connect(url).timeout(10000).get();
            // Document doc = (Document) response.parse();
            byte[] stream = response.bodyAsBytes();

            ImageInfo.analyze(stream).print();
            String filename = Paths.get(new URI(url).getPath()).getFileName().toString();
            save(stream, new File(filename), false);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Done!");
    }

    public static void save(byte[] content, File file, boolean append) throws IOException {
        try (OutputStream stream = new FileOutputStream(file, append)) {
            stream.write(content);
        }
    }
}
