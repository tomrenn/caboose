package com.tomrenn.njtrains.caboose.transit;


import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tomrenn.njtrains.caboose.*;
import com.tomrenn.njtrains.caboose.google.GoogleStorage;
import com.tomrenn.njtrains.caboose.util.Sha1Sink;
import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class TransitItemUpdate implements Observable.OnSubscribe<TransitItem> {
    @Inject OkHttpClient httpClient;
    @Inject GoogleStorage googleStorage;
    String zipUrl;
    TransitItem lastTransitItem;

    private TransitItemUpdate(String zipUrl, TransitItem lastTransitItem) {
        this.zipUrl = zipUrl;
        this.lastTransitItem = lastTransitItem;
        AppComponent appComponent = Components.getAppComponent();
        appComponent.inject(this);
    }

    public static Observable<TransitItem> observable(String zipUrl, TransitItem zipItem) {
        return Observable
                .create(new TransitItemUpdate(zipUrl, zipItem));
    }

    @Override
    public void call(final Subscriber<? super TransitItem> subscriber) {
        Request request = new Request.Builder()
                .url(zipUrl)
                .get()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                subscriber.onError(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()){
                    try {
                        TransitItem transitItem = handleResponse(response);
                        subscriber.onNext(transitItem);
                        subscriber.onCompleted();
                    } catch (NoSuchAlgorithmException e) {
                        subscriber.onError(e);
                    }
                } else {
                    subscriber.onError(new RuntimeException("Response unsuccessful " + response));
                }
            }
        });
    }

    /**
     * Checks if zip file hash is different.
     * If it is, upload and return new TransitItem.
     */
    TransitItem handleResponse(Response response) throws IOException, NoSuchAlgorithmException {
        String filename = Hashing.md5().hashString(zipUrl, Charsets.UTF_8).toString();
        File tmpFile = File.createTempFile(filename, ".zip");
        System.out.println("temp file " + tmpFile.getAbsolutePath());

        Sha1Sink shaSink = new Sha1Sink(Okio.sink(tmpFile));

        BufferedSink bufferedSink = Okio.buffer(shaSink);
        bufferedSink.writeAll(response.body().source());
        bufferedSink.close();

        String latestHash = shaSink.hashString();
        if (lastTransitItem == null
                || latestHash != null && !latestHash.equals(lastTransitItem.checksum)){
            // checksum is different, let's upload the new zip
            Observable<String> upload = googleStorage.putPublicFile(tmpFile, "application/zip");
            String mediaUrl = upload.toBlocking().first();
            return new TransitItem(System.currentTimeMillis(), latestHash, mediaUrl);
        } else {
            return lastTransitItem;
        }
    }
}
