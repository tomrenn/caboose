package com.tomrenn.njtrains.caboose;

import com.google.gson.Gson;
import com.tomrenn.njtrains.caboose.google.GoogleStorage;
import com.tomrenn.njtrains.caboose.transit.TransitData;
import com.tomrenn.njtrains.caboose.transit.TransitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import spark.Response;

import javax.inject.Inject;

import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class Main {
    static final Logger log = LoggerFactory.getLogger(Main.class);
    static final String WORKER_ENV = "NJ_WORKER";
    @Inject GoogleStorage storage;
    @Inject TransitService transitService;
    @Inject Gson gson;

    // in-memory-cache that updates every 10 minutes
    String transitDataJson;


    public static void main(String[] args) {
        Main main = new Main();
        String worker = System.getenv(WORKER_ENV);
        boolean isWorker = worker != null && "1".equals(worker);
        if (isWorker){
            main.routelyUpdate();
        } else {
            main.simpleWebServer();
        }
    }


    public Main(){
        AppComponent appComponent = Components.getAppComponent();
        appComponent.inject(this);
    }


    public String getTransitData(Response response){
        response.type("application/json");
        if (transitDataJson == null){
            response.status(503);
            return "";
        }
        return transitDataJson;
    }

    public void simpleWebServer(){
        log.info("Starting simple web server");
        get("/", (req, res) -> getTransitData(res));

        Observable.timer(0, 10, TimeUnit.MINUTES)
                .flatMap(aLong -> transitService.savedTransitData())
                .subscribe(Main.this::cacheTransitData,
                        throwable -> log.error("Failed fetching Transit data", throwable));
    }

    public void routelyUpdate(){
        log.info("Running update worker");
        Observable.timer(0, 12, TimeUnit.HOURS, Schedulers.immediate())
                .flatMap(aLong1 -> transitService.fetchTransitData())
                .doOnError(throwable -> log.error("Error updating transit data", throwable))
                .subscribe();
    }

    public void cacheTransitData(TransitData transitData){
        this.transitDataJson = gson.toJson(transitData);
    }


}
