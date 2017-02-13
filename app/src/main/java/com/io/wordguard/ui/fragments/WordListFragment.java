package com.io.wordguard.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.io.wordguard.R;
import com.io.wordguard.ui.adapters.WordListRecyclerAdapter;
import com.io.wordguard.ui.componenets.SwipeRecycleView;
import com.io.wordguard.word.Word;

import java.util.ArrayList;

public class WordListFragment extends Fragment {

    private static final String TAG = "WordListFragment";

    private WordListRecyclerAdapter mAdapter;
    private SwipeRecycleView mRecyclerView;

    public WordListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_word_list, container, false);

        mRecyclerView = (SwipeRecycleView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setSwipeListener(new SwipeRecycleView.OnSwipeActionListener() {
            @Override
            public void onSwipe(int position, @SwipeDirection int direction) {
                mAdapter.removeItemAt(position);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new WordListRecyclerAdapter(new ArrayList<Word>(), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

}
