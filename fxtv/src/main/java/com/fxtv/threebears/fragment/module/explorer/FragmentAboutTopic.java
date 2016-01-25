package com.fxtv.threebears.fragment.module.explorer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.explorer.ActivityTopicInfo;
import com.fxtv.threebears.model.HotChatBanner;
import com.fxtv.threebears.model.TopicInfo;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 发现--热聊话题--相关
 */
public class FragmentAboutTopic extends BaseFragment {

    private XListView mListView;
    private TopicInfo mTopicInfo;
    private int mPageNum;
    private int mPageSize = 20;
    private MyAdapter mAdapter;
    cancelFragmentListener cancelFragment;
    public FragmentAboutTopic(){}

    public static FragmentAboutTopic newInstance(TopicInfo topicInfo){
        FragmentAboutTopic hotAboutFragment=new FragmentAboutTopic();
        Bundle bundle=new Bundle();
        bundle.putSerializable("topicInfo",topicInfo);
        hotAboutFragment.setArguments(bundle);
        return hotAboutFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTopicInfo = (TopicInfo) getArguments().getSerializable("topicInfo");
        mLayoutInflater = inflater;
        return inflater.inflate(R.layout.activity_explorer_hot_about,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getData(false, true);
    }

    private void initView() {
        initListView();
        getView().findViewById(R.id.view_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelFragment==null && getActivity() instanceof cancelFragmentListener){
                    cancelFragment=(cancelFragmentListener)getActivity();
                }
                if(cancelFragment!=null){
                    cancelFragment.cancelFragment();
                }
            }
        });
    }

    private void getData(final boolean fromRefresh, final boolean showDialog) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", mTopicInfo.id);
        params.addProperty("page", mPageNum++);
        params.addProperty("pagesize", mPageSize + "");
        if (showDialog) {
            Utils.showProgressDialog(getActivity());
        }
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_relatedTopic, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getActivity(), url, "getHotChatAbout", false, false, new RequestCallBack<List<HotChatBanner>>() {
            @Override
            public void onSuccess(List<HotChatBanner> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (mAdapter == null) {
                        mAdapter = new MyAdapter();
                    }
                    if (fromRefresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    mListView.noMoreData();
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                mListView.stopLoadMore();
                mListView.stopRefresh();
                Utils.dismissProgressDialog();
            }
        });
    }

    private void initListView() {
        mListView = (XListView) getView().findViewById(R.id.hot_about_listview);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setEmptyText(getString(R.string.empty_str_topic_about));
        mListView.setEmptyDrawable(R.drawable.empty_topic);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                try {
                    bundle.putString("id", mAdapter.getItem(position - 1).id);
                    bundle.putString("title", mAdapter.getListData().get(position - 1).title);
                    FrameworkUtils.skipActivity(getActivity(), ActivityTopicInfo.class, bundle);
                    getActivity().finish();
                } catch (Exception e) {
                    showToast("未知错误");
                }
            }
        });
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mListView.setRefreshTime("刚刚");
                mPageNum = 0;
                getData(true, false);
            }

            @Override
            public void onLoadMore() {
                getData(false, false);
            }
        });
        if (mAdapter == null) {
            mAdapter = new MyAdapter();
        }
        mListView.setAdapter(mAdapter);

    }

    class MyAdapter extends BaseListGridAdapter<HotChatBanner> {

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
            getSystem(SystemCommon.class).displayDefaultImage(FragmentAboutTopic.this, holder.img, hotChatBanner.image, SystemCommon.SQUARE);
            return convertView;
        }

        class Holder {
            ImageView img;
            TextView title;
            TextView num;

        }
    }

    public interface cancelFragmentListener{
        void cancelFragment();
    }
}
