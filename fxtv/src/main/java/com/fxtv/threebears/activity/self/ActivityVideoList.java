package com.fxtv.threebears.activity.self;

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
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author FXTV-Android
 *         <p/>
 *         专辑----》视频 列表
 */
public class ActivityVideoList extends BaseActivity {
    private XListView mListView;
    private MyAdapter mAdapter;
    //	private List<Video> mData;
    private String mOid;
    private String mType;
    private String mName;
    private int mPageNum;
    private final int mPageSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_video);
        mOid = getStringExtra("oid");
        mType = getStringExtra("type");
        mName = getStringExtra("name");
        if (mOid != null && !"".equals(mOid)) {
            getSystem(SystemAnalyze.class).analyzeUserAction("focus", mOid, "2");
        }
        initView();
        getData(false, true);
    }

    private void initView() {
        initActionBar();
        initListView();
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.anchor_video_list);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setPageSize(mPageSize);
        if (mAdapter == null) {
            mAdapter = new MyAdapter(null);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mAdapter.getItem(position - 1).id);
                FrameworkUtils.skipActivity(ActivityVideoList.this, ActivityVideoPlay.class, bundle);
            }
        });
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                getData(true, false);
            }

            @Override
            public void onLoadMore() {
                getData(false, false);
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

    class MyAdapter extends BaseListGridAdapter<Video> {

        public MyAdapter(List<Video> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
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
            viewHolder.name.setText(video.title);
            viewHolder.time.setText(video.duration);
            viewHolder.author.setText(video.anchor_name);
            viewHolder.lastTime.setText(video.publish_time);
        /*	SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, viewHolder.img);*/

            getSystem(SystemCommon.class).displayDefaultImage(ActivityVideoList.this, viewHolder.img, video.image);
            final ImageView imgdownload = viewHolder.imgdownload;
            viewHolder.imgdownload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideo = video;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(ActivityVideoList.this, mVideo, mImageDownLoad);
                }
            });
            viewHolder.author.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("anchor_id", video.anchor.id);
                    FrameworkUtils.skipActivity(ActivityVideoList.this, ActivityAnchorZone.class, bundle);
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

    private void getData(final boolean fromRefresh, final boolean showDialog) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", mOid);
        params.addProperty("type", mType);
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", mPageSize + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }

        /*String url = processUrl("Game", "orderVideo", params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, urlcallBack);
        */
        getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.GAME, ApiType.GAME_orderVideo, params), "gameVideosOfOrder", false, false, new RequestCallBack<List<Video>>() {
            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (fromRefresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(ActivityVideoList.this, getString(R.string.notice_no_more_data));
                    mListView.noMoreData();
                }
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(ActivityVideoList.this, resp.msg);
            }

            @Override
            public void onComplete() {
                mListView.stopLoadMore();
                mListView.stopRefresh();
                Utils.dismissProgressDialog();
            }
        });

    }

    private Video mVideo;
    private ImageView mImageDownLoad;
}
