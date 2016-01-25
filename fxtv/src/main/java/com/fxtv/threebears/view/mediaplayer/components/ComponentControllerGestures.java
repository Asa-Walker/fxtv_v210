package com.fxtv.threebears.view.mediaplayer.components;

import android.content.Context;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.threebears.R;
import com.fxtv.threebears.view.VerticalSeekBar;
import com.fxtv.threebears.view.mediaplayer.MediaController;
import com.fxtv.threebears.view.pullToRefreshSwipeMenuListView.PullToRefreshSwipeMenuListView;

public class ComponentControllerGestures {
    private static final String TAG = "ComponentControllerGestures";

    private MediaController mController;
    private ViewGroup mParentView, mChildView;
    private LayoutInflater mInflater;
    private int mDuration;

    private AudioManager mAM;

    private ViewGroup mGestureVolumeLayout, mGestureBrightLayout;
    private VerticalSeekBar mGestureVolumeSeekBar, mGestureBrightSeekBar;
    private TextView mGestrueVolumeTextView, mGestureBrightTextView;
    private ImageView mGestureVolumeImageView;

    public ComponentControllerGestures(MediaController controller, ViewGroup parent, LayoutInflater inflater) {
        this.mController = controller;
        this.mParentView = parent;
        this.mInflater = inflater;

        mWidth = FrameworkUtils.getScreenHeight(mController.getActivity());
        mAM = (AudioManager) mController.getActivity().getSystemService(Context.AUDIO_SERVICE);
        lParams = mController.getActivity().getWindow().getAttributes();
        mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        mCurrentBright = lParams.screenBrightness;

        initView();
        initGestures();
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    private boolean mIsLandscape;
    public void setScreenOrientation(boolean isLandscape) {
        mIsLandscape = isLandscape;
    }

    private void initView() {
        mChildView = (ViewGroup) mInflater.inflate(R.layout.mediaplayer_controller_gestures_layout, mParentView);
        mGestureVolumeLayout = (ViewGroup) mChildView.findViewById(R.id.gesture_volume_layout);
        mGestureVolumeSeekBar = (VerticalSeekBar) mChildView.findViewById(R.id.gesture_volume_seekbar);
        mGestureVolumeSeekBar.setMax(mMaxVolume);
        mGestrueVolumeTextView = (TextView) mChildView.findViewById(R.id.geture_tv_volume_percentage);
        mGestureVolumeImageView = (ImageView) mChildView.findViewById(R.id.gesture_iv_player_volume);

        mGestureBrightLayout = (ViewGroup) mChildView.findViewById(R.id.gesture_bright_layout);
        mGestureBrightSeekBar = (VerticalSeekBar) mChildView.findViewById(R.id.gesture_bright_vertical_seekbar);
        mGestureBrightTextView = (TextView) mChildView.findViewById(R.id.geture_tv_bright_percentage);

    }

    private int mWidth;
    // 1,调节进度，2，调节音量, 3调节亮度
    private int mGestureFlag;
    // 点击区域，0:左边，1：右边
    private int mPointArea;
    private static final float STEP_DIR = 5f;
    private int mStep;
    private int mStep2;
    private int mMaxVolume;
    private int mCurrentVolume;
    private android.view.WindowManager.LayoutParams lParams;
    private float mMaxBright = 1.0f;
    private float mCurrentBright;
    private long mScrollPlayerPos;

    @SuppressWarnings("deprecation")
    private void initGestures() {
        mStep = FrameworkUtils.dip2px(mController.getContext(), STEP_DIR);
        mStep2 = FrameworkUtils.dip2px(mController.getContext(), 2);

        final GestureDetector gestureDetector = new GestureDetector(new OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent arg0) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent arg0) {
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Logger.d(TAG, "onDown");
                if (mWidth / 2 > e.getX())
                    mPointArea = 0;
                else {
                    mPointArea = 1;
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // 竖屏模式下没有手势功能
                if (!mIsLandscape || mController.isLock())
                    return false;

                if (mGestureFlag == 0) {
                    mGestureFlag = adjustGesture(distanceX, distanceY);
                }

                if (mGestureFlag == 1) {
                    // 快进or快退
                    adjustProgress(distanceX);
                } else if (mGestureFlag == 2) {
                    // 调节音量
                    adjustVolume(distanceX, distanceY);
                } else if (mGestureFlag == 3) {
                    // 调节亮度
                    adjustBrightness(distanceX, distanceY);
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent arg0) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        gestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent arg0) {
                mController.toggleControllView();
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent arg0) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent arg0) {
                if (!mController.isLock()) {
                    mController.toggleVideoPlay();
                }
                return false;
            }
        });
        mChildView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                gestureDetector.onTouchEvent(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    if (!mIsLandscape || mController.isLock())
                        return true;

                    mGestureVolumeLayout.setVisibility(View.INVISIBLE);
                    mGestureBrightLayout.setVisibility(View.INVISIBLE);
                    mController.setSeekStatus(false);

                    if (mGestureFlag == 1) {
                        if (mScrollPlayerPos > 0 && mScrollPlayerPos < mDuration) {
                            Logger.d(TAG,"seek to");
                            mController.seekTo(mScrollPlayerPos);
                        }
                    }

                    mGestureFlag = 0;
                    mScrollPlayerPos = 0;
                }
                return true;
            }
        });
    }

    /**
     * 判断手势
     *
     * @param distanceX
     * @param distanceY
     */
    private int adjustGesture(float distanceX, float distanceY) {
        int flag = 0;
        float tmpX = Math.abs(distanceX);
        float tmpY = Math.abs(distanceY);
        if (tmpX >= mStep || tmpY >= mStep) {
            if (tmpX > tmpY) {
                // 水平方向滑动
                flag = 1;
            } else {
                // 竖直方向滑动
                if (mPointArea == 0) {
                    flag = 3;
                } else if (mPointArea == 1) {
                    flag = 2;
                }
            }
        }
        return flag;
    }

    /**
     * 调节进度
     *
     * @param distanceX
     */
    private void adjustProgress(float distanceX) {
        mController.setSeekStatus(true);
        if (mScrollPlayerPos == 0) {
            mScrollPlayerPos = mController.getCurrentPos();
        }
        boolean forward = false;
        if (distanceX > 0) {
            // backward
            if (mScrollPlayerPos != 0) {
                mScrollPlayerPos -= 2 * 1000;
            }
            forward = false;
        } else {
            // forward
            if (mScrollPlayerPos < mDuration) {
                mScrollPlayerPos += 2 * 1000;
            }
            forward = true;
        }
        if (mScrollPlayerPos > 0 && mScrollPlayerPos < mDuration) {
            mController.seekToForGesture(mScrollPlayerPos, forward);
        }
        Logger.d(TAG,"adjustProgress,pos="+mScrollPlayerPos);
    }

    /**
     * 调节音量
     *
     * @param distanceY // * @param distanceX //
     */
    private void adjustVolume(float distanceX, float distanceY) {
        mGestureVolumeLayout.setVisibility(View.VISIBLE);
        int setp = mStep2;
        if (distanceY >= setp) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
            if (mCurrentVolume < mMaxVolume) {// 为避免调节过快，distanceY应大于一个设定值
                mCurrentVolume++;
            }
        } else if (distanceY <= -setp) {// 音量调小
            if (mCurrentVolume > 0) {
                mCurrentVolume--;
            }
        }
        int curpercent = mCurrentVolume * 100 / mMaxVolume;
        mGestureVolumeSeekBar.setProgress(mCurrentVolume);
        mGestrueVolumeTextView.setText(curpercent + "%");
        if (curpercent == 0) {
            mGestureVolumeImageView.setImageResource(R.drawable.player_volume_silence);
        } else {
            mGestureVolumeImageView.setImageResource(R.drawable.player_volume);
        }
        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, 0);
    }

    /**
     * 调节亮度
     *
     * @param distanceY
     * @param distanceX
     */
    private void adjustBrightness(float distanceX, float distanceY) {
        mGestureBrightLayout.setVisibility(View.VISIBLE);
        int setp = mStep2;
        if (mCurrentBright < -0f) {
            mCurrentBright = 0.01f;
        }
        if (distanceY >= setp) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
            if (mCurrentBright < mMaxBright) {// 为避免调节过快，distanceY应大于一个设定ֵ
                mCurrentBright = mCurrentBright + 0.1f;
            }
        } else if (distanceY <= -setp) {// 音量调小
            if (mCurrentBright > 0) {// 为避免调节过快，distanceY应大于一个设定
                mCurrentBright = mCurrentBright - 0.1f;
            }
        }
        if (mCurrentBright > 1)
            mCurrentBright = 1;
        // 防止华为等某些机型，在亮度为零的情况下，手机自动待机
        if (mCurrentBright <= 0) {
            mCurrentBright = 0;
        }
        mGestureBrightTextView.setText((int) (mCurrentBright * 100) + "%");
        if (mCurrentBright <= 0) {
            mCurrentBright = 0.01f;
        }
        lParams.screenBrightness = mCurrentBright;
        mGestureBrightSeekBar.setProgress((int) (mCurrentBright * 100));
        mController.getActivity().getWindow().setAttributes(lParams);
    }
}
