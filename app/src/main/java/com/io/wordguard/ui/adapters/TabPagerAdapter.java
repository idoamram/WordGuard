package com.io.wordguard.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.io.wordguard.ui.fragment.AllWordsFragment;
import com.io.wordguard.ui.fragment.MyWordsFragment;
import com.io.wordguard.ui.fragment.OthersWordsFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public TabPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MyWordsFragment();
            case 1:
                return new OthersWordsFragment();
            case 2:
                return new AllWordsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}