package vn.elite.fundamental.gapi.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.val;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class DriveUtils {
    private static final String APPLICATION_NAME = "Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String CLIENT_SECRET_FILE_NAME = "client_secret.json";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    // Global instance of the HTTP transport.
    private static HttpTransport HTTP_TRANSPORT;

    private static Drive _driveService;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static final String CLIENT_SECRET = "client_secret.json";

    public static Credential getCredentials() throws IOException {
        return getCredentials(HTTP_TRANSPORT);
    }

    public static Credential getCredentials(final HttpTransport httpTransport) throws IOException {
        val clientSecret = new File(DriveQuickstart.class.getResource(CLIENT_SECRET).getPath());
        val folder = new File(clientSecret.getParent());

        if (!clientSecret.exists()) {
            throw new FileNotFoundException(String.format("Cannot get %s at the folder %s!", CLIENT_SECRET, folder));
        }

        val in = new FileInputStream(clientSecret);
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        val flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(folder))
            .setAccessType("offline")
            .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static Drive getDriveService() throws IOException {
        if (_driveService != null) {
            return _driveService;
        }
        Credential credential = getCredentials();

        _driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
        return _driveService;
    }

}
