package com.fxtv.threebears.fragment.module.explorer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.HeaderGridView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.explorer.ActivityTopicInfo;
import com.fxtv.threebears.model.HotChatBanner;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.banner.BannerLayout;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshHeaderGridView;

import java.util.List;

/**
 * 热聊话题--热门
 */
public class FragmentExplorerHot extends BaseFragment {

    private PullToRefreshHeaderGridView mRefreshGridView;
    private List<HotChatBanner> mBannerData, mListData;
    private int mPageNum;
    private int mPageSize = 20;
    private MyGridAdapter mGridAdapter;
    private String TAG = "FragmentExplorerHot";
    private BannerLayout mBanner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_explorer_hot, container, false);
        initView();
        getBannerData(false);
        return mRoot;
    }

    private void getBannerData(final boolean fromRefresh) {
        Utils.showProgressDialog(getActivity());
        JsonObject params = new JsonObject();
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_hotTopic, params);
        getSystem(SystemHttp.class).get(getActivity(), url, "getHotChatBanner", true, true, new RequestCallBack<List<HotChatBanner>>() {
            @Override
            public void onSuccess(List<HotChatBanner> data, Response resp) {
                mBannerData = data;
                if (mBanner != null) {
                    for (int i = 0; i < mBannerData.size(); i++) {
                        mBannerData.get(i).type = "18";
                        mBannerData.get(i).link = mBannerData.get(i).id;
                    }
                    mBanner.setBannerData(mBannerData);
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                getListData(fromRefresh);
            }
        });
//        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "getHotChatBanner", true, true, callBack);
//        getSystemHttpRequests().getHotChatBanner(getActivity(), params.toString(), new SystemHttp.RequestCallBack2() {
//            @Override
//            public void onSuccess(String json, boolean fromCache, String msg) {
//                Gson gson = new Gson();
//                mBannerData = gson.fromJson(json, new TypeToken<List<HotChatBanner>>() {
//                }.getType());
//                if (mBanner != null) {
//                    for (int i = 0; i < mBannerData.size(); i++) {
//                        mBannerData.get(i).type = "18";
//                        mBannerData.get(i).link = mBannerData.get(i).id;
//                    }
//                    mBanner.setBannerData(mBannerData);
//                }
//
//            }
//
//            @Override
//            public void onFailure(String msg, boolean fromCache) {
//                showToast(msg);
//            }
//
//            @Override
//            public void onComplete() {
//                getListData(false);
//            }
//        });
    }

    public void getListData(final boolean fromRefresh) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("page", "" + mPageNum);
        params.addProperty("pagesize", mPageSize + "");
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_topic, params);
        getSystem(SystemHttp.class).get(getActivity(), url, "getHotChatList", false, false, new RequestCallBack<List<HotChatBanner>>() {
            @Override
            public void onSuccess(List<HotChatBanner> data, Response resp) {
                mListData = data;
                if (mGridAdapter == null) {
                    mGridAdapter = new MyGridAdapter();
                }
                if (fromRefresh) {
                    mGridAdapter.setListData(mListData);
                } else {
                    mGridAdapter.addData(mListData);
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
                if (mRefreshGridView != null) {
                    mRefreshGridView.onRefreshComplete();
                }
            }
        });
    }

    private void initView() {
        initPrefreshGridView();
    }

    private void initPrefreshGridView() {
        mRefreshGridView = (PullToRefreshHeaderGridView) mRoot.findViewById(R.id.fragment_tab_main_first_gv);
        initHear();
        mGridAdapter = new MyGridAdapter();
        mRefreshGridView.setAdapter(mGridAdapter);
        mRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HeaderGridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                mPageNum = 0;
                getBannerData(true);
                getListData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                getListData(false);
            }
        });
        mRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                try {
                    bundle.putString("id", mGridAdapter.getListData().get(position - 1).id);
                    bundle.putString("title", mGridAdapter.getListData().get(position - 1).title);
                    FrameworkUtils.skipActivity(getActivity(), ActivityTopicInfo.class, bundle);
                } catch (Exception e) {
                    showToast("未知错误");
                }
            }
        });
    }


    private void initHear() {
        HeaderGridView headerGridView = mRefreshGridView.getRefreshableView();
        mRefreshGridView.setMode(PullToRefreshBase.Mode.BOTH);
        View header = mLayoutInflater.inflate(R.layout.view_banner, null);
        mBanner = (BannerLayout) header.findViewById(R.id.banner);
        int screenWidth = FrameworkUtils.getScreenWidth(getActivity());
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBanner.getLayoutParams();
        layoutParams.height = screenWidth / 16 * 9;
        layoutParams.bottomMargin = FrameworkUtils.dip2px(getActivity(), 0);
        mBanner.setLayoutParams(layoutParams);
        mBanner.setBannerData(mBannerData);
        headerGridView.addHeaderView(header);
    }

    class MyGridAdapter extends BaseListGridAdapter<HotChatBanner> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_hot_chat, null);
                holder = new Holder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.num = (TextView) convertView.findViewById(R.id.num);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            HotChatBanner hotChatBanner = getItem(position);
            holder.title.setText(hotChatBanner.title);
            holder.num.setText(hotChatBanner.join_num + "条吐槽");
//            getSystem(SystemCommon.class).displayDefaultImage(FragmentExplorerHot.this, holder.img, hotChatBanner.cate_image, SystemCommon.SQUARE);
            getSystem(SystemCommon.class).displayRoundedImage(FragmentExplorerHot.this, holder.img, hotChatBanner.cate_image, SystemCommon.SQUARE, 10);
            return convertView;
        }

        class Holder {
            ImageView img;
            TextView title;
            TextView num;
        }
    }

}
