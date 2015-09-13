package com.tomrenn.njtrains.caboose;

import com.tomrenn.njtrains.caboose.google.GoogleStorage;
import com.tomrenn.njtrains.caboose.transit.TransitData;
import com.tomrenn.njtrains.caboose.transit.TransitService;
import rx.Observable;
import rx.functions.Action1;

import javax.inject.Inject;

import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class Main {
    @Inject GoogleStorage storage;
    @Inject TransitService transitService;

    TransitData cachedData;

    public Main(){
        AppComponent appComponent = Components.getAppComponent();
        appComponent.inject(this);
        System.out.println(storage);
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.doStuff();
    }

    public void doStuff(){
        get("/hello", (req, res) -> "Hello world");
        get("/goodbye", (req, res) -> "Goodbye!");

        transitService.savedTransitData()
                .subscribe(transitData -> {
                    if (transitData == null) {
                        System.out.println("Fetching transit data");
                        transitService.fetchTransitData()
                                .subscribe(Main.this::cacheTransitData,
                                        throwable -> throwable.printStackTrace());
                    } else {
                        cacheTransitData(transitData);
                    }
                }, throwable -> throwable.printStackTrace());

        Observable.interval(5, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    System.out.println("new interval " + aLong);
                });
    }

    public void cacheTransitData(TransitData transitData){
        this.cachedData = transitData;
    }

    /** Mildly expensive operation to download zip files */
    public void updateData() {
        Observable<TransitData> savedData = transitService.savedTransitData();
//        Observable<TransitData> newData =
    }


}
