package vn.elite.fundamental.crypto.compress;

import java.io.IOException;

import static vn.elite.core.crypto.compress.zip.FileCompress.compress;

public class Compression {
    public static void main(String[] args) throws IOException {
        compress("multiCompressed.zip", "test1.txt", "test2.txt");
    }
}
