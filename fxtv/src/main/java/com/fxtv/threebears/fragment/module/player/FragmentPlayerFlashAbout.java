package com.fxtv.threebears.fragment.module.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FXTV-Android
 *         <p/>
 *         视频播放界面---->相关tab
 */
public class FragmentPlayerFlashAbout extends BaseFragment {
    private Video mVideo;
    private List<Video> mList;
    private int mPageNum;
    private final int mPageSize = 20;
    private PullToRefreshGridView mPullRefreshGridView;
    private MyAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_play_page_aboutlist, container, false);
        // mVideo = ((ActivityVideoPlay) getActivity()).getVideo();
        mVideo = (Video) getArguments().getSerializable("video");
        initView();
        getData(false, true);
        return mRoot;
    }

    private void initView() {
        // 隐藏自身
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).getTransaction(getActivity())
                        .hide(FragmentPlayerFlashAbout.this).commit();
                ((ActivityVideoPlay) getActivity()).setFragmentPos(0);
            }
        });
        // 阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mPullRefreshGridView = (PullToRefreshGridView) mRoot.findViewById(R.id.fragment_tab_game_gv);
        GridView gridView = mPullRefreshGridView.getRefreshableView();
        mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                getData(true, false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                getData(false, false);
            }
        });
        mAdapter = new MyAdapter();
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().finish();
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mList.get(position).id);
                FrameworkUtils.skipActivityAndFinish(getActivity(), ActivityVideoPlay.class, bundle);
                getActivity().finish();
            }
        });
    }

    private void getData(final boolean isRefresh, final boolean showDialog) {
        if (isRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", mVideo.id);
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", mPageSize + "");
        if (mList == null) {
            mList = new ArrayList<Video>();
        }
        if (showDialog) {
            Utils.showProgressDialog(getActivity());
        }
        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.BASE, ApiType.BASE_relatedVideo, params), "mainVideosOfVideoOfAboutApi", true, true, new RequestCallBack<List<Video>>() {
            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (isRefresh) {
                        mList.clear();
                        mList = data;
                    } else {
                        mList.addAll(data);
                    }
                    if (mAdapter == null) {
                        mAdapter = new MyAdapter();
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    FrameworkUtils.showToast(getActivity(), getString(R.string.notice_no_more_data));
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                if (mPullRefreshGridView != null) {
                    mPullRefreshGridView.onRefreshComplete();
                }
                Utils.dismissProgressDialog();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPullRefreshGridView = null;
        mAdapter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FrameworkUtils.setEmptyList(mList);
    }

    class MyAdapter extends BaseAdapter {
        private int height;

        private MyAdapter() {
            int width = (FrameworkUtils.getScreenWidth(getActivity()) - FrameworkUtils.dip2px(getActivity(), 8) * 3) / 2;
            height = width / 16 * 9;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_gv_video, null);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.download = (ImageView) convertView.findViewById(R.id.down);
                holder.prize = (ImageView) convertView.findViewById(R.id.prize);
                holder.name = (TextView) convertView.findViewById(R.id.lable1);
                holder.durtion = (TextView) convertView.findViewById(R.id.lable2);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.logo = (ImageView) convertView.findViewById(R.id.logo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LayoutParams layoutParams = holder.img.getLayoutParams();
            layoutParams.height = height;
            holder.img.setLayoutParams(layoutParams);
            final Video video = mList.get(position);
//            SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, holder.img);
            SystemManager.getInstance().getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerFlashAbout.this, holder.img, video.image);
//            if (video.lottery_status.equals("1")) {
//                holder.prize.setVisibility(View.VISIBLE);
//                holder.logo.setVisibility(View.GONE);
//            } else {
//                holder.prize.setVisibility(View.GONE);
//                holder.logo.setVisibility(View.VISIBLE);
//            }
            Utils.setVideoLogo(holder.prize, holder.logo, video.lottery_status);
            holder.name.setText(video.game_title);
            holder.durtion.setText(video.duration);
            holder.title.setText(video.title);
            final ImageView imgdownload = holder.download;
            holder.download.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideoChoose = video;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(getActivity(), mVideoChoose, mImageDownLoad);
                }
            });
            if (getSystem(SystemDownloadVideoManager.class).isDownloaded(video.id))
                holder.download.setImageResource(R.drawable.icon_download1);
            else {
                holder.download.setImageResource(R.drawable.icon_download0);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            ImageView prize;
            ImageView download;
            ImageView logo;
            TextView durtion;
            TextView name;
            TextView title;
        }
    }

    private Video mVideoChoose;
    private ImageView mImageDownLoad;

}
