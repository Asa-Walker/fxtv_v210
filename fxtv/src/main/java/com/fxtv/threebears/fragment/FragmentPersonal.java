package com.fxtv.threebears.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.MainActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.activity.message.ActivityChatList;
import com.fxtv.threebears.activity.self.ActivitySelfMyOrder;
import com.fxtv.threebears.activity.user.ActitvityFavorites;
import com.fxtv.threebears.activity.user.ActivityHistory;
import com.fxtv.threebears.activity.user.ActivityMyPresent;
import com.fxtv.threebears.activity.user.download.ActivityNewMyCache;
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.activity.user.settings.ActivitySetup;
import com.fxtv.threebears.activity.user.userinfo.ActivityMyBiscuit;
import com.fxtv.threebears.activity.user.userinfo.ActivityMyLevel;
import com.fxtv.threebears.activity.user.userinfo.ActivityMyWolfSkin;
import com.fxtv.threebears.activity.user.userinfo.ActivityPersonalInformation;
import com.fxtv.threebears.model.PersonalChoose;
import com.fxtv.threebears.model.User;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemPreference;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

;

public class FragmentPersonal extends BaseFragment implements OnClickListener {
    private static final String TAG = "FragmentPersonal";
    private GridView mGridView;
    private TextView mBiscuit, mWolfskin, mLevel, mUserName;
    private Button mQianDao;
    private LinearLayout mBiscuitLayout, mWolfskinLayout, mLevelLayout;
    private ImageView mUserPic;
    private List<PersonalChoose> mList;
    private boolean isHiden = false;
    private boolean isFirstInto = true;
    private GridView mUserValue;
    private MyUserAdapter mUserAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_new_personal, container, false);
        isFirstInto = getSystem(SystemPreference.class).isFirstIntoPersonal();
        initView();

        return mRoot;
    }

    @Override
    public void onResume() {
        if (!getSystem(SystemUser.class).isLogin()) {
            try {
                mBiscuit.setText("0");
                mLevel.setText("LV0");
                mWolfskin.setText("0");
                mQianDao.setVisibility(View.GONE);

                if(mGridView!=null){
                    ImageView msgCenter = (ImageView) mGridView.findViewWithTag("img" + 0);
                    msgCenter.setImageResource(R.drawable.message_center);
                }

            } catch (Exception e) {
            }
        }
        if (!isHiden) {
            judgeUserState();
            checkMessage();
        }
        super.onResume();
    }

    private void checkMessage() {
        getSystem(SystemUser.class).checkNewMessage(new IUserBusynessCallBack() {
            @Override
            public void onResult(boolean result, String arg) {
                if(mGridView==null) return;
                ImageView msgCenter = (ImageView) mGridView.findViewWithTag("img" + 0);
                if (result) {
                    if (!"".equals(arg)) {
                        if ("1".equals(arg)) {
                            msgCenter.setImageResource(R.drawable.message_center_has_msg);
                        } else {
                            msgCenter.setImageResource(R.drawable.message_center);
                        }
                    }
                } else {
                    Logger.i(TAG, arg);
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHiden = hidden;
        if (!hidden) {
            judgeUserState();
            checkMessage();
            // 数据统计
            getSystem(SystemAnalyze.class).analyzeUserAction("main_menu", "6", null);
            getActivity().findViewById(R.id.activity_main_actonbar).setVisibility(View.GONE);
        }
    }

    private void initView() {
        mQianDao = (Button) mRoot.findViewById(R.id.personal_btn_qiandao);
        mQianDao.setOnClickListener(this);
        initPersonalInfoView();
        initGridView();
    }

    private void initGuide() {
        if (isFirstInto) {
            ((MainActivity) getActivity()).initFristGuide(3);
            isFirstInto = false;
            getSystem(SystemPreference.class).setFirstIntoPersonal(isFirstInto);
        }
    }

    private void initGridView() {
        mGridView = (GridView) mRoot.findViewById(R.id.personal_gridView);
        initList();
        mGridView.setAdapter(new MyAdapter(mList));
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                jump(position);
            }
        });
    }

    private void jump(int position) {
        if (position < 5 && !getSystem(SystemUser.class).isLogin()) {
            getSystem(SystemCommon.class).noticeAndLogin(getActivity());
            return;
        }
        switch (position) {
            // 消息中心
            case 0:
                getSystem(SystemAnalyze.class).analyzeUserAction("mine", "1", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityChatList.class);
                break;
            // 我的抽奖
            case 1:
                getSystem(SystemAnalyze.class).analyzeUserAction("mine", "2", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityMyPresent.class);
                break;
            // 我的订阅
            case 2:
                getSystem(SystemAnalyze.class).analyzeUserAction("mine", "3", "");
                FrameworkUtils.skipActivity(getActivity(), ActivitySelfMyOrder.class);
                break;
            // 我的守护
            case 3:
                if (getSystem(SystemUser.class).checkHasGuradAnchor()) {
                    getSystem(SystemAnalyze.class).analyzeUserAction("mine", "4", "");
                    Bundle bundle = new Bundle();
                    bundle.putString("anchor_id",
                            getSystem(SystemUser.class).mUser.guard_anchor);
                    FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
                } else {
                    showToast("请先守护主播");
                }
                break;
            // 收藏管理
            case 4:
                getSystem(SystemAnalyze.class).analyzeUserAction("mine", "5", "");
                FrameworkUtils.skipActivity(getActivity(), ActitvityFavorites.class);
                break;
            // 缓存管理
            case 5:
                getSystem(SystemAnalyze.class).analyzeUserAction("mine", "6", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityNewMyCache.class);
                break;
            // 历史记录
            case 6:
                getSystem(SystemAnalyze.class).analyzeUserAction("mine", "7", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityHistory.class);
                break;
            // 设置
            case 7:
                getSystem(SystemAnalyze.class).analyzeUserAction("mine", "8", "");
                FrameworkUtils.skipActivity(getActivity(), ActivitySetup.class);
                break;
            // 帮助中心
            case 8:
                getSystem(SystemAnalyze.class).analyzeUserAction("mine", "9", "");
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.feixiong.tv/sm/gnsm.html");
                bundle.putString("title", "帮助中心");
                bundle.putBoolean("share_enable", false);
                FrameworkUtils.skipActivity(getActivity(), ActivityWebView.class, bundle);
                break;
            default:
                break;
        }
    }

    private void initList() {
        mList = new ArrayList<PersonalChoose>();
        mList.add(new PersonalChoose("消息中心", R.drawable.message_center));
        mList.add(new PersonalChoose("我的抽奖", R.drawable.my_present));
        mList.add(new PersonalChoose("我的订阅", R.drawable.my_describe));
        mList.add(new PersonalChoose("我的守护", R.drawable.my_protection));
        mList.add(new PersonalChoose("收藏管理", R.drawable.favorite_manager));
        mList.add(new PersonalChoose("缓存管理", R.drawable.cache_manager));
        mList.add(new PersonalChoose("历史记录", R.drawable.history_manager));
        mList.add(new PersonalChoose("设置", R.drawable.setup_manager));
        mList.add(new PersonalChoose("帮助中心", R.drawable.help_center));
    }

    /**
     * 初始化用户信息的UI
     */
    private void initPersonalInfoView() {

        mUserValue = (GridView) mRoot.findViewById(R.id.user_value);
        if (mUserAdapter == null) {
            mUserAdapter = new MyUserAdapter();
        }
        mUserValue.setAdapter(mUserAdapter);
        mUserValue.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!getSystem(SystemUser.class).isLogin()) {
                    getSystem(SystemCommon.class).noticeAndLogin(getActivity());
                    return;
                }
                switch (position) {
                    // 我的等级
                    case 0:
                        FrameworkUtils.skipActivity(getActivity(), ActivityMyLevel.class);
                        break;
                    // 我的饼干
                    case 1:
                        FrameworkUtils.skipActivity(getActivity(), ActivityMyBiscuit.class);
                        break;
                    // 我的熊掌
                    case 2:
                        FrameworkUtils.skipActivity(getActivity(), ActivityMyWolfSkin.class);
                        break;
                    default:
                        break;
                }
            }
        });
        mUserName = (TextView) mRoot.findViewById(R.id.fragment_personal_user_name);
        mUserPic = (ImageView) mRoot.findViewById(R.id.fragment_personal_user_pic);
        mRoot.findViewById(R.id.fragment_personal_user_rela).setOnClickListener(this);

    }

    /**
     * 判断用户状态更改UI
     */
    private void judgeUserState() {
        getUserInfo();
        if (!getSystem(SystemUser.class).isLogin()) {
            mQianDao.setVisibility(View.GONE);
            mUserName.setText("请登录");
            mUserPic.setImageResource(R.drawable.ic_launcher);
        }else{
            initUserBaseInfo();
        }
        initGuide();
    }

    private void initUserBaseInfo() {
        User user=getSystem(SystemUser.class).mUser;
        if(user==null) return;
        mUserName.setText(user.nickname);
        String avatarUrl = user.image;
        //SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(avatarUrl, mUserPic);
        getSystem(SystemCommon.class).displayDefaultImage(getActivity(), mUserPic, avatarUrl);
        mQianDao.setVisibility(View.VISIBLE);
        if (user.sign_status.equals("0")) {
            mQianDao.setText("签到");
            mQianDao.setEnabled(true);
            mQianDao.setBackgroundResource(R.drawable.shape_rectangle_circular_main);
        } else {
            mQianDao.setEnabled(false);
            mQianDao.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
            mQianDao.setText("已签到");
        }
    }

    private void getUserInfo() {
        getSystem(SystemUser.class).getUserInfo(new IUserBusynessCallBack() {

            @Override
            public void onResult(boolean result, String arg) {
                if (result) {
                    Gson gson = new Gson();
                    User user = gson.fromJson(arg, User.class);
                    if (user != null) {
                        getSystem(SystemUser.class).mUser = user;
                        initUserBaseInfo();
                        if (mUserAdapter == null) {
                            mUserAdapter = new MyUserAdapter();
                        }
                        mUserAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!getSystem(SystemUser.class).isLogin()) {
            FrameworkUtils.skipActivity(getActivity(), ActivityLogin.class);
            return;
        }
        switch (v.getId()) {
            case R.id.fragment_personal_user_rela:
                FrameworkUtils.skipActivity(getActivity(), ActivityPersonalInformation.class);
                break;
            case R.id.personal_btn_qiandao:
                if (getSystem(SystemUser.class).mUser.sign_status.equals("0")) {
                    qianDao();
                }
                break;
            default:
                break;
        }
    }

    private void qianDao() {
        JsonObject params = new JsonObject();
        Utils.showProgressDialog(getActivity());
        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_signIn, params), "sign", false, false, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data, Response resp) {
                showToast(resp.msg);
                getSystem(SystemUser.class).mUser.sign_status = "1";
                mQianDao.setText("已签到");
                mQianDao.setEnabled(false);
                mQianDao.setBackgroundResource(R.color.text_color_gray);
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(getActivity(), resp.msg);
            }

            @Override
            public void onComplete() {
                judgeUserState();
                Utils.dismissProgressDialog();
            }
        });

    }

    class MyAdapter extends BaseListGridAdapter<PersonalChoose> {
        private int width;

        public MyAdapter(List<PersonalChoose> listData) {
            super(listData);
            width = (FrameworkUtils.getScreenWidth(getActivity()) - FrameworkUtils.dip2px(getActivity(), 2)) / 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_personal2, parent, false);
            }
            TextView tView = (TextView) convertView.findViewById(R.id.name);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.photo);
            imageView.setTag("img" + position);
            tView.setText(mList.get(position).getName());
            imageView.setImageResource(mList.get(position).getSource());

            if (convertView.getLayoutParams() != null) {
                ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.width = width;
                params.height = width;
                convertView.requestLayout();
            }

            return convertView;
        }
    }

    class MyUserAdapter extends BaseListGridAdapter {

        public MyUserAdapter() {
            super(null);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_user_value, null);
            }
            TextView value = (TextView) convertView.findViewById(R.id.value);
            TextView valueName = (TextView) convertView.findViewById(R.id.value_name);
            User user=getSystem(SystemUser.class).mUser;
            if (position == 0) {
                mLevel = value;
                if (getSystem(SystemUser.class).isLogin()) {
                    value.setText("LV" + user.level);
                } else {
                    value.setText("LV0");
                }
                valueName.setText("我的等级");
            }
            if (position == 1) {
                mBiscuit = value;
                if (getSystem(SystemUser.class).isLogin()) {
                    value.setText(user.currency);
                } else {
                    value.setText("0");
                }
                valueName.setText("我的饼干");
            }
            if (position == 2) {
                mWolfskin = value;
                if (getSystem(SystemUser.class).isLogin()) {
                    value.setText("" + user.paw);
                } else {
                    value.setText("0");
                }
                valueName.setText("我的熊掌");
            }

            return convertView;
        }
    }
}
