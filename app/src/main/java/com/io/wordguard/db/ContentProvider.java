package com.io.wordguard.db;

import android.content.Context;

import com.simplite.orm.interfaces.BackgroundTaskCallBack;

import java.util.ArrayList;
import java.util.List;

public class ContentProvider {

    private static ContentProvider mInstance = new ContentProvider();
    public static ContentProvider getInstance() {return mInstance;}

    private ArrayList<Word> allWords = new ArrayList<>();
    private ArrayList<Word> privateWords = new ArrayList<>();;
    private ArrayList<Word> publicWords = new ArrayList<>();;
    private ArrayList<Word> doneWords = new ArrayList<>();;
    private ArrayList<Word> trashedWords = new ArrayList<>();;

    public void releaseAll() {
        allWords = new ArrayList<>();
        privateWords = new ArrayList<>();
        publicWords = new ArrayList<>();
        doneWords = new ArrayList<>();
        trashedWords = new ArrayList<>();
    }

    public void getAllWords(Context context, final ContentProviderCallBack callback) {
        if (allWords != null && allWords.size() > 0) callback.onFinish(allWords, null, null);
        else {
            Word.findByColumnInBackground(true, Word.COL_STATUS, Word.STATUS_ACTIVE, Word.COL_DEAD_LINE,
                    context, Word.class, new BackgroundTaskCallBack() {
                        @Override
                        public void onSuccess(String result, List<Object> data) {
                            if (data != null && data.size() > 0) {
                                allWords = (ArrayList) data;
                                callback.onFinish(allWords, null, null);
                            } else callback.onFinish(null, null, new Exception());
                        }

                        @Override
                        public void onError(String error) {
                            callback.onFinish(null, null, new Exception(error));
                        }
                    });
        }
    }

    public void getPrivateWords(Context context, final ContentProviderCallBack callback) {
        if (privateWords != null && privateWords.size() > 0) callback.onFinish(privateWords, null, null);
        else {
            Word.queryInBackground(Word.class, context, true,
                    new String[]{Word.COL_TYPE, Word.COL_STATUS},
                    new String[]{String.valueOf(Word.TYPE_PRIVATE), String.valueOf(Word.STATUS_ACTIVE)},
                    Word.COL_DEAD_LINE, new BackgroundTaskCallBack() {
                        @Override
                        public void onSuccess(String result, List<Object> data) {
                            if (data != null && data.size() > 0) {
                                privateWords = (ArrayList) data;
                                callback.onFinish(privateWords, null, null);
                            } else callback.onFinish(null, null, new Exception());
                        }

                        @Override
                        public void onError(String error) {
                            callback.onFinish(null, null, new Exception(error));
                        }
                    });
        }
    }

    public void getPublicWords(Context context, final ContentProviderCallBack callback) {
        if (publicWords != null && publicWords.size() > 0) callback.onFinish(publicWords, null, null);
        else {
            Word.queryInBackground(Word.class, context, true,
                    new String[]{Word.COL_TYPE, Word.COL_STATUS},
                    new String[]{String.valueOf(Word.TYPE_PUBLIC), String.valueOf(Word.STATUS_ACTIVE)},
                    Word.COL_DEAD_LINE, new BackgroundTaskCallBack() {
                        @Override
                        public void onSuccess(String result, List<Object> data) {
                            if (data != null && data.size() > 0) {
                                publicWords = (ArrayList) data;
                                callback.onFinish(publicWords, null, null);
                            } else callback.onFinish(null, null, new Exception());
                        }

                        @Override
                        public void onError(String error) {
                            callback.onFinish(null, null, new Exception(error));
                        }
                    });
        }
    }

    public void getDoneWords(Context context, final ContentProviderCallBack callback) {
        if (doneWords != null && doneWords.size() > 0) callback.onFinish(doneWords, null, null);
        else {
            Word.findByColumnInBackground(true, Word.COL_STATUS, Word.STATUS_DONE, Word.COL_DEAD_LINE,
                    context, Word.class, new BackgroundTaskCallBack() {
                        @Override
                        public void onSuccess(String result, List<Object> data) {
                            if (data != null && data.size() > 0) {
                                doneWords = (ArrayList) data;
                                callback.onFinish(doneWords, null, null);
                            } else callback.onFinish(null, null, new Exception());
                        }

                        @Override
                        public void onError(String error) {
                            callback.onFinish(null, null, new Exception(error));
                        }
                    });
        }
    }

    public void getTrashedWords(Context context, final ContentProviderCallBack callback) {
        if (trashedWords != null && trashedWords.size() > 0) callback.onFinish(trashedWords, null, null);
        else {
            Word.findByColumnInBackground(true, Word.COL_STATUS, Word.STATUS_TRASH, Word.COL_DEAD_LINE,
                    context, Word.class, new BackgroundTaskCallBack() {
                        @Override
                        public void onSuccess(String result, List<Object> data) {
                            if (data != null && data.size() > 0) {
                                trashedWords = (ArrayList) data;
                                callback.onFinish(trashedWords, null, null);
                            } else callback.onFinish(null, null, new Exception());
                        }

                        @Override
                        public void onError(String error) {
                            callback.onFinish(null, null, new Exception(error));
                        }
                    });
        }
    }
}
