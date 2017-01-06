package com.io.wordguard.db.simplite.interfaces;

import com.io.wordguard.db.simplite.DBObject;

import java.util.ArrayList;

public interface FindAllCallback {
    void onFinish(ArrayList<? extends DBObject> data, Object extra, Exception e);
}
