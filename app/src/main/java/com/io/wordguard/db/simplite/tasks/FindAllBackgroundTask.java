package com.io.wordguard.db.simplite.tasks;

import android.content.Context;
import com.io.wordguard.db.simplite.DBObject;
import com.io.wordguard.db.simplite.interfaces.FindAllCallback;
import java.util.ArrayList;

public class FindAllBackgroundTask extends BaseBackgroundTask {

    FindAllCallback callback;
    ArrayList<? extends DBObject> data;

    public FindAllBackgroundTask(Context context, boolean showProgressBar, FindAllCallback callback) {
        super(context, showProgressBar);
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        callback.onFinish(data, result, exception);
    }

    public void setData(ArrayList<? extends DBObject> newData) {
        data = newData;
    }

    public ArrayList<? extends DBObject> getData() {
        return data;
    }
}