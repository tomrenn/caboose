package com.tomrenn.njtrains.caboose.transit;

import rx.Observable;

public interface TransitService {

    /** Gets the latest transit data */
    Observable<TransitData> fetchTransitData();

    /** Saved transit data */
    Observable<TransitData> savedTransitData();
}
