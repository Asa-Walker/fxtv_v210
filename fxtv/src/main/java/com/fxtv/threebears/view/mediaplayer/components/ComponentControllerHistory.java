package com.fxtv.threebears.view.mediaplayer.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.RecentPlayHistory;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemHistory;
import com.fxtv.threebears.view.mediaplayer.MediaController;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ComponentControllerHistory {
    private static final String TAG = "ComponentControllerHistory";
    private MediaController mController;
    private ViewGroup mParentView, mChildView;
    private LayoutInflater mInflater;

    private TextView mHistoryContent, mBtnClose, mBtnReset;

    Timer mTimer;
    private boolean mHasShowed;
    private long mDuration;

    private RecentPlayHistory mRecentPlayHistory;

    public ComponentControllerHistory(MediaController controller, ViewGroup parent, LayoutInflater inflater) {
        this.mController = controller;
        this.mParentView = parent;
        this.mInflater = inflater;

        initView();
    }

    public void setData(String vid, long duration) {
        mDuration = duration;
        mRecentPlayHistory = SystemManager.getInstance().getSystem(SystemHistory.class).getRecentPlayHistory(vid);
        if (mRecentPlayHistory != null) {
        }
    }

    public void show() {
        if (mRecentPlayHistory == null)
            return;

        Logger.d(TAG, "mRecentPlayHistory.vLastPos=" + mRecentPlayHistory.vLastPos);
        Logger.d(TAG, "dis=" + (mDuration - mRecentPlayHistory.vLastPos));
        if (mHasShowed || mRecentPlayHistory == null || mRecentPlayHistory.vLastPos < 10000
                || (mDuration - mRecentPlayHistory.vLastPos) < 10000) {
            return;
        }
        Logger.d(TAG, "show");
        mHistoryContent.setText("系统记录到上次播放至" + mRecentPlayHistory.vLastPosStr);
        mHasShowed = true;
        mChildView.setVisibility(View.VISIBLE);
        mController.seekTo(mRecentPlayHistory.vLastPos);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mController.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        hide();
                    }
                });
            }
        }, 5000);
    }

    public void hide() {
        mChildView.setVisibility(View.GONE);
    }

    public void recoderProgress(Video video) {
        if (video == null) {
            return;
        }
        if (mRecentPlayHistory == null) {
            mRecentPlayHistory = new RecentPlayHistory();
            mRecentPlayHistory.vId = video.id;
            mRecentPlayHistory.vTitle = video.title;
            mRecentPlayHistory.vDuration = video.duration;
            mRecentPlayHistory.vImage = video.image;
        }
        mRecentPlayHistory.vLastPos = mController.getCurrentPos();
        mRecentPlayHistory.vLastPosStr = generateTime(mController.getCurrentPos());
        SystemManager.getInstance().getSystem(SystemHistory.class).updateRecentPlayHistory(mRecentPlayHistory);
    }

    private void initView() {
        mChildView = (ViewGroup) mInflater.inflate(R.layout.mediaplayer_controller_history_layout, mParentView);
        mChildView.setVisibility(View.GONE);
        mHistoryContent = (TextView) mChildView.findViewById(R.id.video_history_content);
        mBtnClose = (TextView) mChildView.findViewById(R.id.video_history_close);
        mBtnReset = (TextView) mChildView.findViewById(R.id.video_history_reset);
        mBtnClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hide();
            }
        });

        mBtnReset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mController.seekTo(0);
                hide();
            }
        });
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60);
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
}
