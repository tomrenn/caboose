package com.tomrenn.njtrains.caboose;


import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        GoogleModule.class,
    }
)
public interface AppComponent {
    void inject(Main main);

}
