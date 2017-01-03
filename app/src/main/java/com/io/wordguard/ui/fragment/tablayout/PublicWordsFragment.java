package com.io.wordguard.ui.fragment.tablayout;

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
import com.io.wordguard.db.Word;
import com.io.wordguard.ui.adapters.WordRecyclerAdapter;
import com.io.wordguard.ui.util.SwipeCallback;
import com.io.wordguard.ui.util.SwipeHelperCallback;
import com.simplite.orm.DBObject;

import java.util.ArrayList;

public class PublicWordsFragment extends Fragment {

    RecyclerView recyclerView;
    WordRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_public_words, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.others_words_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new WordRecyclerAdapter(new ArrayList<Word>());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeHelperCallback(getContext(), 0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, new SwipeCallback() {
            @Override
            public void onSwiped(boolean isSwipedRight, int itemPosition) {
                adapter.removeItem(itemPosition);
            }
        }));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ContentProvider.getInstance().getPublicWords(getActivity(), new ContentProviderCallBack() {
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
