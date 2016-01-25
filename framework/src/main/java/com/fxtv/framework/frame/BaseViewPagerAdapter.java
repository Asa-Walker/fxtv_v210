package com.fxtv.framework.frame;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/12/7.
 * ViewPager+View通用的适配器
 */
public class BaseViewPagerAdapter extends PagerAdapter{
    private List<? extends View> view_list;

    public BaseViewPagerAdapter(List<? extends View> view_list) {
        this.view_list = view_list;
    }

    public List<? extends View> getView_list() {
        return view_list;
    }

    public void setView_list(List<? extends View> view_list) {
        this.view_list = view_list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.view_list==null?0:this.view_list.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view==o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=this.view_list.get(position%this.view_list.size());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView(this.view_list.get(position)); 不安全
        container.removeView((View)object);
    }
}
