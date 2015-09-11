package com.tomrenn.njtrains.caboose;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;
import rx.Observable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Created by tomrenn on 9/10/15.
 */
public class GoogleStorageImpl implements GoogleStorage {
    Storage client;

    public GoogleStorageImpl(Storage client) {
        this.client = client;
    }

    @Override
    public Observable<TransitData> fetchTransitData() {
        return null;
    }

    @Override
    public Observable<Boolean> putTransitData(TransitData transitData) {
        return null;
    }

    @Override
    public Observable<String> putPublicFile(File file) {
        return null;
    }


    /**
     * Uploads data to an object in a bucket.
     *
     * @param name the name of the destination object.
     * @param contentType the MIME type of the data.
     * @param stream the data - for instance, you can use a FileInputStream to upload a file.
     * @param bucketName the name of the bucket to create the object in.
     */
    public void uploadStream(String name, String contentType, InputStream stream, String bucketName)
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
