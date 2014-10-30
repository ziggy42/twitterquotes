package com.caldroid.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * MonthPagerAdapter holds 4 fragments, which provides fragment for current
 * month, previous month and next month. The extra fragment helps for recycle
 * fragments.
 * 
 * @author thomasdao
 * 
 */
public class MonthPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<com.caldroid.app.DateGridFragment> fragments;

	// Lazily create the fragments
	public ArrayList<com.caldroid.app.DateGridFragment> getFragments() {
		if (fragments == null) {
			fragments = new ArrayList<com.caldroid.app.DateGridFragment>();
			for (int i = 0; i < getCount(); i++) {
				fragments.add(new com.caldroid.app.DateGridFragment());
			}
		}
		return fragments;
	}

	public void setFragments(ArrayList<com.caldroid.app.DateGridFragment> fragments) {
		this.fragments = fragments;
	}

	public MonthPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
        com.caldroid.app.DateGridFragment fragment = getFragments().get(position);
		return fragment;
	}

	@Override
	public int getCount() {
		// We need 4 gridviews for previous month, current month and next month,
		// and 1 extra fragment for fragment recycle
		return CaldroidFragment.NUMBER_OF_PAGES;
	}

}
