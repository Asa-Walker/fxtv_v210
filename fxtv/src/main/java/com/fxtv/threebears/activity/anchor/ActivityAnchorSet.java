package com.fxtv.threebears.activity.anchor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.pagerindicator.TabPageIndicator;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.adapter.TabPageIndicatorAdapter;
import com.fxtv.threebears.fragment.module.anchor.FragmentNewAnchorAllModel;
import com.fxtv.threebears.model.GameTab;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 主播集合
 *
 * @author Android2
 */
public class ActivityAnchorSet extends BaseFragmentActivity {
    private ViewPager mViewPager;
    private ViewPageAdapter mAdapter;
    private ArrayList<Fragment> mFragmentList;
    private List<GameTab> mGameTabs;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                getData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_all_anchor);
        initView();
        Utils.showProgressDialog(this);
        mHandler.sendEmptyMessageDelayed(10, 100);
    }

    private void initView() {
        initActionBar();
//        getData();
    }

    private void getData() {
        if (mGameTabs == null) {
            mGameTabs = new ArrayList<GameTab>();
        }
        JsonObject params = new JsonObject();
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_gameList, params);
        getSystem(SystemHttp.class).get(this, url, "getFindList", true, true, new RequestCallBack<List<GameTab>>() {
            @Override
            public void onSuccess(List<GameTab> data, Response resp) {
                mGameTabs = data;
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                initIndicator();
//                Utils.dismissProgressDialog();
            }
        });
    }

    private void initIndicator() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }
        for (int i = 0; i < mGameTabs.size(); i++) {
            FragmentNewAnchorAllModel model = new FragmentNewAnchorAllModel();
            mFragmentList.add(model);
        }
        TabPageIndicator mIndicator = (TabPageIndicator) findViewById(R.id.activity_new_all_anchor_tab);
        mViewPager = (ViewPager) findViewById(R.id.activity_new_all_anchor_vp);
        if (mAdapter == null) {
            mAdapter = new ViewPageAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);
            mViewPager.setOffscreenPageLimit(1);
            mIndicator.setViewPager(mViewPager);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initActionBar() {
        ImageView back = (ImageView) findViewById(R.id.img_back);
        TextView title = (TextView) findViewById(R.id.my_title);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("主播");
        TextView myAnchor = (TextView) findViewById(R.id.ab_editor);
        myAnchor.setText("我的主播");
        myAnchor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getSystem(SystemUser.class).isLogin()) {
                    showToast(getResources().getString(R.string.notice_no_login));
                    FrameworkUtils.skipActivity(ActivityAnchorSet.this, ActivityLogin.class);
                    return;
                } else {
                    if (getSystem(SystemUser.class).checkHasGuradAnchor()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("anchor_id",
                                getSystem(SystemUser.class).mUser.guard_anchor);
                        FrameworkUtils.skipActivity(ActivityAnchorSet.this, ActivityAnchorZone.class, bundle);
                    } else {
                        showToast("请先守护主播");
                    }
                }
            }
        });
    }

    public class ViewPageAdapter extends TabPageIndicatorAdapter {
        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("id", mGameTabs.get(position).id);
            Fragment fragment = mFragmentList.get(position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public int getItemType(int position) {
            // String game_type =
            // getSystem(SystemUser.class).mShouldShowGameMenus
            // .get(position).game_type;
            // if (TextUtils.isEmpty(game_type)) {
            // return 0;
            // } else {
            // return Integer.parseInt(game_type);
            // }
            return 0;
        }

        @Override
        public int getItemPosition(Object object) {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mGameTabs.get(position).title;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGameTabs = null;
        mViewPager = null;
        FrameworkUtils.setEmptyList(mFragmentList);
    }
}
