package com.io.wordguard.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.io.wordguard.R;
import com.io.wordguard.db.ContentProvider;
import com.io.wordguard.db.ContentProviderCallBack;
import com.io.wordguard.db.simplite.DBObject;
import com.io.wordguard.ui.adapters.WordRecyclerAdapter;
import com.io.wordguard.ui.util.SwipeCallback;
import com.io.wordguard.ui.util.SwipeHelperCallback;
import com.io.wordguard.word.Word;
import com.io.wordguard.word.WordHelper;

import java.util.ArrayList;

public class TrashFragment extends Fragment {

    RecyclerView recyclerView;
    WordRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trash, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.trash_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new WordRecyclerAdapter(getActivity(), new ArrayList<Word>());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeHelperCallback(getContext(), 0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, new SwipeCallback() {
            @Override
            public void onSwiped(boolean isSwipedRight, final int itemPosition) {
                WordHelper.changeStatus(isSwipedRight ? Word.STATUS_DONE : Word.STATUS_TRASH,
                        getActivity(), adapter, itemPosition);
            }
        }));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ContentProvider.getInstance().getTrashedWords(getActivity(), new ContentProviderCallBack() {
            @Override
            public void onFinish(ArrayList<? extends DBObject> data, Object extra, Exception e) {
                if (e == null) {
                    adapter.setData((ArrayList<Word>) data);
                } else e.printStackTrace();
            }
        });

        return rootView;
    }
}