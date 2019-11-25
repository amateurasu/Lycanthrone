package vn.elite.snatcher.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Downloader {
    public static void download(String url, String fileName) {
        try (ReadableByteChannel in = Channels.newChannel(new URL(url).openStream());
             FileOutputStream out = new FileOutputStream(fileName)) {
            FileChannel fileChannel = out.getChannel();
            fileChannel.transferFrom(in, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String url = "http://localhost:8080/s/download";
        download(url, "D:/duclm/a.pdf");
        long time = System.currentTimeMillis() - start;
        System.out.println(time);
    }
}
