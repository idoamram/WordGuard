package com.io.wordguard.db;

import android.content.Context;
import android.util.Log;

import com.simplite.orm.interfaces.SimpLiteConfiguration;

public class Configs implements SimpLiteConfiguration {
    @Override
    public void beforeOnCreate(Context context) {
        Log.i("DB", "beforeOnCreate");
    }

    @Override
    public void afterOnCreate(Context context) {
        Log.i("DB", "afterOnCreate");
    }

    @Override
    public void beforeOnUpgrade(Context context) {
        Log.i("DB", "beforeOnUpgrade");
    }

    @Override
    public void afterOnUpgrade(Context context) {
        Log.i("DB", "afterOnUpgrade");
    }
}
