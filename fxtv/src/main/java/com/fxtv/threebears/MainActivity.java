package com.fxtv.threebears;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.threebears.activity.search.ActivitySearchFor;
import com.fxtv.threebears.activity.user.ActivityHistory;
import com.fxtv.threebears.activity.user.download.ActivityNewMyCache;
import com.fxtv.threebears.fragment.FragmentPersonal;
import com.fxtv.threebears.fragment.FragmentTabExplorer;
import com.fxtv.threebears.fragment.FragmentTabGame;
import com.fxtv.threebears.fragment.FragmentTabMain;
import com.fxtv.threebears.fragment.FragmentTabSelf;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;

import java.util.Arrays;

/**
 * @author FXTV-Android 首页
 */
public class MainActivity extends BaseFragmentActivity implements OnClickListener {
    private static final String TAG = "MainActivity";
    private int mLastSelectIndex = 0;
    private BaseFragment[] mFragmentManagerPool;
    private int[] layoutIdArray = new int[]{R.id.layout_home, R.id.layout_game, R.id.layout_self, R.id.layout_find, R.id.layout_personal};
    public boolean hasMessage = false;
    private TextView actionBarTitle;
    private View activity_main_actonbar;
    private ImageView actionBarLeftImg2;
    private ImageView im_seach, im_down;
    private ImageView im_history;
    private TextView actionBarLeftTextView;
    private TextView actionBarRightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (baseSavedInstance != null) {
            mLastSelectIndex = baseSavedInstance.getInt("mLastCheckedid");
        }
        setContentView(R.layout.activity_main);
        if (mFragmentManagerPool == null) {
            mFragmentManagerPool = new BaseFragment[]{new FragmentTabMain(), new FragmentTabGame(), new FragmentTabSelf(), new FragmentTabExplorer(), new FragmentPersonal()};
        }
        //systemAnim=getSystem(SystemAnim.class);
        initActionBar();
        initRadioGroup();
        if (!TextUtils.isEmpty(getStringExtra("type"))) {
            mLastSelectIndex = 4;
        }
        tabClick(mLastSelectIndex);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mLastCheckedid", mLastSelectIndex);
    }

    public void jump2Child(int position) {
        tabClick(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageView mineButton = (ImageView) ((ViewGroup) findViewById(layoutIdArray[4])).getChildAt(0);//我的
        final Boolean isMine = mineButton.isSelected();

        if (!getSystem(SystemUser.class).isLogin()) {
            hasMessage = false;
            if (isMine) {
                mineButton.setImageResource(R.drawable.activity_main_icon_anchor_1);
            } else {
                mineButton.setImageResource(R.drawable.activity_main_icon_anchor_0);
            }
        } else {
            getSystem(SystemUser.class).checkNewMessage(new IUserBusynessCallBack() {

                @Override
                public void onResult(boolean result, String arg) {
                    if (result) {
                        if (!"".equals(arg)) {
                            int resouceId;
                            if ("1".equals(arg)) {
                                hasMessage = true;
                                if (isMine) {
                                    resouceId = R.drawable.mine_new_msg1;
                                } else {
                                    resouceId = R.drawable.mine_new_meg0;
                                }
                            } else {
                                hasMessage = false;
                                if (isMine) {
                                    resouceId = R.drawable.activity_main_icon_anchor_1;
                                } else {
                                    resouceId = R.drawable.activity_main_icon_anchor_0;
                                }
                            }
                            mineButton.setImageResource(resouceId);
                        }
                    } else {
                        Logger.i(TAG, arg);
                    }
                }
            });
        }

    }

    public void initFristGuide(int type) {
        final RelativeLayout bg = (RelativeLayout) findViewById(R.id.first_guide);
        bg.setVisibility(View.VISIBLE);
        switch (type) {
            case 1:
                final ImageView guideMine = (ImageView) findViewById(R.id.guide_mine);
                guideMine.setImageResource(R.drawable.guide_mine);
                guideMine.setVisibility(View.VISIBLE);
                bg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guideMine.setVisibility(View.GONE);
                        bg.setVisibility(View.GONE);
                    }
                });
                break;
            case 2:
                final ImageView guideFXShop = (ImageView) findViewById(R.id.guide_fx_shop);
                final ImageView guideAnchorSpace = (ImageView) findViewById(R.id.guide_anchor_space);
                final ImageView guideHotChat = (ImageView) findViewById(R.id.guide_hot_chat);
                guideFXShop.setImageResource(R.drawable.guide_video_focus);
                guideFXShop.setVisibility(View.VISIBLE);
                bg.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (guideFXShop.getVisibility() == View.VISIBLE) {
                            guideFXShop.setVisibility(View.GONE);
                            guideAnchorSpace.setImageResource(R.drawable.guide_earn_bicuit);
                            guideAnchorSpace.setVisibility(View.VISIBLE);
                        } else if (guideAnchorSpace.getVisibility() == View.VISIBLE) {
                            guideAnchorSpace.setVisibility(View.GONE);
                            guideHotChat.setImageResource(R.drawable.guide_hot_chat);
                            guideHotChat.setVisibility(View.VISIBLE);
                        } else if (guideHotChat.getVisibility() == View.VISIBLE) {
                            guideHotChat.setVisibility(View.GONE);
                            bg.setVisibility(View.GONE);
                        }
                    }
                });
                break;
            case 3:
                final ImageView guideMyPawn = (ImageView) findViewById(R.id.guide_wolf_skin);
                guideMyPawn.setImageResource(R.drawable.guide_wolf_skin);
                guideMyPawn.setVisibility(View.VISIBLE);
                bg.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        guideMyPawn.setVisibility(View.GONE);
                        bg.setVisibility(View.GONE);
                    }
                });
                break;
            default:
                break;
        }
    }

    private void initRadioGroup() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fragment, mFragmentManagerPool[0])
                .add(R.id.activity_main_fragment, mFragmentManagerPool[1])
                .add(R.id.activity_main_fragment, mFragmentManagerPool[2])
                .add(R.id.activity_main_fragment, mFragmentManagerPool[3])
                .add(R.id.activity_main_fragment, mFragmentManagerPool[4]);

        for (int i = 0; i < mFragmentManagerPool.length; i++) {
            if (i == mLastSelectIndex) {
                transaction.show(mFragmentManagerPool[i]);
            } else {
                transaction.hide(mFragmentManagerPool[i]);
            }
        }
        transaction.commit();

    }

    private void defaultMain() {
        actionBarTitle.setText(getString(R.string.activity_main_tab_main));
        actionBarLeftImg2.setVisibility(View.VISIBLE);
        im_seach.setVisibility(View.VISIBLE);
        im_down.setVisibility(View.VISIBLE);
        im_history.setVisibility(View.VISIBLE);
    }

    public void onTabClicked(View view) {
        int selectIndex = Arrays.binarySearch(layoutIdArray, view.getId());
        if (mLastSelectIndex == selectIndex) {
            return;
        }
        tabClick(selectIndex);
    }

    private void tabClick(int selectIndex) {
        if (selectIndex == 2 && !getSystem(SystemUser.class).isLogin()) {
            getSystem(SystemCommon.class).noticeAndLogin(MainActivity.this);
            return;
        }
        selectIndex(mLastSelectIndex, false);
        ViewGroup newGroup = selectIndex(selectIndex, true);

        ImageView img_personal;
        if (selectIndex == 4) {
            img_personal = (ImageView) newGroup.getChildAt(0);
            if (!hasMessage || !getSystem(SystemUser.class).isLogin()) {
                img_personal.setImageResource(R.drawable.activity_main_icon_anchor_1);
            } else {
                img_personal.setImageResource(R.drawable.mine_new_msg1);
            }
        } else {
            img_personal = (ImageView) ((ViewGroup) findViewById(layoutIdArray[4])).getChildAt(0);
            if (!hasMessage || !getSystem(SystemUser.class).isLogin()) {
                img_personal.setImageResource(R.drawable.activity_main_icon_anchor_0);
            } else {
                img_personal.setImageResource(R.drawable.mine_new_meg0);
            }
        }

        getSupportFragmentManager().beginTransaction()
                .hide(mFragmentManagerPool[mLastSelectIndex])
                .show(mFragmentManagerPool[selectIndex])
                .commit();

        mLastSelectIndex = selectIndex;

        //处理ActionBar
        activity_main_actonbar.setVisibility(View.VISIBLE);
        actionBarLeftImg2.setVisibility(View.GONE);
        im_seach.setVisibility(View.GONE);
        im_down.setVisibility(View.GONE);
        im_history.setVisibility(View.GONE);
        actionBarLeftTextView.setVisibility(View.GONE);
        actionBarRightTextView.setVisibility(View.GONE);

        switch (selectIndex) {
            case 1:
                actionBarTitle.setText(getString(R.string.activity_main_tab_game));
                im_seach.setVisibility(View.VISIBLE);
                im_down.setVisibility(View.VISIBLE);
                break;
            case 2:
                actionBarTitle.setText(getString(R.string.activity_main_tab_self));
                actionBarLeftTextView.setVisibility(View.VISIBLE);
                actionBarRightTextView.setVisibility(View.VISIBLE);
                break;
            case 3:
                actionBarTitle.setText(getString(R.string.activity_main_tab_explorer));
                break;
            case 4:
                //activity_main_actonbar.setVisibility(View.GONE);
                break;
            default://首页
                defaultMain();
                break;
        }
    }

    private ViewGroup selectIndex(int lastIndex, boolean isSelect) {
        ViewGroup lastGroup = (ViewGroup) findViewById(layoutIdArray[lastIndex]);
        lastGroup.getChildAt(0).setSelected(isSelect);
        lastGroup.getChildAt(1).setSelected(isSelect);
        return lastGroup;
    }

    private void initActionBar() {
        activity_main_actonbar = findViewById(R.id.activity_main_actonbar);

        actionBarTitle = (TextView) findViewById(R.id.ab_title);
        actionBarLeftTextView = (TextView) findViewById(R.id.ab_left_tv);
        actionBarLeftImg2 = (ImageView) findViewById(R.id.ab_left_img2);
        actionBarRightTextView = (TextView) findViewById(R.id.ab_right_tv);
        im_seach = (ImageView) findViewById(R.id.ab_right_img1);
        im_down = (ImageView) findViewById(R.id.ab_right_img2);
        im_history = (ImageView) findViewById(R.id.ab_right_img3);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBarLeftImg2.getLayoutParams();
        params.leftMargin = 0;//解决xml布局中有左边距 ;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        actionBarLeftImg2.setPadding(FrameworkUtils.dip2px(this, 10), 0, 0, 0);
        actionBarLeftImg2.setImageResource(R.drawable.logo);//飞熊视频

        im_seach.setImageResource(R.drawable.icon_seach1);
        im_down.setImageResource(R.drawable.icon_download3);
        im_history.setImageResource(R.drawable.history_icon);

        actionBarLeftTextView.setPadding(FrameworkUtils.dip2px(this, 10), 0, 0, 0);
        actionBarLeftTextView.setText("只看主播");
        actionBarLeftTextView.setOnClickListener(((FragmentTabSelf) mFragmentManagerPool[2]).clickListener);

        actionBarRightTextView.setText("我的订阅");
        actionBarRightTextView.setOnClickListener(((FragmentTabSelf) mFragmentManagerPool[2]).order_click);

        actionBarTitle.setOnClickListener(this);
        actionBarLeftImg2.setOnClickListener(this);
        im_seach.setOnClickListener(this);
        im_down.setOnClickListener(this);
        im_history.setOnClickListener(this);

        //defaultMain();
    }

    private long mClickTime;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mClickTime) > 2000) {
            FrameworkUtils.showToast(this, getResources().getString(R.string.notice_one_more_quit));
            mClickTime = System.currentTimeMillis();
        } else {
            getSystem(SystemAnalyze.class).analyzeAppEnd();
            Utils.exitApp(getApplicationContext());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ab_title:
            case R.id.ab_left_img2:
                if (mFragmentManagerPool != null && mFragmentManagerPool.length > 0)
                    ((FragmentTabMain) mFragmentManagerPool[0]).setCurrentItem(0);//滚动到第一页
                break;
            case R.id.ab_right_img1:
                FrameworkUtils.skipActivity(this, ActivitySearchFor.class);
                break;
            case R.id.ab_right_img2:
                getSystem(SystemAnalyze.class).analyzeUserAction("main_menu", "9", null);
                FrameworkUtils.skipActivity(this, ActivityNewMyCache.class);//9-缓存按钮
                break;
            case R.id.ab_right_img3:
                getSystem(SystemAnalyze.class).analyzeUserAction("main_menu", "10", null);
                FrameworkUtils.skipActivity(this, ActivityHistory.class);//10-历史记录按钮）
                break;
        }
    }
}
