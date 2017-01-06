package com.io.wordguard.db.simplite.tasks;

import android.content.Context;

import com.io.wordguard.db.simplite.DBObject;
import com.io.wordguard.db.simplite.interfaces.FindOneCallback;

public class FindOneBackgroundTask<EntityClass extends DBObject> extends BaseBackgroundTask {

    FindOneCallback callback;
    EntityClass object;

    public FindOneBackgroundTask(Context context, boolean showProgressBar, FindOneCallback callback) {
        super(context, showProgressBar);
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        callback.onFinish(object, result, exception);
    }
}
