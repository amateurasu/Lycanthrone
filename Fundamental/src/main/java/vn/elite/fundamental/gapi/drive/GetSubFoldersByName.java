package vn.elite.fundamental.gapi.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetSubFoldersByName {
    public static List<File> getGoogleSubFolderByName(String googleFolderIdParent, String subFolderName) throws IOException {
        Drive driveService = DriveUtils.getDriveService();

        String pageToken = null;
        val list = new ArrayList<File>();

        String query;
        if (googleFolderIdParent == null) {
            query = String.format(" name='%s' and mimeType='application/vnd.google-apps.folder' and 'root' in parents", subFolderName);
        } else {
            query = String.format(" name='%s' and mimeType='application/vnd.google-apps.folder' and '%s' in parents",
                    subFolderName, googleFolderIdParent);
        }

        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, createdTime)")
                    .setPageToken(pageToken).execute();
            list.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return list;
    }

    // com.google.api.services.drive.model.File
    public static List<File> getGoogleRootFoldersByName(String subFolderName) throws IOException {
        return getGoogleSubFolderByName(null, subFolderName);
    }

    public static void main(String[] args) throws IOException {
        val rootGoogleFolders = getGoogleRootFoldersByName("TEST");
        for (File folder : rootGoogleFolders) {
            System.out.printf("Folder ID: %s --- Name: %s%n", folder.getId(), folder.getName());
        }
    }

}
