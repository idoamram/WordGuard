package com.io.wordguard.word;

import android.content.Context;
import android.widget.Toast;

import com.io.wordguard.db.ContentProvider;
import com.io.wordguard.db.simplite.interfaces.CRUDCallback;
import com.io.wordguard.ui.adapters.WordRecyclerAdapter;

public class WordHelper {
    public static void changeStatus(int status, final Context context, final WordRecyclerAdapter adapter, final int itemPosition) {
        Word word = adapter.getWordAt(itemPosition);
        word.setStatus(status);
        word.saveInBackground(context, false, new CRUDCallback() {
            @Override
            public void onFinish(Object extra, Exception e) {
                if (e == null && (int)extra == 1) {
                    adapter.removeItem(itemPosition);
                    ContentProvider.getInstance().releaseAll();
                }
                else Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
