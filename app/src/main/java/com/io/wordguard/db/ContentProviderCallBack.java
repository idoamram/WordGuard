package com.io.wordguard.db;

import com.simplite.orm.DBObject;

import java.util.ArrayList;

public interface ContentProviderCallBack {
    void onFinish(ArrayList<? extends DBObject> data, Object extra, Exception e);
}
