package com.io.wordguard.db.simplite.interfaces;

import com.io.wordguard.db.simplite.DBObject;

import java.util.ArrayList;
import java.util.List;

public interface FindAllCallback<EntityClass extends DBObject> {
    void onFinish(ArrayList<EntityClass> data, Object extra, Exception e);
}
