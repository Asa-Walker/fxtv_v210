package com.fxtv.threebears.view.mediaplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.system.SystemConfig;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.mediaplayer.DanmakuController.IDanmakuListeners;
import com.fxtv.threebears.view.mediaplayer.components.ComponentControllerBottom;
import com.fxtv.threebears.view.mediaplayer.components.ComponentControllerDanmaku;
import com.fxtv.threebears.view.mediaplayer.components.ComponentControllerGestures;
import com.fxtv.threebears.view.mediaplayer.components.ComponentControllerHistory;
import com.fxtv.threebears.view.mediaplayer.components.ComponentControllerTips;
import com.fxtv.threebears.view.mediaplayer.components.ComponentControllerTitle;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MediaController extends FrameLayout {
    private static final String TAG = "MediaController";

    public final static int MSG_SHOW = 0;
    public final static int MSG_HIDE = 1;
    public final static int CHOOSE_HIDE = 3;
    private Context mContext;
    private ViewGroup mRoot;
    private LayoutInflater mInflater;
    private IjkVideoView mPlayer;
    private Video mVideo;
    private ImageView mLockImg, mLogo;

    private ComponentControllerTitle mComponentTitle;
    private ComponentControllerBottom mComponentBottom;
    private ComponentControllerTips mComponentTips;
    private ComponentControllerGestures mComponentGestures;
    private ComponentControllerHistory mComponentHistory;
    private ComponentControllerDanmaku mComponentDanmaku;

    private int mDuration;
    private String mDurationString;

    public String mUrlMobile;
    public String mUrlPc;
    public String mShouleUseSource;
    private int mPlayerErrorCounts;

    private long mLastPos;
    private boolean mIsDownloaded, mIsLandscape, mIsLock, mAllUrlIsInvalid;
    private boolean mIsControllViewShowing;
    private boolean mSeekBarIsDragging;

    private boolean mIsChangedRate;
    private boolean mIsChangingRate;
    private boolean mPlayerPrepared;

    private boolean mDanmakuEnable = true;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW:
                    mIsControllViewShowing = true;
                    if (mIsLandscape) {
                        mLockImg.setVisibility(View.VISIBLE);
                        if (!mIsLock) {
                            mComponentTitle.show();
                            mComponentBottom.show();
                        }
                    } else {
                        mIsLock = false;
                        mLockImg.setVisibility(View.GONE);
                        mComponentTitle.show();
                        mComponentBottom.show();
                    }

                    if (mShouldImmersive) {
                        if (mIsLandscape) {
                            mHandler.removeCallbacks(showNavigation2);
                            mHandler.removeCallbacks(hideNavigation);
                            mHandler.post(showNavigation);
                        } else {
                            mHandler.removeCallbacks(showNavigation);
                            mHandler.removeCallbacks(hideNavigation);
                            mHandler.post(showNavigation2);
                        }
                    }

                    sendMessageDelayed(obtainMessage(MSG_SHOW), 1000);
                    break;
                case MSG_HIDE:
                    mIsControllViewShowing = false;
                    removeMessages(MSG_SHOW);
                    mLockImg.setVisibility(View.GONE);
                    mComponentTitle.hide();
                    mComponentBottom.hide();

                    if (mShouldImmersive) {
                        if (mIsLandscape) {
                            mHandler.removeCallbacks(showNavigation2);
                            mHandler.removeCallbacks(showNavigation);
                            mHandler.post(hideNavigation);
                        }
                    }

                    break;
                case CHOOSE_HIDE:
                    View view = mRoot.findViewById(R.id.video_proportion_choose);
                    if (view.getVisibility() == View.VISIBLE) {
                        mRoot.findViewById(R.id.video_proportion_choose).setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    public MediaController(Context context) {
        this(context, null);
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initView();
    }

    public void setPlayer(IjkVideoView player) {
        mPlayer = player;
        initPlayerListens();
    }

    public Video getVideo() {
        return mVideo;
    }

    public void setVideo(Video video) {
        this.mVideo = video;
        mDuration = convertTimeString2Int(mVideo.duration);
        mDurationString = mVideo.duration;
        mUrlMobile = mVideo.url;
        mUrlPc = mVideo.url_pc;
        mShouleUseSource = mVideo.use_pc;

        mComponentTitle.setTitle(mVideo.title);
        mComponentBottom.setDuration(mDuration, mDurationString);
        mComponentBottom.setStream(mVideo.stream_size);
        mComponentHistory.setData(mVideo.id, mDuration);
        mComponentGestures.setDuration(mDuration);
        mComponentTips.setDuration(mDuration);
        mComponentTips.showBuffer("播放器初始化中...");
        setVideoPath();
    }

    private ViewGroup mRootView;

    public void setRootView(ViewGroup rootView) {
        mRootView = rootView;
    }

    public void setNative(VideoCache videoCache) {
        mIsDownloaded = true;
        mIsLandscape = true;
        mDuration = convertTimeString2Int(videoCache.duration);
        mDurationString = videoCache.duration;
        mUrlMobile = videoCache.url;

        mComponentTitle.setTitle(videoCache.title);
        mComponentBottom.setDuration(mDuration, mDurationString);
        mComponentHistory.setData(videoCache.vid, mDuration);
        mComponentGestures.setDuration(mDuration);
        mComponentGestures.setScreenOrientation(mIsLandscape);
        mComponentTips.setDuration(mDuration);

        mComponentTips.showBuffer("播放器初始化中...");
        setVideoPath();
    }

    public void show() {
        show(5000);
    }

    public void show(int time) {
        mHandler.sendEmptyMessage(MSG_SHOW);
        mHandler.removeMessages(MSG_HIDE);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_HIDE), time);
    }

    public void hide() {
        mHandler.sendEmptyMessage(MSG_HIDE);
    }

    public void toggleControllView() {
        if (mIsControllViewShowing) {
            hide();
        } else {
            show();
        }
    }

    public void start() {
        Logger.d(TAG, "start");
        if (canPlay()) {
            if (mLastPos > 0) {
                Logger.d(TAG, "mLastPos=" + mLastPos);
                mPlayer.seekTo(mLastPos);
                mLastPos = 0;
            } else {

            }
            mPlayer.start();
            mComponentDanmaku.resume();
        }
    }

    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
            mLastPos = getCurrentPos();
            mComponentDanmaku.pause();

            if (mComponentHistory != null && mVideo != null && mPlayer.getCurrentStatus() != IjkVideoView.STATE_IDLE) {
                mComponentHistory.recoderProgress(mVideo);
            }
        }
    }

    public void toggleVideoPlay() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                pause();
            } else {
                start();
            }
        }
    }

    public void releasePlayer() {
        mPlayer.stopPlayback();
        mPlayer.release(true);
        IjkMediaPlayer.native_profileEnd();
    }

    public void setSeekStatus(boolean seeking) {
        mSeekBarIsDragging = seeking;
    }

    public boolean getSeekStatus() {
        return mSeekBarIsDragging;
    }

    public boolean isChangingRate() {
        return mIsChangingRate;
    }

    public void seekTo(long ms) {
        mPlayer.seekTo(ms);
        show();
    }

    public void seekToForGesture(long pos, boolean isForward) {
//        seekTo(pos);
        mComponentBottom.updateSeekBar(pos);
        mComponentBottom.updateTime(pos);
        mComponentTips.showSeek(pos, isForward);
        show();
    }

    public long getCurrentPos() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getBufferPercentage() {
        if (mPlayer != null) {
            return mPlayer.getBufferPercentage();
        }
        return 0;
    }

    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    public void changeRate(String url) {
        if (url == null) {
            return;
        }
        mLastPos = getCurrentPos();
        mPlayerPrepared = false;
        mIsChangedRate = true;
        mIsChangingRate = true;
        mPlayer.setVideoPath(url);
        Logger.d(TAG, "changeRate,url=" + url);
    }

    public boolean isLock() {
        return mIsLock;
    }

    public Activity getActivity() {
        return (Activity) mContext;
    }

    public void sendDanmaku(String content) {
        mComponentDanmaku.sendDanmaku(mVideo.id, getCurrentPos(), content);
    }

    public void toggleDanmaku() {
        if (mComponentDanmaku.isShown()) {
            mDanmakuEnable = false;
            mComponentDanmaku.hide();
        } else {
            mDanmakuEnable = true;
            mComponentDanmaku.show();
        }
    }

    public boolean getDanmakuEnable() {
        return mDanmakuEnable;
    }

    public boolean danmakuIsShown() {
        if (mComponentDanmaku != null) {
            return mComponentDanmaku.isShown();
        }
        return false;
    }

    public boolean isDownloaded() {
        return mIsDownloaded;
    }

    public void onPause() {
        Logger.d(TAG, "onPause");
        pause();
    }

    public void onResume() {
        // start();
    }

    public void onDestory() {
        Logger.d(TAG, "onDestory");
        mComponentDanmaku.release();
        new Thread(new Runnable() {

            @Override
            public void run() {
                releasePlayer();
            }
        }).start();
    }

    public void onBackPressed() {
        if (mIsDownloaded) {
            finishActivity();
        } else {
            if (mIsLandscape) {
                setScreenOrientation(false);
            } else {
                finishActivity();
            }
        }
    }

    public void finishActivity() {
        mComponentHistory.recoderProgress(mVideo);
        mPlayer.stopPlayback();
        mPlayer.release(true);
        IjkMediaPlayer.native_profileEnd();
        getActivity().finish();
    }

    /**
     * 设置屏幕方向 true:l false:p
     *
     * @param isLandscape
     */
    public void onScreenChange(boolean isLandscape) {
        mIsLandscape = isLandscape;
        if (isLandscape) {
            if (mDanmakuEnable) {
                mComponentDanmaku.show();
            }
            mPlayer.setAspectRatio(mIsLandscape, mChoosenProportion);
        } else {
            mComponentDanmaku.hide();
            mPlayer.setAspectRatio(mIsLandscape, IRenderView.AR_ASPECT_FIT_PARENT);
        }

        mComponentTitle.setScreenOrientation(isLandscape);
        mComponentBottom.setScreenOrientation(isLandscape);
        mComponentGestures.setScreenOrientation(isLandscape);

        setVideoLogoLayoutParams();
        mRoot.findViewById(R.id.video_proportion_choose).setVisibility(View.GONE);
        show();
    }

    public void toggleScreen() {
        if (mIsLandscape) {
            setScreenOrientation(false);
        } else {
            setScreenOrientation(true);
        }
    }

    public void setScreenOrientation(boolean landscape) {
        if (!landscape) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        if (mAutoRotation) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                }
            }, 3000);
        }
    }

    /**
     * ----------------私有函数----------------
     */

    private boolean mShouldImmersive;
    private boolean mAutoRotation;

    private void initView() {
        mShouldImmersive = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        int flag = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0);
        mAutoRotation = flag == 1;

        mRoot = (ViewGroup) mInflater.inflate(R.layout.mediaplayer_controller_layout, null);
        addView(mRoot);

        mLogo = (ImageView) mRoot.findViewById(R.id.logo);

        mLockImg = (ImageView) mRoot.findViewById(R.id.player_lock);
        mLockImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mIsLock) {
                    mIsLock = false;
                    mLockImg.setImageResource(R.drawable.icon_unlock);
                    show();
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                } else {
                    mIsLock = true;
                    mLockImg.setImageResource(R.drawable.icon_lock);
                    hide();
                    setScreenOrientation(true);
                }
            }
        });

        initContainer();

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_WARN);
    }

    private void initContainer() {
        initContainerTitle();
        initContainerBottom();
        initContainerTips();
        initContainerGestures();
        initContainerHistory();
        initContainerDanmaku();
    }

    private void initContainerDanmaku() {
        ViewGroup container = (ViewGroup) mRoot.findViewById(R.id.video_danmaku_layout);
        mComponentDanmaku = new ComponentControllerDanmaku(this, container, mInflater);
        mComponentDanmaku.setCallBack(new IDanmakuListeners() {

            @Override
            public void onPrapared() {
                Logger.d(TAG, "danmaku onPrapared");
                test();
            }

            @Override
            public void onLoadDanmakuError() {
                showToast("加载弹幕失败");
                test();
            }
        });
    }

    private void test() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mComponentTips.hideBuffer();
                mComponentDanmaku.start();
                mComponentDanmaku.hide();
                start();
                show();
                mComponentHistory.show();
            }
        });
    }

    private void initContainerHistory() {
        ViewGroup containerHistory = (ViewGroup) mRoot.findViewById(R.id.video_history_layout);
        mComponentHistory = new ComponentControllerHistory(this, containerHistory, mInflater);
    }

    private void initContainerGestures() {
        ViewGroup containerGestures = (ViewGroup) mRoot.findViewById(R.id.video_gesture_layout);
        mComponentGestures = new ComponentControllerGestures(this, containerGestures, mInflater);
    }

    private void initContainerTips() {
        ViewGroup containerTips = (ViewGroup) mRoot.findViewById(R.id.video_tips_layout);
        mComponentTips = new ComponentControllerTips(this, containerTips, mInflater);
    }

    private void initContainerBottom() {
        ViewGroup containerBottom = (ViewGroup) mRoot.findViewById(R.id.video_bottom_layout);
        mComponentBottom = new ComponentControllerBottom(this, containerBottom, mInflater);
    }

    private void initContainerTitle() {
        ViewGroup containerTitle = (ViewGroup) mRoot.findViewById(R.id.video_title_layout);
        mComponentTitle = new ComponentControllerTitle(this, containerTitle, mInflater);
    }

    private void initPlayerListens() {
        mPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                Logger.d(TAG, "onPrepared");
                mPlayerPrepared = true;
                if (mIsDownloaded) {
                    start();
                    show();
                    mComponentHistory.show();
                    mComponentTips.hideBuffer();
                } else {
                    if (mIsChangingRate) {
                        start();
                        mIsChangingRate = false;
                    } else {
                        Logger.d(TAG, "initPlayerListens,onPrepared,init danmaku");
                        mComponentTips.showBuffer("弹幕初始化中");
                        mComponentDanmaku.setDanmakuUrl(mVideo.barrage_url);
                    }
                }
            }
        });
        mPlayer.setOnInfoListener(new OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int arg2) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        Logger.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        Logger.d(TAG, "MEDIA_INFO_BUFFERING_START");
                        mComponentTips.showBuffer("努力加载中...");
                        mComponentDanmaku.pause();
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        Logger.d(TAG, "MEDIA_INFO_BUFFERING_END");
                        mComponentTips.hideBuffer();
                        mComponentDanmaku.resume();
                        break;
                }

                return false;
            }
        });
        mPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(IMediaPlayer arg0, int arg1, int arg2) {
                mLastPos = mPlayer.getCurrentStatus();
                Logger.e(TAG, "onError,arg1=" + arg1 + ",arg2=" + arg2);
                playErrorToNet();
                if (mIsDownloaded) {
                    showToast("播放器出错");
                    return false;
                }

                if (mIsChangedRate) {
                    showToast("播放器地址无效");
                    mComponentDanmaku.pause();
                } else {
                    if (mPlayerErrorCounts <= 1) {
                        mPlayerErrorCounts++;
                        getVideoUrlLatest();
                    } else {
                        mAllUrlIsInvalid = true;
                        releasePlayer();
                        FrameworkUtils.showToast(getActivity(), "播放器地址无效");
                    }
                }
                return false;
            }
        });
        mPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                Logger.d(TAG, "onCompletion");
                if (!mIsDownloaded) {
                    setScreenOrientation(false);
                    show();
                } else {
                    finishActivity();
                }
            }
        });
        mPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(IMediaPlayer mp) {
                Logger.d(TAG, "onSeekComplete");
                if (!mIsDownloaded) {
                    mComponentDanmaku.seekTo(getCurrentPos());
                }
            }
        });
    }

    /**
     * 播放失败时向后台请求做统计用
     */
    private void playErrorToNet() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mVideo.id);
        String url = Utils.processUrl(ModuleType.BASE, ApiType.BASE_playError, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext, url, "playError", false, false, new RequestCallBack() {
            @Override
            public void onSuccess(Object data, Response resp) {
                Logger.d(TAG, "onSuccess_playErrorToNet_msg=" + resp.msg);
            }

            @Override
            public void onFailure(Response resp) {
                Logger.d(TAG, "onFailure_playErrorToNet_msg=" + resp.msg);
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void setVideoPath() {
        if (mIsDownloaded) {
            mPlayer.setVideoPath(mUrlMobile);
            return;
        }

        if (mPlayerErrorCounts < 2) {
            mPlayer.setVideoPath(mUrlMobile);
        } else {
            mPlayer.setVideoPath(mUrlPc);
        }

//        if (mShouleUseSource.equals("1")) {// 使用pc源
//            if (mPlayerErrorCounts < 2) {
//                Logger.d(TAG, "pc url error counts:" + mPlayerErrorCounts + ",url=" + mUrlPc);
//                mPlayer.setVideoPath(mUrlPc);
//            } else {
//                Logger.d(TAG, "pc url error over limit,use mobile url=" + mUrlMobile);
//                mPlayer.setVideoPath(mUrlMobile);
//            }
//        } else { // 使用移动源
//            if (mPlayerErrorCounts < 2) {
//                Logger.d(TAG, "mobile url error counts:" + mPlayerErrorCounts + ",url=" + mUrlMobile);
//                mPlayer.setVideoPath(mUrlMobile);
//            } else {
//                Logger.d(TAG, "mobile url error over limit,use pc url=" + mUrlPc);
//                mPlayer.setVideoPath(mUrlPc);
//            }
//        }
    }

    private boolean canPlay() {
        if (!mPlayerPrepared) {
            return false;
        }

        if (mIsDownloaded)
            return true;

        if (mAllUrlIsInvalid)
            return false;

        if (FrameworkUtils.isNetworkConnected(mContext)) {
            if (FrameworkUtils.isWifiConnected(mContext)) {
                return true;
            } else {
                if (SystemManager.getInstance().getSystem(SystemConfig.class).mCanPlayUnderFlowEvn) {
                    return true;
                } else {
                    FrameworkUtils.showToast(mContext,
                            mContext.getString(R.string.notice_NOT_ALLOW_DOWNLOAD_AND_PLAY_UNDER_3GOR4G));
                    return false;
                }
            }
        } else {
            FrameworkUtils.showToast(mContext, mContext.getString(R.string.notice_net_not_connect));
            return false;
        }
    }

    private void getVideoUrlLatest() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mVideo.id);
        params.addProperty("type", "1");

        mComponentTips.showBuffer("更新播放地址...");
        String url = Utils.processUrl(ModuleType.BASE, ApiType.BASE_videoPlayUrl, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext, url, "getVideoLatestUrl", false, false, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data, Response resp) {
                try {
                    JSONObject jObject = new JSONObject(data);
                    mUrlMobile = jObject.getString("url");
                    mUrlPc = jObject.getString("url_pc");
                    mShouleUseSource = jObject.getString("use_pc");
                    setVideoPath();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Logger.d(TAG, "播放器地址错误,code=" + "001");
                    showToast("播放器地址错误,code=" + "001");
                    mComponentTips.hideBuffer();
                }
            }

            @Override
            public void onFailure(Response resp) {
                Logger.d(TAG, "请求播放地址出错,code=" + "002");
                showToast("请求播放地址出错,code=" + "002");
                mComponentTips.hideBuffer();
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void setVideoLogoLayoutParams() {
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mLogo
                .getLayoutParams();
        if (mIsLandscape) {
            params.width = FrameworkUtils.dip2px(mContext, 96);
            params.height = FrameworkUtils.dip2px(mContext, 22);
            params.setMargins(0, FrameworkUtils.dip2px(mContext, 20), 10, 0);
        } else {
            params.width = FrameworkUtils.dip2px(mContext, 96);
            params.height = FrameworkUtils.dip2px(mContext, 22);
        }
        mLogo.setLayoutParams(params);
    }

    private int convertTimeString2Int(String duration) {
        int total = 0;
        String[] split = duration.split(":");
        for (int i = 0; i < split.length; i++) {
            int tmp = Integer.parseInt(split[i]);

            if (i != split.length - 1) {
                total += tmp * 60 * (split.length - i - 1) * 1000;
            } else {
                total += tmp * 1000;
            }
        }

        return total;
    }

    private OnClickListener videoChooseListnner;
    private int mChoosenProportion = IRenderView.AR_ASPECT_FIT_PARENT;

    /**
     * 获取当前视频比例
     *
     * @return
     */
    public int getCurrentProportion() {
        return mChoosenProportion;
    }

    public void showChooseVideoProportion() {
        View parent = mRoot.findViewById(R.id.video_proportion_choose);
        parent.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(CHOOSE_HIDE, 3000);
        if (videoChooseListnner == null) {
            videoChooseListnner = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        //默认比例
                        case R.id.moren:
                            mPlayer.setAspectRatio(mIsLandscape, IRenderView.AR_ASPECT_FIT_PARENT);
                            changeColor(R.id.moren, IRenderView.AR_ASPECT_FIT_PARENT);
                            break;
                        //16:9比例
                        case R.id.long_choose:
                            mPlayer.setAspectRatio(mIsLandscape, IRenderView.AR_16_9_FIT_PARENT);
                            changeColor(R.id.long_choose, IRenderView.AR_16_9_FIT_PARENT);
                            break;
                        //4:3比例
                        case R.id.width_choose:
                            mPlayer.setAspectRatio(mIsLandscape, IRenderView.AR_4_3_FIT_PARENT);
                            changeColor(R.id.width_choose, IRenderView.AR_4_3_FIT_PARENT);
                            break;
                        //全屏
                        case R.id.full_screen_choose:
                            mPlayer.setAspectRatio(mIsLandscape, IRenderView.AR_MATCH_PARENT);
                            changeColor(R.id.full_screen_choose, IRenderView.AR_MATCH_PARENT);
                            break;
                        default:
                            break;

                    }
                }
            };
            mRoot.findViewById(R.id.moren).setOnClickListener(videoChooseListnner);
            mRoot.findViewById(R.id.full_screen_choose).setOnClickListener(videoChooseListnner);
            mRoot.findViewById(R.id.width_choose).setOnClickListener(videoChooseListnner);
            mRoot.findViewById(R.id.long_choose).setOnClickListener(videoChooseListnner);
        }
    }

    private void changeColor(int resouce, int ChoosenProportion) {
        int[] temp = {R.id.moren, R.id.long_choose, R.id.width_choose, R.id.full_screen_choose};
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == resouce) {
                ((TextView) mRoot.findViewById(resouce)).setTextColor(getResources().getColor(R.color.main_color));
            } else {
                ((TextView) mRoot.findViewById(temp[i])).setTextColor(getResources().getColor(R.color.text_color_white));
            }
        }
        mChoosenProportion = ChoosenProportion;
        mRoot.findViewById(R.id.video_proportion_choose).setVisibility(View.GONE);
    }

    private final Runnable hideNavigation = new Runnable() {
        @Override
        public void run() {
            mRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable showNavigation = new Runnable() {
        @Override
        public void run() {
            mRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
    };

    private final Runnable showNavigation2 = new Runnable() {
        @Override
        public void run() {
            mRootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_VISIBLE
            );
        }
    };

    private void showToast(String msg) {
        FrameworkUtils.showToast(mContext, msg);
    }
}
