package com.fxtv.threebears.fragment.module.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.fxtv.framework.system.callback.RequestSimpleCallBack;
import com.fxtv.framework.widget.HeaderGridView;
import com.fxtv.threebears.MainActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.IndexBanner;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.system.SystemPreference;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.banner.BannerLayout;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshHeaderGridView;

import java.util.List;

/**
 * @author FXTV-Android
 */
public class FragmentTabMainFirst extends BaseFragment {
    private static final String TAG = "FragmentTabMainFirst";
    private BannerLayout mBanner;
    private List<IndexBanner> mBannerData;
    private PullToRefreshHeaderGridView mRefreshGridView;
    private GridViewAdapter mGridViewAdapter;
    private Activity thisActivity;
    private int mPageNum;
    private final int mPageSize = 20;
    private String game_id;
    private boolean isVisibleToUser;
    private boolean isvideoFailure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game_id = getArguments().getString("game_id");
        thisActivity = getActivity();
        Logger.d(TAG, "onCreate,index=" + " id=" + game_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_tab_main_first, container, false);
        initView();
        if (isVisibleToUser) {//当前Fragment可见时显示dialog
            Utils.showProgressDialog(thisActivity);
        }
        getBannerData(false);
        getGridData(false);
        return mRoot;
    }

    private void getGridData(final boolean fromRefresh) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", "" + game_id);
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", mPageSize + "");
        //共用
        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.INDEX, ApiType.INDEX_menuVideo,params),"menuVideo "+ game_id,!fromRefresh,true, new RequestCallBack<List<Video>>() {
            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (mGridViewAdapter == null) {
                    mGridViewAdapter = new GridViewAdapter(null);
                }
                if (data != null && data.size() != 0) {
                    if (fromRefresh) {
                        mGridViewAdapter.setListData(data);
                    } else {
                        mGridViewAdapter.addData(data);
                    }
                } else {
                    isvideoFailure = true;
                    FrameworkUtils.showToast(thisActivity, getString(R.string.notice_no_more_data));
                }
                firstGuide();
            }

            @Override
            public void onFailure(Response resp) {
                isvideoFailure = true;
                FrameworkUtils.showToast(thisActivity, resp.msg);
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

    private void firstGuide() {
        if (getSystem(SystemPreference.class).isFirstIntoMain()) {
            ((MainActivity) thisActivity).initFristGuide(1);
            getSystem(SystemPreference.class).setFirstIntoMain(false);
        }
    }

    private void getBannerData(boolean fromRefresh) {
        JsonObject params = new JsonObject();
        params.addProperty("id", game_id + "");
        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.INDEX, ApiType.INDEX_menuBanner,params),!fromRefresh, true, new RequestSimpleCallBack<List<IndexBanner>>() {

            @Override
            public void onSuccess(List<IndexBanner> data, Response resp) {
                if (data != null && mBanner!=null)
                    mBanner.setBannerData(data);
            }
        });
    }

    private void initView() {
        mRefreshGridView = (PullToRefreshHeaderGridView) mRoot.findViewById(R.id.fragment_tab_main_first_gv);
        HeaderGridView headerGridView = mRefreshGridView.getRefreshableView();
        mRefreshGridView.setMode(Mode.BOTH);
       // View header = mLayoutInflater.inflate(R.layout.fragment_tab_main_first_header, null);

        View headView=mLayoutInflater.inflate(R.layout.view_banner,null);
        mBanner = (BannerLayout)headView.findViewById(R.id.banner);

        int screenWidth = FrameworkUtils.getScreenWidth(thisActivity);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBanner.getLayoutParams();
        if(layoutParams==null) layoutParams=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        layoutParams.height = screenWidth / 16 * 9;
        layoutParams.bottomMargin = FrameworkUtils.dip2px(thisActivity, 5);
        mBanner.setBannerData(mBannerData);

        headerGridView.addHeaderView(headView);
        mRefreshGridView.setOnRefreshListener(new OnRefreshListener2<HeaderGridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                Logger.d(TAG, "onPullDownToRefresh()");
                mPageNum = 0;
                getBannerData(true);
                getGridData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                getGridData(false);
            }
        });
        if (mGridViewAdapter == null) {
            mGridViewAdapter = new GridViewAdapter(null);
        }
        mRefreshGridView.setAdapter(mGridViewAdapter);
        mRefreshGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mGridViewAdapter.getItem(position - 2).id);
                bundle.putString("skipType", "11");
                FrameworkUtils.skipActivity(thisActivity, ActivityVideoPlay.class, bundle);
            }
        });

        mBanner.setBannerClickListener(new BannerLayout.BannerClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getSystem(SystemAnalyze.class).analyzeUserAction("index", ""+(position+1), game_id);
            }
        });
    }

    class GridViewAdapter extends BaseListGridAdapter<Video> {
        private int height;

        private GridViewAdapter(List<Video> list) {
            super(list);
            int width = (FrameworkUtils.getScreenWidth(thisActivity) - FrameworkUtils.dip2px(thisActivity, 8) * 2) / 2;
            height = width / 16 * 9;
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
                holder.logo = (ImageView) convertView.findViewById(R.id.logo);
                holder.name = (TextView) convertView.findViewById(R.id.lable1);
                holder.durtion = (TextView) convertView.findViewById(R.id.lable2);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LayoutParams layoutParams = holder.img.getLayoutParams();
            layoutParams.height = height;
            holder.img.setLayoutParams(layoutParams);
            final Video video = getItem(position);
            /*SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, holder.img);*/

            getSystem(SystemCommon.class).displayDefaultImage(FragmentTabMainFirst.this, holder.img, video.image);

            Utils.setVideoLogo(holder.prize,holder.logo,video.lottery_status);

            holder.name.setText(video.game_title);
            holder.durtion.setText(video.duration);
            holder.title.setText(video.title);
            final ImageView imgdownload = holder.download;
            holder.download.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideo = video;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(getActivity(), mVideo,mImageDownLoad);
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

    private Video mVideo;
    private ImageView mImageDownLoad;



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {//true 表示可见
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        Logger.d(TAG,"setUserVisibleHint "+isVisibleToUser +" game_id"+game_id);
        if (!isVisibleToUser) {
            if (mBanner != null) {
                mBanner.stopAutoPlay();
            }
        } else {
            if (mGridViewAdapter == null || FrameworkUtils.isListEmpty(mGridViewAdapter.getListData())) {
                Logger.d(TAG, "setUserVisibleHint game_id=" + game_id + " isvideoFailure=" + isvideoFailure);
                Utils.showProgressDialog(thisActivity);
                if (isvideoFailure) {
                    getBannerData(false);
                    getGridData(false);
                }
            }
            if (mBanner != null) {
                mBanner.startAutoPlay();
            }
        }
    }

    public String getGameId() {
        return game_id;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume =" + game_id);
        mBanner.startAutoPlay();

    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d(TAG, "onStop ="+game_id);
        mBanner.stopAutoPlay();
    }

    @Override
    public void onDestroy() {
        FrameworkUtils.setEmptyList(mBannerData);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (mBanner != null){
            mBanner.destroyDrawingCache();
        }
        mBanner = null;
        if (mRefreshGridView != null)
            mRefreshGridView.destroyDrawingCache();
        mRefreshGridView = null;
        mGridViewAdapter = null;
        super.onDestroyView();
    }
}
