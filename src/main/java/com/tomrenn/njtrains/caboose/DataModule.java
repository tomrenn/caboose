package com.tomrenn.njtrains.caboose;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

@Module
public class DataModule {

    /** Client that likes cookies! */
    @Provides @Singleton
    OkHttpClient providesHttpClient(){
        OkHttpClient httpClient = new OkHttpClient();
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        httpClient.setCookieHandler(cookieManager);
        return httpClient;
    }

    @Provides @Singleton
    Gson providesGson(){
        return new GsonBuilder().create();
    }
}
