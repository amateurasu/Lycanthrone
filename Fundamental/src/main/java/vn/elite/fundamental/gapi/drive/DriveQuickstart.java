package vn.elite.fundamental.gapi.drive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DriveQuickstart {
    private static final String APPLICATION_NAME = "Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static void main(String... args) throws IOException, GeneralSecurityException {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        val credential = DriveUtils.getCredentials(httpTransport);
        val service = new Drive
            .Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();

        val result = service.files().list().setPageSize(150).setFields("nextPageToken, files(id, name)").execute();
        val files = result.getFiles();
        if (files == null || files.isEmpty()) {
            log.error("No files found.");
            return;
        }

        AtomicInteger count = new AtomicInteger();
        files.forEach(file -> {
            log.info("{} ({})", String.format("%03d %80s", count.getAndIncrement(), file.getName()), file.getId());
        });
    }
}
