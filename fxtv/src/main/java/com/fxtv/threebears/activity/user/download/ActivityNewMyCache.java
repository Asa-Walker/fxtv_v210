package com.fxtv.threebears.activity.user.download;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.frame.BaseFragmentAdapter;
import com.fxtv.threebears.R;
import com.fxtv.threebears.fragment.module.cache.FragmentMyCacheDownLoading;
import com.fxtv.threebears.fragment.module.cache.FragmentMyCacheDownloaded;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版缓存页(把缓存过和正在缓存的页面放在一个activity中)
 *
 * @author Android2
 */
public class ActivityNewMyCache extends BaseFragmentActivity {
    private TextView mCancel;
    private RadioGroup mRadioGroup;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private boolean changeIcon = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_my_cache);
        initView();
    }

    private void initView() {
        initmViewPager();
        initActionBar();
    }

    private void initmViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.activity_my_cache_vp);
        mViewPager.setVisibility(View.VISIBLE);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 0) {
                    ((RadioButton) mRadioGroup.getChildAt(arg0)).setChecked(true);
                } else if (arg0 == 1) {
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
        mFragmentList = new ArrayList<Fragment>(2);
        mFragmentList.add(new FragmentMyCacheDownLoading());
        mFragmentList.add(new FragmentMyCacheDownloaded());
        mViewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), mFragmentList));
    }

    private void initActionBar() {
        mCancel = (TextView) findViewById(R.id.activity_my_cache_cancel);
        mCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentMyCacheDownloaded) mFragmentList.get(1)).changeIcon(changeIcon);
                if (changeIcon) {
                    changeIcon = false;
                    mCancel.setText("取消");
                } else {
                    changeIcon = true;
                    mCancel.setText("编辑");
                }
            }
        });
        findViewById(R.id.img_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initRdiaoGroup();
    }

    private void initRdiaoGroup() {
        mRadioGroup = (RadioGroup) findViewById(R.id.activity_my_cache_rg);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.activity_my_cache_downloading) {
                    mViewPager.setCurrentItem(0);
                    mCancel.setVisibility(View.GONE);
                } else {
                    mViewPager.setCurrentItem(1);
                    mCancel.setVisibility(View.VISIBLE);
                }
            }
        });
        // 默认选中
        RadioButton t = (RadioButton) mRadioGroup.getChildAt(0);
        t.setChecked(true);
    }

	/*class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return mFragmentList.get(arg0);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}
	}*/
}
