package com.fxtv.threebears.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.activity.self.ActivitySelfMyOrder;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

public class FragmentTabSelf extends BaseFragment {
    private XListView mListView;
    private MyAdapter mAdapter;
    private int mPageNum;
    private boolean mIsAll = true;
    private int mPageSize = 30;
    private TextView mActionBarLeftTextView;
    private boolean isHiden = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_tab_self, container, false);
        initView();
        if (getSystem(SystemUser.class).isLogin()) {
            getData(false, true);
        }
        Logger.d("TAG", "FragmentTabSelf onCreateView ==");
        return mRoot;
    }

    private void initView() {
        mActionBarLeftTextView= (TextView) getActivity().findViewById(R.id.ab_left_tv);
        initListView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.d("TAG", "self onHiddenChanged = " + hidden);
        isHiden = hidden;
        if (!hidden) {
            getData(true, true);
            // 数据统计
            getSystem(SystemAnalyze.class).analyzeUserAction("main_menu", "3", null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHiden) {
            getData(false, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public OnClickListener clickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            mIsAll = !mIsAll;
            mListView.setRefreshTime("刚刚");
            mPageNum = 0;
            Utils.showProgressDialog(getActivity());
            getData(true, true);
        }
    };

    public OnClickListener order_click= new OnClickListener() {
        @Override
        public void onClick(View v) {
            FrameworkUtils.skipActivity(getActivity(), ActivitySelfMyOrder.class);
        }
    };

    private void initListView() {
        mListView = (XListView) mRoot.findViewById(R.id.lv);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setPageSize(30);
        mListView.setEmptyText(getString(R.string.empty_str_self));
        mListView.setEmptyDrawable(R.drawable.empty_order);
        if (mAdapter == null) {
            mAdapter = new MyAdapter(null);
        }
        mListView.setAdapter(mAdapter);
//		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mAdapter.getItem(position - 1).id);
                bundle.putString("skipType", "31");
                FrameworkUtils.skipActivity(getActivity(), ActivityVideoPlay.class, bundle);
            }
        });
        mListView.setXListViewListener(new IXListViewListener() {
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
    }

    class MyAdapter extends BaseListGridAdapter<Video> {

        public MyAdapter(List<Video> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_video, null);
                viewHolder = new ViewHolder();
                viewHolder.imgJiang = (ImageView) convertView.findViewById(R.id.present_icon);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
                viewHolder.imgdownload = (ImageView) convertView.findViewById(R.id.down);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.name = (TextView) convertView.findViewById(R.id.lable1);
                viewHolder.time = (TextView) convertView.findViewById(R.id.lable2);
                viewHolder.author = (TextView) convertView.findViewById(R.id.lable3);
                viewHolder.lastTime = (TextView) convertView.findViewById(R.id.lable4);
                viewHolder.logo = (ImageView) convertView.findViewById(R.id.logo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Video video = getItem(position);
            if (video.lottery_status.equals("1")) {
                viewHolder.imgJiang.setVisibility(View.VISIBLE);
                viewHolder.logo.setVisibility(View.GONE);
            } else {
                viewHolder.imgJiang.setVisibility(View.GONE);
                viewHolder.logo.setVisibility(View.VISIBLE);
            }
            viewHolder.title.setText(video.title);
            viewHolder.name.setText(video.game_title);
            viewHolder.time.setText(video.duration);
            viewHolder.author.setText(video.anchor_name);
            viewHolder.lastTime.setText(video.publish_time);
            /*SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, viewHolder.img);*/

            getSystem(SystemCommon.class).displayDefaultImage(FragmentTabSelf.this, viewHolder.img, video.image);
            final ImageView imgdownload = viewHolder.imgdownload;
            viewHolder.imgdownload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideoChoose = video;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(getActivity(), mVideoChoose, mImageDownLoad);
                }
            });
            viewHolder.author.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("anchor_id", video.anchor_id);
                    bundle.putString("skipType", "32");
                    FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
                }
            });
            if (getSystem(SystemDownloadVideoManager.class).isDownloaded(video.id))
                viewHolder.imgdownload.setImageResource(R.drawable.icon_download1);
            else {
                viewHolder.imgdownload.setImageResource(R.drawable.icon_download0);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView imgJiang;
            ImageView imgdownload;
            ImageView img;
            ImageView logo;
            TextView title;
            TextView name;
            TextView time;
            TextView author;
            TextView lastTime;
        }
    }

    private void getData(final boolean fromRefresh, boolean showDialog) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("page", mPageNum + "");
        if (mIsAll) {
            params.addProperty("type", "");
        } else {
            params.addProperty("type", "UGC");
        }
        params.addProperty("pagesize", mPageSize + "");
        boolean usecache = !fromRefresh;
        if (showDialog) {
            Utils.showProgressDialog(getActivity());
        }

        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.MINE, ApiType.MINE_myVideo, params), "selfVideosOfSelf", false, false, new RequestCallBack<List<Video>>() {

            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (fromRefresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (fromRefresh) {
                        mAdapter.setListData(null);//清空数据
                    }
                    FrameworkUtils.showToast(getActivity(), getString(R.string.notice_no_more_data));
                }
                if (mIsAll) {
                    mActionBarLeftTextView.setText("只看主播");
                } else {
                    mActionBarLeftTextView.setText("查看全部");
                }
            }

            @Override
            public void onFailure(Response resp) {
                mIsAll = !mIsAll;
                if (fromRefresh) {
                    mAdapter.setListData(null);
                }
            }

            @Override
            public void onComplete() {
                mListView.stopLoadMore();
                mListView.stopRefresh();
                mAdapter.notifyDataSetChanged();
                Utils.dismissProgressDialog();
            }
        });


    }

    private Video mVideoChoose;
    private ImageView mImageDownLoad;

}
