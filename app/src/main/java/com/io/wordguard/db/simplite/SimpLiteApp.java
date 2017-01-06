package com.io.wordguard.db.simplite;

import android.app.Application;

import com.io.wordguard.db.simplite.SimpliteContext;

public class SimpLiteApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SimpliteContext.setContext(this);
    }
}
