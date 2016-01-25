package com.fxtv.threebears.activity.player;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.RecentPlayHistory;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.system.SystemHistory;
import com.fxtv.threebears.view.mediaplayer.IjkVideoView;
import com.fxtv.threebears.view.mediaplayer.MediaController;

import java.util.Locale;

public class ActivityVideoPlayLandscape extends BaseActivity {
    private static final String TAG = "ActivityVideoPlayLandscape";
    private VideoCache mVideoCache;
    private MediaController mMediaController;
    private ViewGroup mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_play_l);
        mRootView = (ViewGroup) findViewById(R.id.root_view);
        mVideoCache = (VideoCache) getIntent().getSerializableExtra("video");
        initVideoPlayer();
    }

    private void initVideoPlayer() {
        IjkVideoView videoView = (IjkVideoView) findViewById(R.id.video_view);
        mMediaController = (MediaController) findViewById(R.id.controller);
        mMediaController.setRootView(mRootView);
        mMediaController.setPlayer(videoView);
//		mMediaController.setVideo(mVideo, true);
        mMediaController.setNative(mVideoCache);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG, "onPause");
        recordHistory();
        mMediaController.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaController.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaController.onDestory();
    }

    private void recordHistory() {
        RecentPlayHistory history = new RecentPlayHistory();
        history.vId = mVideoCache.vid;
        history.vTitle = mVideoCache.title;
        history.vDuration = mVideoCache.duration;
        history.vImage = mVideoCache.image;
        history.vLastPos = mMediaController.getCurrentPos();
        history.vLastPosStr = generateTime(mMediaController.getCurrentPos());
        SystemManager.getInstance().getSystem(SystemHistory.class).updateRecentPlayHistory(history);
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60);
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
}
