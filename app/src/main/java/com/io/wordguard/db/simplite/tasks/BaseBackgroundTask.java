package com.io.wordguard.db.simplite.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.io.wordguard.R;

public class BaseBackgroundTask extends AsyncTask<Void,Void,Object> {

    ProgressDialog progressDialog;
    Exception exception;
    boolean showProgressBar;
    Context context;

    public BaseBackgroundTask(Context context, boolean showProgressBar) {
        this.context = context;
        this.showProgressBar = showProgressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (showProgressBar) {
            progressDialog = getProgressDialog(context);
            progressDialog.show();
        }
    }


    @Override
    protected Object doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if (showProgressBar) {
            progressDialog.dismiss();
        }
    }

    private ProgressDialog getProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public void setException(Exception e){
        e.printStackTrace();
        exception = new Exception(e);
    }
}
