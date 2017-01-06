package com.io.wordguard.db.simplite.tasks;

import android.content.Context;
import com.io.wordguard.db.simplite.interfaces.CRUDCallback;

public class CRUDBackgroundTask extends BaseBackgroundTask {

    CRUDCallback callback;

    public CRUDBackgroundTask(Context context, boolean showProgressBar, CRUDCallback callback) {
        super(context, showProgressBar);
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        callback.onFinish(result, exception);
    }
}
