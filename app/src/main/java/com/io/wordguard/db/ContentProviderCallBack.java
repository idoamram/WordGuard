package com.io.wordguard.db;

import com.io.wordguard.db.simplite.DBObject;
import java.util.ArrayList;

public interface ContentProviderCallBack<EntityType extends DBObject> {
    void onFinish(ArrayList<EntityType> data, Object extra, Exception e);
}
