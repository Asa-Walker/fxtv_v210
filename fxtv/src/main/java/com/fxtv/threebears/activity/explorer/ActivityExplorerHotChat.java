package com.fxtv.threebears.activity.explorer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.fragment.module.explorer.FragmentExplorerFocus;
import com.fxtv.threebears.fragment.module.explorer.FragmentExplorerHot;

/**
 * 热聊话题
 */
public class ActivityExplorerHotChat extends BaseFragmentActivity {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private FragmentExplorerHot mFragmentHot;
    private FragmentExplorerFocus mFragmentFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_my_cache);
        initView();
    }

    private void initView() {
//        initViewPager();
        initActionBar();
        initFragment();
    }

    private void initFragment() {
        findViewById(R.id.hot_chat_linear).setVisibility(View.VISIBLE);
        mFragmentHot = new FragmentExplorerHot();
        mFragmentFocus = new FragmentExplorerFocus();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.hot_chat_linear, mFragmentHot, "hot")
                .add(R.id.hot_chat_linear, mFragmentFocus, "focus").commit();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.activity_my_cache_downloading) {
                    FragmentManager fragmentManager = ActivityExplorerHotChat.this.getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.show(mFragmentHot).hide(mFragmentFocus).commit();
                } else {
                    FragmentManager fragmentManager = ActivityExplorerHotChat.this.getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.show(mFragmentFocus).hide(mFragmentHot).commit();
                }
            }
        });
        RadioButton t = (RadioButton) mRadioGroup.getChildAt(0);
        t.setChecked(true);
    }

    private void initActionBar() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.activity_my_cache_cancel).setVisibility(View.GONE);
        initRdiaoGroup();
    }

    private void initRdiaoGroup() {
        mRadioGroup = (RadioGroup) findViewById(R.id.activity_my_cache_rg);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.activity_my_cache_downloading) {
                    mViewPager.setCurrentItem(0);
                } else {
                    mViewPager.setCurrentItem(1);
                }
            }
        });
        // 默认选中
        RadioButton t = (RadioButton) mRadioGroup.getChildAt(0);
        t.setText("热门");
        ((RadioButton) mRadioGroup.getChildAt(1)).setText("关注");
    }

    private void initViewPager() {
//        mViewPager = (ViewPager) findViewById(R.id.activity_my_cache_vp);
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageSelected(int arg0) {
//                if (arg0 == 0) {
//                    ((RadioButton) mRadioGroup.getChildAt(arg0)).setChecked(true);
//                } else if (arg0 == 1) {
//                    ((RadioButton) mRadioGroup.getChildAt(arg0)).setChecked(true);
//                }
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//                // TODO Auto-generated method stub
//            }
//        });
//        mFragmentList = new ArrayList<Fragment>(2);
//        mFragmentList.add(new FragmentExplorerHot());
//        mFragmentList.add(new FragmentExplorerFocus());
//        mViewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), mFragmentList));
    }
}
