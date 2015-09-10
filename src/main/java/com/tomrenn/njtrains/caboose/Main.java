package com.tomrenn.njtrains.caboose;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;
import com.squareup.okhttp.OkHttpClient;
import rx.Observable;
import rx.functions.Func1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Created by tomrenn on 9/2/15.
 */
public class Main {
    static String APPLICATION_NAME;

    public static void main(String[] args) throws IOException, GeneralSecurityException {
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

        File file = new File("/Users/tomrenn/tmp/hello.txt");
        InputStream fileStream = new FileInputStream(file);
        uploadStream(client, "hello.txt", "text/plain", fileStream, "njtrains");


//        final String NJ_USERNAME = System.getenv("NJ_USERNAME");
//        final String NJ_PASSWORD = System.getenv("NJ_PASSWORD");
//
//        OkHttpClient httpClient = new OkHttpClient();
//        CookieManager cookieManager = new CookieManager();
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//        httpClient.setCookieHandler(cookieManager);
//
//        Observable<NJTransitZipUrls> dataUrls = Observable
//                .create(new GetTransitZipUrls(httpClient, NJ_USERNAME, NJ_PASSWORD));
//
//        Observable<TransitData.TransitItem> railItem = dataUrls
//                .flatMap(njTransitZipUrls -> Observable.create(new TransitItemUpdate(httpClient, client, njTransitZipUrls.railUrl, null)));


        // fetch latest transit_data.json
        // build into TransitData object.

        // for (rail, bus) get TransitItem object
        // - Pull zip file, compare hash to TransitItem from TransitData
        //  - If hash is different, upload zip and build new TransitItem
        //  - else return the same latestZip

        // compare generated TransitItem to one from Bucket, if different
        // upload the latestZip object
    }

    /**
     * Uploads data to an object in a bucket.
     *
     * @param name the name of the destination object.
     * @param contentType the MIME type of the data.
     * @param stream the data - for instance, you can use a FileInputStream to upload a file.
     * @param bucketName the name of the bucket to create the object in.
     */
    public static void uploadStream(Storage client,
            String name, String contentType, InputStream stream, String bucketName)
            throws IOException, GeneralSecurityException {
        InputStreamContent contentStream = new InputStreamContent(contentType, stream);
        StorageObject objectMetadata = new StorageObject()
                // Set the destination object name
                .setName(name)
                        // Set the access control list to publicly read-only
                .setAcl(Arrays.asList(
                        new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

        // Do the insert
        Storage.Objects.Insert insertRequest = client.objects().insert(
                bucketName, objectMetadata, contentStream);

        insertRequest.execute();
    }
}
