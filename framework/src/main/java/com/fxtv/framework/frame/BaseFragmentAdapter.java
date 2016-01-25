package com.fxtv.framework.frame;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/12/7.
 * ViewPager+Fragment通用的适配器
 */
public class BaseFragmentAdapter extends FragmentPagerAdapter{
    List<? extends Fragment> fragments;
    public BaseFragmentAdapter(FragmentManager fm) {
        super(fm);
    }
    public BaseFragmentAdapter(FragmentManager fm,List<? extends Fragment> fragments) {
        super(fm);
        this.fragments=fragments;
    }
    public List<? extends Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<? extends Fragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments==null?0:fragments.size();
    }
}
