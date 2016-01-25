package com.fxtv.threebears.fragment.module.explorer;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.explorer.ActivityTopicInfo;
import com.fxtv.threebears.model.HotChatBanner;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.pullToRefreshSwipeMenuListView.PullToRefreshSwipeMenuListView;
import com.fxtv.threebears.view.pullToRefreshSwipeMenuListView.SwipeMenu;
import com.fxtv.threebears.view.pullToRefreshSwipeMenuListView.SwipeMenuCreator;
import com.fxtv.threebears.view.pullToRefreshSwipeMenuListView.SwipeMenuItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 发现页--热聊话题--关注
 */
public class FragmentExplorerFocus extends BaseFragment {

    private PullToRefreshSwipeMenuListView mRefreshGridView;
    private List<HotChatBanner> mListData;
    private int mPageNum;
    private int mPageSize = 20;
    private MyGridAdapter mGridAdapter;
    private boolean dontGetNetData = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_explorer_focus, container, false);
        initView();
//        getListData(false, true);
        return mRoot;
    }

    private void getListData(final boolean fromRefresh, final boolean showDialog) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        if (showDialog) {
            Utils.showProgressDialog(getActivity());
        }
        JsonObject params = new JsonObject();
        params.addProperty("page", "" + mPageNum);
        params.addProperty("pagesize", mPageSize + "");
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_followTopic, params);
        getSystem(SystemHttp.class).get(getActivity(), url, "getHotFocusList", false, false, new RequestCallBack<List<HotChatBanner>>() {
            @Override
            public void onSuccess(List<HotChatBanner> data, Response resp) {
//                mRoot.findViewById(R.id.no_focus).setVisibility(View.GONE);
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
                if (!"没有更多内容".equals(resp.msg)) {
                    if (!dontGetNetData) {
                        showToast(resp.msg);
                    }
                }
                if (mPageNum < 2) {
//                    mRoot.findViewById(R.id.no_focus).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
                mRefreshGridView.stopLoadMore();
                mRefreshGridView.stopRefresh();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        dontGetNetData = hidden;
    }

    @Override
    public void onResume() {
        super.onResume();
        getListData(true, true);
    }

    private void initView() {
        initPrefreshGridView();
    }

    private void initPrefreshGridView() {
        mRefreshGridView = (PullToRefreshSwipeMenuListView) mRoot.findViewById(R.id.swiplv);
        mGridAdapter = new MyGridAdapter();
        mRefreshGridView.setAdapter(mGridAdapter);
        mRefreshGridView.setPullRefreshEnable(true);
        mRefreshGridView.setPullLoadEnable(true);
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
        mRefreshGridView.setXListViewListener(new PullToRefreshSwipeMenuListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mRefreshGridView.setRefreshTime("刚刚");
                mPageNum = 0;
                getListData(true, false);
            }

            @Override
            public void onLoadMore() {
                getListData(false, false);
            }
        });
        initWipeMenuCraetor();

        mRefreshGridView.setOnMenuItemClickListener(new PullToRefreshSwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                cancelFocus(position);
            }
        });
        View empytView = mRoot.findViewById(R.id.view_empty);
        TextView tv_empty = (TextView) empytView.findViewById(R.id.tv_empty);
        tv_empty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.empty_topic, 0, 0);
        tv_empty.setText(String.format("你还没有关注的话题，快去关注吧!"));
        mRefreshGridView.setEmptyView(empytView);
    }

    /**
     * 取消关注
     *
     * @param position
     */
    private void cancelFocus(final int position) {
        Utils.showProgressDialog(getActivity());
        JsonObject json = new JsonObject();
        JsonObject follow = new JsonObject();
        follow.addProperty("id", mListData.get(position).id);
        //1，关注；0,取消关注
        follow.addProperty("status", "0");
        JsonArray array = new JsonArray();
        array.add(follow);
        json.add("follow", array);
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_topic_follow, json);
        getSystem(SystemHttp.class).get(getActivity(), url, "topic_follow", false, false, new RequestCallBack() {
            @Override
            public void onSuccess(Object data, Response resp) {
                showToast(resp.msg);
                mGridAdapter.getListData().remove(position);
                if (mGridAdapter.getListData().size() == 0) {
//                    mRoot.findViewById(R.id.no_focus).setVisibility(View.VISIBLE);
                }
                mGridAdapter.setListData(mListData);
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });
    }

    private void initWipeMenuCraetor() {
        SwipeMenuCreator swipcreater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
                //set title
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        mRefreshGridView.setMenuCreator(swipcreater);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
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
//            getSystem(SystemCommon.class).displayDefaultImage(FragmentExplorerFocus.this, holder.img, hotChatBanner.cate_image, SystemCommon.SQUARE);
            getSystem(SystemCommon.class).displayRoundedImage(FragmentExplorerFocus.this, holder.img, hotChatBanner.cate_image, SystemCommon.SQUARE, 10);
            return convertView;
        }

        class Holder {
            ImageView img;
            TextView title;
            TextView num;
        }
    }

}
