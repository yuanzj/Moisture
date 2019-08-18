package com.drt.moisture;

import android.app.Application;

import com.drt.moisture.data.source.LocalDataService;
import com.drt.moisture.data.source.local.LocalDataServiceImpl;


public class App extends Application {

    private static App app;

    public static App getInstance() {
        if (app == null) {
            throw new IllegalAccessError("App is null");
        }
        return app;
    }

    private LocalDataService localDataService;

    public LocalDataService getLocalDataService() {
        return localDataService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        localDataService = new LocalDataServiceImpl(this);
    }


}