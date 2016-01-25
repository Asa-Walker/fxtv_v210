package com.fxtv.threebears.fragment.module.player;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.view.mediaplayer.IjkVideoView;
import com.fxtv.threebears.view.mediaplayer.MediaController;

public class FragmentPlayerVideo extends BaseFragment {
    private IjkVideoView mVideoView;
    private MediaController mMediaController;
    int mScreenWidth;
    int mScreenHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_player_video, container, false);
        mVideoView = (IjkVideoView) mRoot.findViewById(R.id.video_view);
        mMediaController = (MediaController) mRoot.findViewById(R.id.controller);
        mMediaController.setPlayer(mVideoView);

        mScreenWidth = FrameworkUtils.getScreenWidth(getContext());
        mScreenHeight = FrameworkUtils.getScreenHeight(getContext());

        setScreenChange(false);

        return mRoot;
    }

    private Handler mHandler = new Handler();

    public void setVideo(final Video video) {
        if (mMediaController == null) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    setVideo(video);
                }
            }, 200);
        } else {
            mMediaController.setVideo(video);
        }
    }

    ActivityVideoPlay mParent;

    public void setParent(ActivityVideoPlay parent) {
        mParent = parent;
        if (mMediaController != null) {
            //mMediaController.setParent(mParent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMediaController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMediaController.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMediaController.onDestory();
    }

    public void onBackPressed() {
        mMediaController.onBackPressed();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setScreenChange(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void setScreenChange(boolean isLandscape) {
        ViewGroup.LayoutParams layoutParams = mRoot.getLayoutParams();
        int width = 0, height = 0;
        if (isLandscape) {
            width = mScreenHeight;
            height = mScreenWidth;

//            mRoot.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

            mRoot.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        } else {
            width = mScreenWidth;
            height = mScreenWidth / 16 * 9;


        }

        layoutParams.width = width;
        layoutParams.height = height;

        mRoot.setLayoutParams(layoutParams);
    }
}
