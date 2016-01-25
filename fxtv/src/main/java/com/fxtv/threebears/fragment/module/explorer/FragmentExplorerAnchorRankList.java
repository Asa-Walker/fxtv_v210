package com.fxtv.threebears.fragment.module.explorer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.threebears.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 主播排行榜
 *
 * @author Android2
 */
public class FragmentExplorerAnchorRankList extends BaseFragment {
    private RadioGroup mRadioGroup;
    private ViewPager mViewPager;
    private List<FragmentExplorerAnchorChild> mFragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_acnhor_rank_list, container, false);
        initView();
        return mRoot;
    }

    private void initView() {
        initViewPager();
        initRadioButton();
    }

    public void updateRankList() {
        for (int i = 0; i < mFragmentList.size(); i++) {
            mFragmentList.get(i).getData();
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) mRoot.findViewById(R.id.anchor_rank_list_vp);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 0) {
                    ((RadioButton) mRadioGroup.getChildAt(arg0)).setChecked(true);
                } else if (arg0 == 1) {
                    ((RadioButton) mRadioGroup.getChildAt(arg0)).setChecked(true);
                } else if (arg0 == 2) {
                    ((RadioButton) mRadioGroup.getChildAt(arg0)).setChecked(true);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }


            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
        mFragmentList = new ArrayList<FragmentExplorerAnchorChild>(3);
        // 主播订阅排行榜
    /*	mFragmentList.add(new FragmentExplorerAnchorChild("anchorOrder"));
		// 主播守护排行榜
		mFragmentList.add(new FragmentExplorerAnchorChild("anchorGuard"));
		// 主播热度排行榜
		mFragmentList.add(new FragmentExplorerAnchorChild("anchorVisit"));*/
        mFragmentList.add(new FragmentExplorerAnchorChild());
        mFragmentList.add(new FragmentExplorerAnchorChild());
        mFragmentList.add(new FragmentExplorerAnchorChild());
        mViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);
    }


    private void initRadioButton() {
        mRadioGroup = (RadioGroup) mRoot.findViewById(R.id.fragment_rank_list_rg);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 订阅排行
                if (checkedId == R.id.subscribe_rank_list) {
                    mViewPager.setCurrentItem(0);
                }
                // 守护排行
                if (checkedId == R.id.protection_rank_list) {
                    mViewPager.setCurrentItem(1);
                }
                // 热度排行
                if (checkedId == R.id.hot_rank_list) {
                    mViewPager.setCurrentItem(2);
                }
            }
        });
        // 默认选中
        RadioButton t = (RadioButton) mRadioGroup.getChildAt(0);
        t.setChecked(true);
    }

    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Bundle bundle = new Bundle();
            Fragment fragment = mFragmentList.get(arg0);
            if (arg0 == 0) {
                bundle.putString("name", "anchorOrder");
            }
            if (arg0 == 1) {
                bundle.putString("name", "anchorGuard");
            }
            if (arg0 == 2) {
                bundle.putString("name", "anchorVisit");
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
