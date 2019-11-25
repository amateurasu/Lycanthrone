package vn.elite.gapi.drive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import lombok.val;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicInteger;

public class DriveQuickstart {
    private static final String APPLICATION_NAME = "Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static void main(String... args) throws IOException, GeneralSecurityException {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        val credential = DriveUtils.getCredentials(httpTransport);
        val service = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

        val result = service.files().list().setPageSize(150).setFields("nextPageToken, files(id, name)").execute();
        val files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
            return;
        }

        System.out.println("Files:");
        AtomicInteger count = new AtomicInteger();
        files.forEach(file -> {
            System.out.printf("%03d %80s (%s)\n", count.getAndIncrement(), file.getName(), file.getId());
        });
    }
}