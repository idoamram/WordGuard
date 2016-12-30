package com.io.wordguard.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.io.wordguard.R;
import com.io.wordguard.ui.adapters.TabPagerAdapter;
import com.io.wordguard.ui.util.ThemeUtils;

public class ActiveFragment extends Fragment {
    private AppBarLayout mAppBarLayout;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_active, container, false);

        mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBar);

        TabLayout tabLayout = new TabLayout(mAppBarLayout.getContext());
        TabLayout.LayoutParams layoutParams = new TabLayout.LayoutParams(
                TabLayout.LayoutParams.MATCH_PARENT,
                TabLayout.LayoutParams.WRAP_CONTENT);
        tabLayout.setBackgroundColor(ThemeUtils.getThemeColor(getActivity(), R.color.colorPrimary));
        tabLayout.setLayoutParams(layoutParams);

        mAppBarLayout.addView(tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_words));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.others_words));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all_words));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        TabPagerAdapter adapter = new TabPagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove tab layout when fragment view destroyed
        mAppBarLayout.removeViewAt(1);
    }
}