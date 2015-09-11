package com.tomrenn.njtrains.caboose;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

@Module
public class GoogleModule {

    @Provides @Singleton
    JsonFactory providesJsonFactory(){
        return JacksonFactory.getDefaultInstance();
    }

    @Provides @Singleton
    HttpTransport providesHttpTransport(){
        return new NetHttpTransport();
    }

    @Provides @Singleton
    Storage providesStorage(Credential creds, HttpTransport httpTransport, JsonFactory jsonFactory) {
        String APPLICATION_NAME = System.getenv("GOOGLE_PROJECT_NAME");
        return new Storage.Builder(httpTransport, jsonFactory, creds)
                .setApplicationName(APPLICATION_NAME).build();
    }

    @Provides @Singleton
    Credential providesCredential(JsonFactory jsonFactory, HttpTransport httpTransport) {
        String ACCOUNT_ID = System.getenv("GOOGLE_SERVICE_ACCOUNT_ID");
        String ACCOUNT_P12 = System.getenv("GOOGLE_SERVICE_ACCOUNT_P12");
        String OAUTH_JSON_FILEPATH = System.getenv("GOOGLE_OAUTH_FILE");

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

        try {
            return new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(ACCOUNT_ID)
                    .setServiceAccountPrivateKeyFromP12File(new File(ACCOUNT_P12))
                    .setServiceAccountScopes(scopes)
                    .build();
        } catch (IOException|GeneralSecurityException e){
            // todo log error
        }
        return null;

    }
}
