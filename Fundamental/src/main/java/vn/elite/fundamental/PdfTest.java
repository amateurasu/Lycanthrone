package vn.elite.fundamental;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;

import java.io.File;
import java.io.IOException;

public class PdfTest {
    public static void main(String[] args) {
        File infile = new File("E:\\Documents\\[Private]\\_062-V000001208.pdf");
        File outfile = new File("E:\\Documents\\[Private]\\062-V00001208.pdf");

        try (PDDocument document = PDDocument.load(infile, "091864930")) {
            if (document.isEncrypted()) {
                DecryptionMaterial decryptionMaterial = new StandardDecryptionMaterial("091864930");
                AccessPermission ap = document.getCurrentAccessPermission();
                if (ap.isOwnerPermission()) {
                    document.setAllSecurityToBeRemoved(true);
                    document.save(outfile);
                } else {
                    System.out.println("Error: You are only allowed to decrypt a document with the owner password.");
                }
            } else {
                System.err.println("Error: Document is not encrypted.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
