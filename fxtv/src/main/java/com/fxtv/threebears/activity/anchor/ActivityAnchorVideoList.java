package com.fxtv.threebears.activity.anchor;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 主播的视频列表
 *
 * @author Administrator
 */
public class ActivityAnchorVideoList extends BaseActivity {
    private XListView mListView;
    //private List<Video> mData;
    private int mPageNum;
    private String mAnchorId;
    private MyAdapter mAdapter;
    private int mPageSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_video_list);
        initView();
        mAnchorId = getStringExtra("anchor_id");
        getData(false, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        initActionBar();

        initXListView();
    }

    private void initXListView() {
        mListView = (XListView) findViewById(R.id.activity_anchor_video_list_xlistview);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setPageSize(mPageSize);
        mListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                mListView.setRefreshTime("刚刚");
                getData(true, false);
            }

            @Override
            public void onLoadMore() {
                getData(false, false);
            }
        });

        if (mAdapter == null) {
            mAdapter = new MyAdapter(null);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putString("video_id", mAdapter.getItem(position - 1).id);
                FrameworkUtils.skipActivity(ActivityAnchorVideoList.this, ActivityVideoPlay.class, bundle);
            }
        });

    }

    /**
     * 获取数据
     *
     * @param showDialog
     */
    private void getData(final boolean fromRefresh, final boolean showDialog) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", mAnchorId);
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", mPageSize + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }

        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_videoList, params);
        getSystem(SystemHttp.class).get(this, url, "anchorVideosOfAnchorApi", !fromRefresh, true, new RequestCallBack<List<Video>>() {
            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (mAdapter == null) {
                        mAdapter = new MyAdapter(data);
                    }
                    if (fromRefresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(ActivityAnchorVideoList.this,
                            getString(R.string.notice_no_more_data));
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

    private void initActionBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("视频列表");
        ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
        btnBack.setImageResource(R.drawable.icon_arrow_left1);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<Video> {

        public MyAdapter(List<Video> listData) {
            super(listData);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(ActivityAnchorVideoList.this, R.layout.item_video, null);
                holder = new Holder();

                holder.gameName = (TextView) convertView.findViewById(R.id.lable1);
                holder.duration = (TextView) convertView.findViewById(R.id.lable2);
                holder.publishTiem = (TextView) convertView.findViewById(R.id.lable3);
                convertView.findViewById(R.id.lable4).setVisibility(View.INVISIBLE);
                holder.tiltle = (TextView) convertView.findViewById(R.id.title);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.imgdownload = (ImageView) convertView.findViewById(R.id.down);
                holder.logo = (ImageView) convertView.findViewById(R.id.logo);
                holder.prize = (ImageView) convertView.findViewById(R.id.present_icon);
                convertView.setTag(holder);

            } else {
                holder = (Holder) convertView.getTag();
            }
            final Video itemData = getItem(position);
            holder.img.setImageResource(itemData.video_download_Img);
            holder.gameName.setText(itemData.game_title);
            holder.duration.setText(itemData.duration);
            holder.publishTiem.setText(itemData.publish_time);
            holder.tiltle.setText(itemData.title);

//			SystemManager.getInstance().getSystem(SystemImageLoader.class)
//					.displayImageDefault(itemData.image, holder.img);
            getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorVideoList.this, holder.img, itemData.image);
            final ImageView imgdownload = holder.imgdownload;
            holder.imgdownload.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mVideo = itemData;
                    mImageDownLoad = imgdownload;
//                    getStreamSizes(itemData.id, v);
                    getSystem(SystemCommon.class).showDownloadDialog(ActivityAnchorVideoList.this, mVideo, mImageDownLoad);
                }
            });

//            if (itemData.lottery_status.equals("1")) {
//                holder.prize.setVisibility(View.VISIBLE);
//                holder.logo.setVisibility(View.GONE);
//            } else {
//                holder.prize.setVisibility(View.GONE);
//                holder.logo.setVisibility(View.VISIBLE);
//            }
            Utils.setVideoLogo(holder.prize, holder.logo, itemData.lottery_status);
            if (getSystem(SystemDownloadVideoManager.class).isDownloaded(itemData.id))
                holder.imgdownload.setImageResource(R.drawable.icon_download1);
            else {
                holder.imgdownload.setImageResource(R.drawable.icon_download0);
            }

            return convertView;
        }

        class Holder {
            ImageView img;
            ImageView imgdownload;
            ImageView logo;
            ImageView prize;
            TextView publishTiem;
            TextView tiltle;
            TextView gameName;
            TextView duration;
        }

    }

    private Video mVideo;
    private ImageView mImageDownLoad;

}
