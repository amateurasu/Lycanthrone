package vn.elite.core.crypto.compress.zip;

import lombok.val;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipDirectory {
    public static void main(String[] args) throws IOException {
        val sourceFile = "D:\\Projects\\Web\\Tools\\Fnatchs";
        try (
            val fos = new FileOutputStream("dirCompressed.zip");
            val zipOut = new ZipOutputStream(fos)
        ) {
            File fileToZip = new File(sourceFile);

            zipFile(fileToZip, fileToZip.getName(), zipOut);
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) { return; }

        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            if (children == null) {
                return;
            }
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
