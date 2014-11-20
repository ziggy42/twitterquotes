package com.andreapivetta.tweetbooster.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andreapivetta.tweetbooster.R;

public class CategoriesFirstFragment extends Fragment {

    private static final int NUM_PAGES = 7;

    public CategoriesFirstFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_categories_first, container,
                false);

        SharedPreferences myPref = getActivity().getSharedPreferences("MyPref", 0);

        ViewPager mPager = (ViewPager) rootView.findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        if (myPref.getBoolean("Animation", true))
            mPager.setPageTransformer(true, new ZoomOutPageTransformer());


        if (myPref.getBoolean("IS_THIS_A_RETURN", false)) {
            mPager.setCurrentItem(myPref.getInt("LAST_CATEGORY", 0));
            myPref.edit().putBoolean("IS_THIS_A_RETURN", false).apply();
        }

        return rootView;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return SingleCategoryFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.categories)[position];
        }
    }

}
