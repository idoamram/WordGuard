package com.io.wordguard.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.io.wordguard.R;
import com.io.wordguard.ui.adapters.WordListRecyclerAdapter;
import com.io.wordguard.ui.componenets.SwipeableRecycleView;
import com.io.wordguard.word.Word;

import java.util.ArrayList;

public class WordListFragment extends Fragment {

    private WordListRecyclerAdapter mAdapter;
    private SwipeableRecycleView mRecyclerView;

    public WordListFragment() {
        // Required empty public constructor
    }

//    public static WordListFragment newInstance() {
//        WordListFragment fragment = new WordListFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_word_list, container, false);

        mRecyclerView = (SwipeableRecycleView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new WordListRecyclerAdapter(new ArrayList<Word>());
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

}
