package com.tomrenn.njtrains.caboose.transit;


import com.google.gson.Gson;
import com.tomrenn.njtrains.caboose.google.GoogleStorage;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class TransitModule {

    @Provides @Singleton
    TransitService providesTransitService(GoogleStorage googleStorage, Gson gson){
        return new TransitServiceImpl(googleStorage, gson);
    }

}
