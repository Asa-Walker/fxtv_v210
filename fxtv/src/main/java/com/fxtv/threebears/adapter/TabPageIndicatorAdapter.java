package com.fxtv.threebears.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {

	public TabPageIndicatorAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		return null;
	}

	@Override
	public int getCount() {
		return 0;
	}

	public int getItemType(int position) {
		return 0;
	}
}
