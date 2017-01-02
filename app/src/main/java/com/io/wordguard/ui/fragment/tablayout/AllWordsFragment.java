package com.io.wordguard.ui.fragment.tablayout;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.io.wordguard.R;
import com.io.wordguard.db.Word;
import com.io.wordguard.ui.adapters.WordRecyclerAdapter;
import com.io.wordguard.ui.util.SwipeCallback;
import com.io.wordguard.ui.util.SwipeHelperCallback;
import com.simplite.orm.interfaces.BackgroundTaskCallBack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllWordsFragment extends Fragment {

    RecyclerView recyclerView;
    WordRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_words, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.all_words_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Word word1 = new Word(getContext());
        word1.setTitle("Call to Mama");
        word1.setContactPhoneNumber("0545290025");
        word1.setDeadLine(new Date(new Date().getTime() + 824000000));

        Word word2 = new Word(getContext());
        word2.setTitle("Make papa a cup of tea");
        word2.setContactEmail("ido.movieditor@gmail.com");
        word2.setDeadLine(new Date(new Date().getTime() + 734000000));

        Word word3 = new Word(getContext());
        word3.setTitle("Buy a MacBook Pro 15inch with TouchBar");
        word3.setContactName("iDigital");
        word3.setLatitude(29.557669);
        word3.setLongitude(34.951925);
        word3.setDeadLine(new Date(new Date().getTime() + 974000000));

        Word word4 = new Word(getContext());
        word4.setTitle("Surf in the weekend!!");
        word4.setDeadLine(new Date(new Date().getTime() + 1004000000));

        ArrayList<Word> words = new ArrayList<>();
        words.add(word1);
        words.add(word2);
        words.add(word3);
        words.add(word4);

        adapter = new WordRecyclerAdapter(words);
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
        return rootView;
    }
}
