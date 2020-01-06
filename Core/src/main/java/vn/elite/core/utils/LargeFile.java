package vn.elite.core.utils;

import lombok.AllArgsConstructor;
import lombok.val;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.function.Consumer;

public class LargeFile {
    public static void main(String[] args) throws IOException {
        val start = System.currentTimeMillis();
        String file = "D:\\Software\\HaskellPlatform-8.6.5-core-x86_64-setup.exe";
        // readLine(file, System.out::println);
        try (val fos = new FileOutputStream("D:\\Software\\HaskellPlatform.exe")) {
            readBytes(file, bytes -> {
                try {
                    // val read = new ByteArrayInputStream(bytes);
                    // save("file.json", read);
                    fos.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println(System.currentTimeMillis() - start);
        }
    }

    public static void readLine(String file, Consumer<? super String> consumer) throws IOException {
        try (val fis = new FileInputStream(file)) {
            readLine0(fis, consumer);
        }
    }

    public static void readLine(File file, Consumer<? super String> consumer) throws IOException {
        try (val fis = new FileInputStream(file)) {
            readLine0(fis, consumer);
        }
    }

    public static void readBytes(String file, Consumer<? super byte[]> consumer) throws IOException {
        try (val fis = new FileInputStream(file)) {
            readBytes0(fis, consumer);
        }
    }

    public static void readBytes(File file, Consumer<? super byte[]> consumer) throws IOException {
        try (val fis = new FileInputStream(file)) {
            readBytes0(fis, consumer);
        }
    }

    public static void save(String fileName, String content) throws IOException {
        try (
            RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
            FileChannel channel = stream.getChannel()
        ) {
            byte[] strBytes = content.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
            buffer.put(strBytes);
            buffer.flip();
            channel.write(buffer);
        }
    }

    public static void save(String fileName, ByteArrayInputStream content) throws IOException {
        try (
            RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
            FileChannel channel = stream.getChannel()
        ) {
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = content.read(buffer, read, 1024)) != -1) {
                System.out.println(new String(buffer, StandardCharsets.UTF_8));
                ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length - 1);
                byteBuffer.put(buffer);
                byteBuffer.flip();
                channel.write(byteBuffer);
            }
        }
        // verify
        // RandomAccessFile reader = new RandomAccessFile(fileName, "r");
        // assertEquals(value, reader.readLine());
        // reader.close();
    }

    private static void readLine0(FileInputStream fis, Consumer<? super String> action) {
        try (val scanner = new Scanner(fis, "UTF-8")) {
            new LineIterable(scanner).forEach(action);
        }
    }

    static void readBytes0(FileInputStream fis, Consumer<? super byte[]> action) throws IOException {
        val buffer = new byte[1024];
        int read;
        while ((read = fis.read(buffer)) != -1) {
            if (read == 1024) {
                action.accept(buffer);
            } else {
                val temp = new byte[read];
                System.arraycopy(buffer, 0, temp, 0, read);
                action.accept(temp);
            }
        }
    }

    @AllArgsConstructor
    private static class LineIterable implements Iterable<String> {
        private Scanner scanner;

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                @Override
                public boolean hasNext() {
                    return scanner.hasNextLine();
                }

                @Override
                public String next() {
                    return scanner.nextLine();
                }
            };
        }
    }
}
