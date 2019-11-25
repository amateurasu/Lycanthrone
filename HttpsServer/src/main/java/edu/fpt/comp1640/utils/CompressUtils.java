package edu.fpt.comp1640.utils;

import lombok.val;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressUtils {

    public static void zipFile(final List<String> files, final File targetZipFile) throws IOException {
        try (val fos = new FileOutputStream(targetZipFile); val zos = new ZipOutputStream(fos)) {
            for (String file : files) {
                putToZip(file, zos);
            }
        }
    }

    public static void zipFile(final String file, final File targetZipFile) throws IOException {
        try (val fos = new FileOutputStream(targetZipFile); val zos = new ZipOutputStream(fos)) {
            putToZip(file, zos);
        }
    }

    private static void putToZip(String file, ZipOutputStream zos) throws IOException {
        val buffer = new byte[1024];
        val currentFile = new File(file);
        if (currentFile.isDirectory()) {
            return;
        }

        try (val fis = new FileInputStream(currentFile)) {
            val entry = new ZipEntry(currentFile.getName());
            zos.putNextEntry(entry);
            int read;
            while ((read = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, read);
            }
            zos.closeEntry();
        }
    }
}
