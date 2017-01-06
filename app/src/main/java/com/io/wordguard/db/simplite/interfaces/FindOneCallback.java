package com.io.wordguard.db.simplite.interfaces;

import com.io.wordguard.db.simplite.DBObject;

public interface FindOneCallback<EntityClass extends DBObject> {
    void onFinish(EntityClass data, Object extra, Exception e);
}
