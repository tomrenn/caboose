package com.tomrenn.njtrains.caboose;


import com.tomrenn.njtrains.caboose.google.GoogleModule;
import com.tomrenn.njtrains.caboose.transit.GetTransitZipUrls;
import com.tomrenn.njtrains.caboose.transit.TransitItemUpdate;
import com.tomrenn.njtrains.caboose.transit.TransitModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        GoogleModule.class,
        DataModule.class,
        TransitModule.class,
    }
)
public interface AppComponent {
    void inject(Main main);

    void inject(TransitItemUpdate transitItemUpdate);

    void inject(GetTransitZipUrls getTransitZipUrls);
}
