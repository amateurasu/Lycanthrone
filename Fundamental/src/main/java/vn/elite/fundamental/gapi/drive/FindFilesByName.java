package vn.elite.fundamental.gapi.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindFilesByName {
    public static List<File> getGoogleFilesByName(String fileNameLike) throws IOException {
        Drive driveService = DriveUtils.getDriveService();

        String pageToken = null;
        val list = new ArrayList<File>();

        String query = String.format(" name contains '%s'  and mimeType != 'application/vnd.google-apps.folder' ", fileNameLike);

        do {
            // Fields will be assigned values: id, name, createdTime, mimeType
            FileList result = driveService.files().list().setQ(query).setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, createdTime, mimeType)")
                    .setPageToken(pageToken).execute();
            list.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return list;
    }

    public static void main(String[] args) throws IOException {
        List<File> rootGoogleFolders = getGoogleFilesByName("u");
        for (File folder : rootGoogleFolders) {
            System.out.printf("Mime Type: %s --- Name: %s%n", folder.getMimeType(), folder.getName());
        }

        System.out.println("Done!");
    }

}
