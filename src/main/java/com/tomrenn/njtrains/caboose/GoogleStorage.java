package com.tomrenn.njtrains.caboose;

import rx.Observable;

import java.io.File;

/**
 *
 */
public interface GoogleStorage {

    Observable<TransitData> fetchTransitData();

    Observable<Boolean> putTransitData(TransitData transitData);

    /** Returns the public facing url string */
    Observable<String> putPublicFile(File file);
}
