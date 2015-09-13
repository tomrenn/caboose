package com.tomrenn.njtrains.caboose.google;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import static com.google.common.io.Closeables.closeQuietly;

@Singleton
public class GoogleStorageImpl implements GoogleStorage {
    private static final String BUCKET_NAME = "njtrains";
    private static final String APPLICATION_TYPE_JSON = "application/json";
    Storage client;

    @Inject
    public GoogleStorageImpl(Storage client) {
        this.client = client;
    }


    @Override
    public Observable<String> putJson(final String name, final String json) {
        final InputStream jsonStream = new ByteArrayInputStream(json.getBytes(Charsets.UTF_8));
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String url = uploadStream(name, APPLICATION_TYPE_JSON, jsonStream, BUCKET_NAME);
                    subscriber.onNext(url);
                    subscriber.onCompleted();
                } catch (Exception e){
                    subscriber.onError(e);
                } finally {
                    closeQuietly(jsonStream);
                }
            }
        });
    }

    @Override
    public Observable<InputStream> getJson(final String name) {
        return Observable.create(new Observable.OnSubscribe<InputStream>() {
            @Override
            public void call(Subscriber<? super InputStream> subscriber) {
                try {
                    Storage.Objects.Get storageGet = client.objects().get(BUCKET_NAME, name);
                    InputStream inputStream = storageGet.executeMediaAsInputStream();
                    subscriber.onNext(inputStream);
                    subscriber.onCompleted();
                } catch (IOException e){
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<String> putPublicFile(final File file, final String contentType) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                    String url = uploadStream(file.getName(), contentType, inputStream, BUCKET_NAME);
                    subscriber.onNext(url);
                    subscriber.onCompleted();
                } catch (IOException|GeneralSecurityException e){
                    subscriber.onError(e);
                } finally {
                    Closeables.closeQuietly(inputStream);
                }
            }
        });
    }


    /**
     * Uploads data to an object in a bucket, returns the media link.
     *
     */
    public String uploadStream(String name, String contentType, InputStream stream, String bucketName)
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

        StorageObject storageObject = insertRequest.execute();
        return storageObject.getMediaLink();
    }
}
