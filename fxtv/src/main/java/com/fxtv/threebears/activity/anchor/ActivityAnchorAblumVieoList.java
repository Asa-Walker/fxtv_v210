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
import com.fxtv.threebears.model.Special;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 主播空间里的专辑进入的界面
 *
 * @author Administrator
 */
public class ActivityAnchorAblumVieoList extends BaseActivity {
    private XListView mListView;
    //private List<Video> mData;
    private String mName;
    private Special mSpecial;
    private myAdapter mAdapter;
    private int mPageNum;
    private int mPageSize = 20;
    private Video mVideo;
    private ImageView mImageDownLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_ablum_video_list);
        mName = getStringExtra("ablum_name");
        mSpecial = (Special) getSerializable("special");
        // mAnchorId = getStringExtra("anchor_id");
        initView();
        getData(false, true);
    }

    private void initView() {
        initActionBar();
        initListView();
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_anchor_ablum_video_list_xlitview);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setPageSize(mPageSize);
        if (mAdapter == null) {
            mAdapter = new myAdapter(null);
        }
        mListView.setAdapter(mAdapter);
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
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mAdapter.getItem(position - 1).id);
                FrameworkUtils.skipActivity(ActivityAnchorAblumVieoList.this, ActivityVideoPlay.class, bundle);
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
        params.addProperty("id", mSpecial.id);
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", mPageSize + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_albumVideo, params);
        getSystem(SystemHttp.class).get(this, url, "getAblumVideoList", !fromRefresh, true, new RequestCallBack<List<Video>>() {
            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (mAdapter == null) {
                    mAdapter = new myAdapter(data);
                }
                if (fromRefresh) {
                    mAdapter.setListData(data);
                } else {
                    mAdapter.addData(data);
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
        title.setText(mName);
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

    class myAdapter extends BaseListGridAdapter<Video> {

        public myAdapter(List<Video> listData) {
            super(listData);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(ActivityAnchorAblumVieoList.this, R.layout.item_video, null);
                holder = new Holder();
                holder.gameName = (TextView) convertView.findViewById(R.id.lable1);
                holder.gameName.setVisibility(View.GONE);
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
            holder.gameName.setText(itemData.game_title);
            holder.duration.setText(itemData.duration);
            holder.publishTiem.setText(itemData.publish_time);
            holder.tiltle.setText(itemData.title);
            /*SystemManager.getInstance().getSystem(SystemImageLoader.class)
                    .displayImageDefault(itemData.image, holder.img);*/
            getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorAblumVieoList.this, holder.img, itemData.image);
            final ImageView imgdownload = holder.imgdownload;
            holder.imgdownload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideo = itemData;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(ActivityAnchorAblumVieoList.this, mVideo, mImageDownLoad);
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
            ImageView prize;
            ImageView logo;
            TextView publishTiem;
            TextView tiltle;
            TextView gameName;
            TextView duration;
        }
    }

}
