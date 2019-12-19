package vn.elite.gapi.drive;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class CreateGoogleFile {

    public static File createGoogleFile(
        String googleFolderIdParent, String contentType, String customFileName, byte[] uploadData
    ) throws IOException {
        val uploadStreamContent = new ByteArrayContent(contentType, uploadData);
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    private static File _createGoogleFile(
        String googleFolderIdParent, String contentType, String customFileName,
        AbstractInputStreamContent uploadStreamContent
    ) throws IOException {
        File fileMetadata = new File().setName(customFileName);

        List<String> parents = Collections.singletonList(googleFolderIdParent);
        fileMetadata.setParents(parents);

        Drive driveService = DriveUtils.getDriveService();

        return driveService.files().create(fileMetadata, uploadStreamContent)
            .setFields("id, webContentLink, webViewLink, parents").execute();
    }

    // Create Google File from java.io.File
    public static File createGoogleFile(
        String googleFolderIdParent, String contentType, String customFileName, java.io.File uploadFile
    ) throws IOException {
        val uploadStreamContent = new FileContent(contentType, uploadFile);
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    // Create Google File from InputStream
    public static File createGoogleFile(
        String googleFolderIdParent, String contentType, String customFileName, InputStream inputStream
    ) throws IOException {

        val uploadStreamContent = new InputStreamContent(contentType, inputStream);
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    public static void main(String[] args) throws IOException {
        java.io.File uploadFile = new java.io.File("/home/tran/Downloads/test.txt");

        // Create Google File:

        File googleFile = createGoogleFile(null, "text/plain", "newfile.txt", uploadFile);

        System.out.println("Created Google file!");
        System.out.println("WebContentLink: " + googleFile.getWebContentLink());
        System.out.println("WebViewLink: " + googleFile.getWebViewLink());

        System.out.println("Done!");
    }
}

