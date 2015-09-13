package com.tomrenn.njtrains.caboose.google;

import com.tomrenn.njtrains.caboose.transit.TransitData;
import okio.Source;
import rx.Observable;

import java.io.File;
import java.io.InputStream;

/**
 *
 */
public interface GoogleStorage {

//    Observable<TransitData> fetchTransitData();
//
//    Observable<Boolean> putTransitData(TransitData transitData);

    Observable<String> putJson(String name, String json);

    Observable<InputStream> getJson(String name);


    /** Returns the public facing url string */
    Observable<String> putPublicFile(File file, String contentType);
}
