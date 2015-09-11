package com.tomrenn.njtrains.caboose;

import com.google.api.services.storage.Storage;

import javax.inject.Inject;


public class Main {
    @Inject Storage storage;

    public Main(){
        AppComponent appComponent = DaggerAppComponent.builder().build();
        appComponent.inject(this);
        System.out.println(storage);
    }

    public static void main(String[] args) {
        Main main = new Main();


//        final String NJ_USERNAME = System.getenv("NJ_USERNAME");
//        final String NJ_PASSWORD = System.getenv("NJ_PASSWORD");
//
//        OkHttpClient httpClient = new OkHttpClient();
//        CookieManager cookieManager = new CookieManager();
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//        httpClient.setCookieHandler(cookieManager);
//
//        Observable<NJTransitZipUrls> dataUrls = Observable
//                .create(new GetTransitZipUrls(httpClient, NJ_USERNAME, NJ_PASSWORD));
//
//        Observable<TransitData.TransitItem> railItem = dataUrls
//                .flatMap(njTransitZipUrls -> Observable.create(new TransitItemUpdate(httpClient, client, njTransitZipUrls.railUrl, null)));


        // fetch latest transit_data.json
        // build into TransitData object.

        // for (rail, bus) get TransitItem object
        // - Pull zip file, compare hash to TransitItem from TransitData
        //  - If hash is different, upload zip and build new TransitItem
        //  - else return the same latestZip

        // compare generated TransitItem to one from Bucket, if different
        // upload the latestZip object
    }

}
