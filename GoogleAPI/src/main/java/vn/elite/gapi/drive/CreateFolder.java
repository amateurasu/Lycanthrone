package vn.elite.gapi.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CreateFolder {

    public static File createGoogleFolder(String folderIdParent, String folderName) throws IOException {
        File fileMetadata = new File().setName(folderName).setMimeType("application/vnd.google-apps.folder");

        if (folderIdParent != null) {
            List<String> parents = Collections.singletonList(folderIdParent);
            fileMetadata.setParents(parents);
        }
        Drive driveService = DriveUtils.getDriveService();

        // Create a Folder.
        // Returns File object with id & name fields will be assigned values
        return driveService.files().create(fileMetadata).setFields("id, name").execute();
    }

    public static void main(String[] args) throws IOException {
        // Create a Root Folder
        File folder = createGoogleFolder(null, "TEST-FOLDER");
        System.out.printf("Created folder with id= %s; name= %s%nDone!%n", folder.getId(), folder.getName());
    }

}