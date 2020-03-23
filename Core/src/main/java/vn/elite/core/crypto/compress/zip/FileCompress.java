package vn.elite.core.crypto.compress.zip;

import lombok.val;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileCompress {
    public static void uncompress(String fileZip) throws IOException {
        val buffer = new byte[1024];
        try (val zis = new ZipInputStream(new FileInputStream(fileZip))) {
            var zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                val newFile = new File("unzipTest/" + fileName);
                try (val fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    public static void compress(String out, String... inputs) throws IOException {
        try (
            val fos = new FileOutputStream(out);
            val zipFile = new ZipOutputStream(fos)
        ) {
            for (String srcFile : inputs) {
                addToZip(new File(srcFile), zipFile);
            }
        }
    }

    public static void addToZip(File fileToZip, ZipOutputStream zipOut) throws IOException {
        try (val fis = new FileInputStream(fileToZip)) {
            val zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            val bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }
}
