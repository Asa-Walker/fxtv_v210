package com.fxtv.threebears.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fxtv.framework.frame.SystemManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.StreamSize;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.model.VideoStreamsizes;
import com.fxtv.threebears.system.SystemDownloadVideoManager;

import java.util.List;

/**
 * 下载dialog
 */
public class DownloadDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private List<StreamSize> mList;
    private VideoCache mVideoCache;
    private Video mVideo;
    private TextView low, normal, high, supper, cancel;
    private DowbloadCallBack mCallBack;

    public DownloadDialog(Context context, Video video, List<StreamSize> list, DowbloadCallBack callBack) {
        super(context, R.style.my_dialog);
        mContext = context;
        mList = list;
        mVideo = video;
        mCallBack = callBack;
        if (video != null) {
            mVideoCache = new VideoCache();
            mVideoCache.vid = video.id;
            mVideoCache.image = video.image;
            mVideoCache.title = video.title;
            mVideoCache.duration = video.duration;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_dialog);
        setCanceledOnTouchOutside(false);
        init();
    }

    private void init() {
        low = (TextView) findViewById(R.id.low);
        normal = (TextView) findViewById(R.id.normal);
        high = (TextView) findViewById(R.id.high);
        supper = (TextView) findViewById(R.id.supper);
        cancel = (TextView) findViewById(R.id.cancel);
        initVideoStream();
        cancel.setOnClickListener(this);
    }

    private void initVideoStream() {
        for (int i = 0; i < mList.size(); i++) {
            StreamSize streamSize = mList.get(i);
            if ("low".equals(streamSize.stream_type)) {
                low.setText(streamSize.title + "(" + streamSize.size + "M)");
                low.setVisibility(View.VISIBLE);
                if (mVideo.stream_size == null) {
                    mVideo.stream_size = new VideoStreamsizes();
                }
                mVideo.stream_size.low = streamSize.url;
                mVideo.stream_size.lowSize = streamSize.size;
                low.setOnClickListener(this);
            }
            if ("normal".equals(streamSize.stream_type)) {
                normal.setText(streamSize.title + "(" + streamSize.size + "M)");
                normal.setVisibility(View.VISIBLE);
                if (mVideo.stream_size == null) {
                    mVideo.stream_size = new VideoStreamsizes();
                }
                mVideo.stream_size.normal = streamSize.url;
                mVideo.stream_size.normalSize = streamSize.size;
                normal.setOnClickListener(this);
            }
            if ("high".equals(streamSize.stream_type)) {
                high.setText(streamSize.title + "(" + streamSize.size + "M)");
                high.setVisibility(View.VISIBLE);
                if (mVideo.stream_size == null) {
                    mVideo.stream_size = new VideoStreamsizes();
                }
                mVideo.stream_size.high = streamSize.url;
                mVideo.stream_size.highSize = streamSize.size;
                high.setOnClickListener(this);
            }
            if ("hd2".equals(streamSize.stream_type)) {
                supper.setText(streamSize.title + "(" + streamSize.size + "M)");
                supper.setVisibility(View.VISIBLE);
                if (mVideo.stream_size == null) {
                    mVideo.stream_size = new VideoStreamsizes();
                }
                mVideo.stream_size.hd2 = streamSize.url;
                mVideo.stream_size.hd2Size = streamSize.size;
                supper.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.low:
                downLoad(mVideo.stream_size.low, mVideo.stream_size.lowSize);
                break;
            case R.id.normal:
                downLoad(mVideo.stream_size.normal, mVideo.stream_size.normalSize);
                break;
            case R.id.high:
                downLoad(mVideo.stream_size.high, mVideo.stream_size.highSize);
                break;
            case R.id.supper:
                downLoad(mVideo.stream_size.hd2, mVideo.stream_size.hd2Size);
                break;
            case R.id.cancel:
                this.dismiss();
                break;
            default:
                break;
        }
    }

    public void downLoad(String url, int size) {
        mVideoCache.net_url = url;
        mVideoCache.size = size + "";
        SystemManager.getInstance().getSystem(SystemDownloadVideoManager.class).downloadVideo(mVideoCache, new SystemDownloadVideoManager.IDownloadCallBack() {
            @Override
            public void onResult(boolean flag, String msg) {
                mCallBack.onDownLoadSuccess(flag, msg);
            }
        });
        this.dismiss();
    }

    public interface DowbloadCallBack {
        void onDownLoadSuccess(boolean flag, String msg);
    }
}
