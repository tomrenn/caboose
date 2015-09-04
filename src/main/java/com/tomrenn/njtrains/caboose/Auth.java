package com.tomrenn.njtrains.caboose;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.storage.StorageScopes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tomrenn on 9/2/15.
 */
public class Auth {
    JsonFactory jsonFactory;
    HttpTransport httpTransport;
    final String ACCOUNT_ID;
    final String ACCOUNT_P12;
    final String OAUTH_JSON_FILEPATH;

    public Auth(JsonFactory jsonFactory, HttpTransport httpTransport) {
        this.jsonFactory = jsonFactory;
        this.httpTransport = httpTransport;
        this.ACCOUNT_ID = System.getenv("GOOGLE_SERVICE_ACCOUNT_ID");
        this.ACCOUNT_P12 = System.getenv("GOOGLE_SERVICE_ACCOUNT_P12");
        this.OAUTH_JSON_FILEPATH = System.getenv("GOOGLE_OAUTH_FILE");
    }

    Credential buildGoogleCredential() throws GeneralSecurityException, IOException{
        // Load client secret, check that it is well formed..
        // todo: is this even needed??
        GoogleClientSecrets clientSecrets;
        try {
            FileReader fileReader = new FileReader(new File(OAUTH_JSON_FILEPATH));
            clientSecrets = GoogleClientSecrets.load(jsonFactory, fileReader);
            if (clientSecrets.getDetails().getClientId() == null ||
                    clientSecrets.getDetails().getClientSecret() == null) {
                throw new Exception("client_secrets not well formed.");
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            System.out.println("Problem loading client_secrets.json file. Make sure it exists, you are " +
                    "loading it with the right path, and a client ID and client secret are " +
                    "defined in it.\n" + e.getMessage());
            System.exit(1);
        }

        Set<String> scopes = new HashSet<>();
        scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
        scopes.add(StorageScopes.DEVSTORAGE_READ_ONLY);
        scopes.add(StorageScopes.DEVSTORAGE_READ_WRITE);

        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(ACCOUNT_ID)
                .setServiceAccountPrivateKeyFromP12File(new File(ACCOUNT_P12))
                .setServiceAccountScopes(scopes)
                .build();
    }
}
