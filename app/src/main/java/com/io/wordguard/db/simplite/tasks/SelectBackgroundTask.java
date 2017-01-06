package com.io.wordguard.db.simplite.tasks;

import android.content.Context;
import com.io.wordguard.db.simplite.interfaces.SelectCallback;

public class SelectBackgroundTask extends BaseBackgroundTask {

    SelectCallback callback;

    public SelectBackgroundTask(Context context, boolean showProgressBar, SelectCallback callback) {
        super(context, showProgressBar);
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        callback.onFinish(result, exception);
    }
}