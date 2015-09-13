package com.tomrenn.njtrains.caboose.transit;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.tomrenn.njtrains.caboose.google.GoogleStorage;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStreamReader;
import java.io.Reader;

import static com.google.common.io.Closeables.closeQuietly;

@Singleton
public class TransitServiceImpl implements TransitService {
    static final String TRANSIT_DATA_NAME = "transit.json";
    GoogleStorage googleStorage;
    Gson gson;

    @Inject
    public TransitServiceImpl(GoogleStorage googleStorage, Gson gson) {
        this.googleStorage = googleStorage;
        this.gson = gson;
    }

    public Observable<NJTransitZipUrls> getZipUrls() {
        return GetTransitZipUrls.observable();
    }

    @Override
    public Observable<TransitData> fetchTransitData() {
        return Observable.create(new Observable.OnSubscribe<TransitData>() {

            @Override
            public void call(Subscriber<? super TransitData> subscriber) {
                // BAH! Troubles with zip()
                NJTransitZipUrls transitZipUrls = getZipUrls().toBlocking().first();
                TransitData transitData = savedTransitData().toBlocking().first();

                TransitItem railItem = transitData == null ? null : transitData.getRail();
                TransitItem busItem = transitData == null ? null : transitData.getBus();

                Observable<TransitItem> railObservable = TransitItemUpdate
                        .observable(transitZipUrls.railUrl, railItem);
                Observable<TransitItem> busObservable = TransitItemUpdate
                        .observable(transitZipUrls.busUrl, busItem);
                busObservable = Observable.just(null);

                Observable.zip(railObservable, busObservable, TransitData::new)
                        .flatMap(newerTransitData -> saveIfNewer(transitData, newerTransitData))
                        .subscribe(subscriber);
            }
        });
    }

    public Observable<TransitData> saveIfNewer(TransitData previous, final TransitData newer){
        if (newer.isNewer(previous)){
            String json = gson.toJson(newer, TransitData.class);
            return googleStorage.putJson(TRANSIT_DATA_NAME, json)
                    .map(inputStream -> newer);
        } else {
            return Observable.just(previous);
        }
    }

    public Observable<Void> saveTransitData(TransitData transitData) {
        String json = gson.toJson(transitData, TransitData.class);
        return googleStorage.putJson(TRANSIT_DATA_NAME, json).cast(Void.class);
    }

    @Override
    public Observable<TransitData> savedTransitData() {
        return googleStorage.getJson(TRANSIT_DATA_NAME)
                .map(source -> {
                    try {
                        Reader reader = new InputStreamReader(source);
                        return gson.fromJson(reader, TransitData.class);
                    } catch (JsonSyntaxException | JsonIOException e) {
                        return null;
                    } finally {
                        closeQuietly(source);
                    }
                })
                .onErrorReturn(throwable -> null);
    }

}
