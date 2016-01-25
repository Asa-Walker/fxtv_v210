package com.fxtv.threebears.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.fxtv.threebears.activity.anchor.ActivityAnchorSet;
import com.fxtv.threebears.activity.explorer.ActivityEcplorerVideoCenter;
import com.fxtv.threebears.activity.explorer.ActivityExplorerAnchorCircle;
import com.fxtv.threebears.activity.explorer.ActivityExplorerH5Gams;
import com.fxtv.threebears.activity.explorer.ActivityExplorerHotChat;
import com.fxtv.threebears.activity.explorer.ActivityExplorerMyCookie;
import com.fxtv.threebears.activity.explorer.ActivityExplorerTask;
import com.fxtv.threebears.activity.explorer.ActivityFeiXiongCup;
import com.fxtv.threebears.activity.explorer.ActivityHotVote;
import com.fxtv.threebears.activity.explorer.ActivityQRCode;
import com.fxtv.threebears.activity.explorer.ActivityRankList;
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.model.Action;
import com.fxtv.threebears.model.PersonalChoose;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemConfig;
import com.fxtv.threebears.system.SystemPreference;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentTabExplorer extends BaseFragment implements OnClickListener {
    private List<Action> mList;
    private List<PersonalChoose> mChooses;
    private GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_tab_explorer, container, false);
        initView();
        return mRoot;
    }

    private void initView() {
        initGridView();
    }

    private void initFirstGuide() {
        if (getSystem(SystemPreference.class).isFirstIntoExplorer()) {
            ((MainActivity) getActivity()).initFristGuide(2);
            getSystem(SystemPreference.class).setFirstIntoExplorer(false);
        }
    }

    private void initGridView() {
        mGridView = (GridView) mRoot.findViewById(R.id.fragment_tab_explorer_gridView);
        initList();
        mGridView.setAdapter(new MyAdapter(mChooses));
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                jump(position);
            }
        });
    }

    private void jump(int position) {
        switch (position) {
            // 主播圈
            case 0:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "1", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityExplorerAnchorCircle.class);
                break;
            // 主播空间（全部主播）
            case 1:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "2", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorSet.class);
                break;
            // 排行榜
            case 2:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "4", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityRankList.class);
                break;
            // 每日任务
            case 3:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "5", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityExplorerTask.class);
                break;
            // 飞熊商城
            case 4:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "6", "");
                Bundle bundle = new Bundle();
                bundle.putString("url", getSystem(SystemConfig.class).STORE_URL);
                bundle.putString("title", "飞熊商城");
                bundle.putBoolean("share_enable", false);
                FrameworkUtils.skipActivity(getActivity(), ActivityWebView.class, bundle);
                break;
            // 热点投票
            case 5:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "7", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityHotVote.class);
                break;
            // 扫一扫
            case 6:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "8", "");
                // Intent intent = new Intent(getActivity(),
                // ActivityQRCode.class);
                // startActivityForResult(intent, 1990);
                FrameworkUtils.skipActivity(getActivity(), ActivityQRCode.class);
                break;
            // 活动中心
            case 7:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "3", "");
                Intent intent1 = new Intent(getActivity(), ActivityExplorerMyCookie.class);
                startActivityForResult(intent1, 1991);
                break;
            // 飞熊杯
            case 8:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "9", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityFeiXiongCup.class);
                break;
            //视频专题
            case 9:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "10", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityEcplorerVideoCenter.class);
                break;
            //赚饼干
            case 10:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "11", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityExplorerH5Gams.class);
                break;
            //热聊话题
            case 11:
                getSystem(SystemAnalyze.class).analyzeUserAction("discovery", "12", "");
                FrameworkUtils.skipActivity(getActivity(), ActivityExplorerHotChat.class);
                break;
            default:
                break;
        }
    }

    private void initList() {
        mChooses = new ArrayList<PersonalChoose>();
        mChooses.add(new PersonalChoose("主播圈", R.drawable.friends_circle));
        mChooses.add(new PersonalChoose("主播空间", R.drawable.all_anchor));
        mChooses.add(new PersonalChoose("排行榜", R.drawable.rank_list));
        mChooses.add(new PersonalChoose("每日任务", R.drawable.mission));
        mChooses.add(new PersonalChoose("飞熊商城", R.drawable.feixiong_mall));
        mChooses.add(new PersonalChoose("热点投票", R.drawable.hot_vote));
        mChooses.add(new PersonalChoose("扫一扫", R.drawable.qr_code));
        mChooses.add(new PersonalChoose("活动中心", R.drawable.cookies));
        mChooses.add(new PersonalChoose("飞熊杯", R.drawable.feixiong_record));
        mChooses.add(new PersonalChoose("视频专题", R.drawable.icon_video_center));
        mChooses.add(new PersonalChoose("赚饼干", R.drawable.icon_macine));
        mChooses.add(new PersonalChoose("热聊话题", R.drawable.icon_hot_chat));
    }

    private void getBannerData() {
        JsonObject params = new JsonObject();
        params.addProperty("page", 1 + "");
        params.addProperty("pagesize", 20 + "");
        params.addProperty("type", 2 + "");
        Utils.showProgressDialog(getActivity());
        if (mList == null) {
            mList = new ArrayList<>();
        }
        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.FIND, ApiType.FIND_activityCenter, params), "getActionBanner", true, true, new RequestCallBack<List<Action>>() {
            @Override
            public void onSuccess(List<Action> data, Response resp) {
                if (data != null && data.size() != 0) {
                    mList = data;
                    initBanner();
                }
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(getActivity(), resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    private void initBanner() {
       /* mBanner = (BannerLayout) mRoot.findViewById(R.id.fragment_tab_main_first_header_banner);
        int screenWidth = FrameworkUtils.getScreenWidth(getActivity());
        LayoutParams layoutParams = mBanner.getLayoutParams();
        layoutParams.height = screenWidth / 18 * 9;
        mBanner.setLayoutParams(layoutParams);
        mBanner.setBannerData(mList);*/
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initFirstGuide();
            // 数据统计
            getSystem(SystemAnalyze.class).analyzeUserAction("main_menu", "4", null);
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if (requestCode == 1990 && resultCode == 10) {
        // String QRCode = data.getStringExtra("result");
        // if (!TextUtils.isEmpty(QRCode)) {
        // // if (QRCode.contains("FXTV240W")) {
        // // sendToQRCode(QRCode);
        // // } else
        // if (QRCode.contains("FXTV_LOGIN")) {
        // Logger.d("debug", "Qrcode=" + QRCode);
        // temp(QRCode);
        // } else {
        // if (URLUtil.isValidUrl(QRCode)) {
        // Intent intent = new Intent(Intent.ACTION_VIEW,
        // (Uri.parse(QRCode))).addCategory(
        // Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);
        // } else {
        // showToast("扫描的二维码无效");
        // }
        // }
        // }
        // }
        // 为了跳回"我的"fragment
        if (requestCode == 1991 && resultCode == 20) {
            ((MainActivity) getActivity()).jump2Child(4);
        }
    }

    private void temp(String code) {
        JsonObject params = new JsonObject();
        params.addProperty("qmtt", code);
        Utils.showProgressDialog(getActivity());

        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.TV, ApiType.TV_qrcodeLogin, params), "tvScanLogin", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                Logger.d("debug", "onSuccess,json=" + data);
                showToast("成功登录TV端");
            }

            @Override
            public void onFailure(Response resp) {
                Logger.d("debug", "onFailure,msg=" + resp.msg);
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    class MyAdapter extends BaseListGridAdapter<PersonalChoose> {

        private int width, height;


        public MyAdapter(List<PersonalChoose> listData) {
            super(listData);
            width = (FrameworkUtils.getScreenWidth(getActivity()) - FrameworkUtils.dip2px(getActivity(), 2)) / 3;
            height = width;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_personal2, parent, false);
            }
            TextView tView = (TextView) convertView.findViewById(R.id.name);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.photo);
            tView.setText(mChooses.get(position).getName());
            imageView.setImageResource(mChooses.get(position).getSource());
            if (convertView.getLayoutParams() != null) {
                ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.width = width;
                params.height = height;
                convertView.requestLayout();
            }
            return convertView;
        }
    }
}
