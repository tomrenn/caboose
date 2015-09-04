package com.tomrenn.njtrains.caboose;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by tomrenn on 9/2/15.
 */
public class Main {
    static String APPLICATION_NAME;

    public static void main(String[] args) throws IOException {
        APPLICATION_NAME = System.getenv("GOOGLE_PROJECT_NAME");
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        HttpTransport httpTransport = new NetHttpTransport();
        Auth auth = new Auth(jsonFactory, httpTransport);
        Credential creds = null;
        try {
            creds = auth.buildGoogleCredential();
        } catch (GeneralSecurityException|IOException e){
            // log error
            System.exit(1);
        }

        String BUCKET_NAME = "njtrains";
        Storage client = new Storage.Builder(httpTransport, jsonFactory, creds)
                .setApplicationName(APPLICATION_NAME).build();

        Storage.Buckets.Get getBucket = client.buckets().get(BUCKET_NAME);
        getBucket.setProjection("full");
        Bucket bucket = getBucket.execute();
        System.out.println("name: " + BUCKET_NAME);
        System.out.println("location: " + bucket.getLocation());
        System.out.println("timeCreated: " + bucket.getTimeCreated());
        System.out.println("owner: " + bucket.getOwner());

        // fetch latest transit_data.json
        // build into TransitData object.

        // for (rail, bus) get LatestZip object
        // - Pull zip file, compare hash to LatestZip from TransitData
        //  - If hash is different, upload zip and build new LatestZip
        //  - else return the same latestZip

        // compare generated LatestZip to one from Bucket, if different
        // upload the latestZip object
    }
}
