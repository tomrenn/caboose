package com.tomrenn.njtrains.caboose;


import com.google.api.services.storage.Storage;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class TransitItemUpdate implements Observable.OnSubscribe<TransitData.TransitItem> {
    OkHttpClient httpClient;
    Storage storageClient;
    String zipUrl;
    TransitData.TransitItem lastZip;

    public TransitItemUpdate(OkHttpClient httpClient, Storage storageClient, String zipUrl, TransitData.TransitItem lastZip) {
        this.httpClient = httpClient;
        this.storageClient = storageClient;
        this.zipUrl = zipUrl;
        this.lastZip = lastZip;
    }

    public static Observable<TransitData.TransitItem> asObservable(){
        return null;
    }

    @Override
    public void call(final Subscriber<? super TransitData.TransitItem> subscriber) {
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
                        TransitData.TransitItem transitItem = handleResponse(response);
                        subscriber.onNext(transitItem);
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
     * Checks if zip file is different.
     * If it is, upload
     * @param response
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    TransitData.TransitItem handleResponse(Response response) throws IOException, NoSuchAlgorithmException{
        File tmpFile = File.createTempFile("data", "zip");

        Sha1Sink shaSink = new Sha1Sink(Okio.sink(tmpFile));

        BufferedSink bufferedSink = Okio.buffer(shaSink);
        bufferedSink.writeAll(response.body().source());
        bufferedSink.close();

        String latestHash = shaSink.hashString();
        if (latestHash != null && !latestHash.equals(lastZip.checksum)){
            // checksum is different, let's upload the new zip
            String dataZipUrl = "from storage";
            return new TransitData.TransitItem(System.currentTimeMillis(), latestHash, dataZipUrl);
        } else {
            return lastZip;
        }
    }
}
