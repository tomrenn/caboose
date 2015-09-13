package com.tomrenn.njtrains.caboose;

public class Components {
    static AppComponent appComponent;


    public static AppComponent getAppComponent(){
        if (appComponent == null){
            appComponent = DaggerAppComponent.builder().build();
        }
        return appComponent;
    }
}
