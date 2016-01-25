package com.fxtv.threebears.view.mediaplayer.components;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
import com.fxtv.threebears.model.StreamSize;
import com.fxtv.threebears.model.VideoStreamsizes;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.mediaplayer.MediaController;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Locale;

public class ComponentControllerBottom {
    private static final String TAG = "ComponentControllerBottom";

    private Context mContext;
    private static final String STREAM_SUPER = "超清";
    private static final String STREAM_HIGH = "高清";
    private static final String STREAM_NORMAL = "标清";
    private static final String STREAM_LOW = "流畅";

    private MediaController mController;
    private ViewGroup mParentView, mChildView, mChildViewL;
    private LayoutInflater mInflater;

    private ImageView mBtnStart, mBtnStartL, mBtnScreen, mBtnScreenL;
    private TextView mTvDuration, mTvDurationL, mTvPosL;
    private volatile TextView mTvPos;
    private SeekBar mSeekBar, mSeekBarL;
    private TextView mTvRate;
    private RadioButton mDanmaku;
    private ImageView mDanmakuSend;

    private long mDuration;
    private String mDurationStr;
    private VideoStreamsizes mVideoStreamsizes;

    private PopupWindow mBottomRatePop;
    private String mCurrentRate;
    private InputMethodManager mInputMethodManager;
    private PopupWindow mPopupWindow;


    public ComponentControllerBottom(MediaController controller, ViewGroup parent, LayoutInflater inflater) {
        this.mController = controller;
        this.mParentView = parent;
        this.mInflater = inflater;
        mContext = controller.getContext();
        mInputMethodManager = (InputMethodManager) controller.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        initView();
    }

    public void setDuration(long duration, String durationStr) {
        this.mDuration = duration;
        this.mDurationStr = durationStr;
        mTvDuration.setText(durationStr);
        mTvDurationL.setText(durationStr);
    }

    public void setStream(VideoStreamsizes vs) {
        mVideoStreamsizes = vs;
        mTvRate.setText(getLastStream());
    }

    public void show() {
        if (mIsLandscape) {
            if (mController.isDownloaded()) {
                mChildView.setVisibility(View.VISIBLE);
            } else {
                mChildViewL.setVisibility(View.VISIBLE);
            }
        } else {
            mChildView.setVisibility(View.VISIBLE);
        }

        update();
    }

    public void hide() {
        if (mIsLandscape) {
            if (mController.isDownloaded()) {
                mChildView.setVisibility(View.GONE);
            } else {
                mChildViewL.setVisibility(View.GONE);
            }
        } else {
            mChildView.setVisibility(View.GONE);
        }

        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private boolean mIsLandscape;

    public void setScreenOrientation(boolean isLandscape) {
        mIsLandscape = isLandscape;
        if (mController.isDownloaded()) {
            mParentView.removeAllViews();
            mParentView.addView(mChildView);
        } else {
            if (isLandscape) {
                mParentView.removeAllViews();
                mParentView.addView(mChildViewL);
            } else {
                mParentView.removeAllViews();
                mParentView.addView(mChildView);
            }
        }
    }

    private void update() {
        updateStartBtn();
        if (!mController.getSeekStatus() && !mController.isChangingRate()) {
            updateTime(mController.getCurrentPos());
            updateSeekBar(mController.getCurrentPos());
        }
    }

    public void updateSeekBar(long pos) {
        int progress = progress2SeekBarPos(pos);
        mSeekBar.setProgress(progress);
        mSeekBarL.setProgress(progress);

        int bufferPercentage = mController.getBufferPercentage();
        mSeekBar.setSecondaryProgress(bufferPercentage * 10);
        mSeekBarL.setSecondaryProgress(bufferPercentage * 10);
    }

    public void updateTime(long pos) {
        String currPos = generateTime(pos);
        mTvPos.setText(currPos + "/");
        mTvPosL.setText(currPos + "/");
    }

    private void updateTime(int progress) {
//        String currPos = generateTime(mController.getCurrentPos());
        String currPos = generateTime(seekBarPos2Progress(progress));
        mTvPos.setText(currPos + "/");
        mTvPosL.setText(currPos + "/");
    }

    private void updateStartBtn() {
        if (mController.isPlaying()) {
            mBtnStart.setImageResource(R.drawable.pause);
            mBtnStartL.setImageResource(R.drawable.pause);
        } else {
            mBtnStart.setImageResource(R.drawable.play);
            mBtnStartL.setImageResource(R.drawable.play);
        }
    }

    private void initView() {
        initListens();

        initP();
        initL();
    }

    private void initL() {
        mChildViewL = (ViewGroup) mInflater.inflate(R.layout.mediaplayer_controller_bottom_l_layout, null);

        mBtnStartL = (ImageView) mChildViewL.findViewById(R.id.btn_start);
        mBtnScreenL = (ImageView) mChildViewL.findViewById(R.id.btn_screen);

        mTvDurationL = (TextView) mChildViewL.findViewById(R.id.duration);
        mTvDurationL.setText(mDurationStr);

        mTvPosL = (TextView) mChildViewL.findViewById(R.id.pos);
        mSeekBarL = (SeekBar) mChildViewL.findViewById(R.id.seekbar);
        mBtnStartL.setOnClickListener(mBtnStartListener);
        mBtnScreenL.setOnClickListener(mBtnScreenListener);
        mSeekBarL.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mTvRate = (TextView) mChildViewL.findViewById(R.id.tv_rate);
        mTvRate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mVideoStreamsizes != null) {
                    showRatePop();
                }
            }
        });

        mDanmakuSend = (ImageView) mChildViewL.findViewById(R.id.btn_danmaku_send);
        mDanmakuSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SystemManager.getInstance().getSystem(SystemUser.class).isLogin()) {
                    mController.pause();
                    mController.show(3600000);
                    showDamakuPop(v);
                } else {
                    FrameworkUtils.showToast(mContext, "请先登录");
                }
            }
        });
        mDanmaku = (RadioButton) mChildViewL.findViewById(R.id.btn_danmaku);
        mDanmaku.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mController.danmakuIsShown()) {
                    mDanmaku.setBackgroundResource(R.drawable.icon_danmaku_close);
                    mDanmakuSend.setVisibility(View.GONE);
                } else {
                    mDanmaku.setBackgroundResource(R.drawable.icon_danmaku_open);
                    mDanmakuSend.setVisibility(View.VISIBLE);
                }
                mController.toggleDanmaku();
            }
        });
    }

    // 显示输入弹幕的布局
    private void showDamakuPop(View parent) {
        mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            mPopupWindow.showAtLocation(parent, Gravity.TOP, 0, 0);
        } else {
            ViewGroup layout = (ViewGroup) View.inflate(mContext, R.layout.pop_send_damaku, null);
            mPopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
            mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.touming_black_color));
            Button send = (Button) layout.findViewById(R.id.comment_btn_send);
            final EditText comment = (EditText) layout.findViewById(R.id.fragment_play_page_comment_et_msg);
            comment.setFocusable(true);
            layout.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInputMethodManager.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                    mPopupWindow.dismiss();
                }
            });
            send.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("".equals(comment.getText().toString())) {
                        FrameworkUtils.showToast(mContext, "弹幕不能为空");
                        return;
                    }
                    try {
                        mController.sendDanmaku(comment.getText().toString());
                    } catch (Exception e) {
                        FrameworkUtils.showToast(mContext, "弹幕发送失败,请重新进入视频后再试");
                    }
                    comment.setText("");
                    mInputMethodManager.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                    mPopupWindow.dismiss();
                }
            });
            mPopupWindow.showAtLocation(parent, Gravity.TOP, 0, 0);
            mPopupWindow.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    Logger.d(TAG, "pop dismiss");
                    mController.start();
                    mController.hide();
                }
            });
        }
    }

    private void initP() {
        mChildView = (ViewGroup) mInflater.inflate(R.layout.mediaplayer_controller_bottom_layout, null);
        mParentView.addView(mChildView);

        mBtnStart = (ImageView) mChildView.findViewById(R.id.btn_start);
        mBtnScreen = (ImageView) mChildView.findViewById(R.id.btn_screen);
        mTvDuration = (TextView) mChildView.findViewById(R.id.duration);
        mTvPos = (TextView) mChildView.findViewById(R.id.pos);
        mSeekBar = (SeekBar) mChildView.findViewById(R.id.seekbar);
        mBtnStart.setOnClickListener(mBtnStartListener);
        mBtnScreen.setOnClickListener(mBtnScreenListener);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
    }

    private OnClickListener mBtnStartListener, mBtnScreenListener;
    private OnSeekBarChangeListener mSeekBarChangeListener;

    private int countLines;

    private void initListens() {
        mBtnStartListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                mController.toggleVideoPlay();

            }
        };
        mBtnScreenListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                mController.toggleScreen();
            }
        };

        mSeekBarChangeListener = new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mController.setSeekStatus(false);
                Logger.d(TAG, "seek to =" + seekBarPos2Progress(seekBar.getProgress()));
                mController.seekTo(seekBarPos2Progress(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mController.setSeekStatus(true);
                mController.show();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                updateTime(progress);
            }
        };
    }

    private int progress2SeekBarPos(long progress) {
        if (mDuration != 0) {
            return (int) (1000 * progress / mDuration);
        }
        return 0;
    }

    private long seekBarPos2Progress(int seekBarPos) {
        if (mDuration != 0) {
            return (long) ((mDuration * seekBarPos) * 0.001);
        }
        return 0;
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60);
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);

    }

    private void showRatePop() {
        if (mBottomRatePop == null) {
            createRatePop();
        }
        if (!mBottomRatePop.isShowing()) {
            int[] popPos = getPopPos();
            mBottomRatePop.showAtLocation(mChildViewL, Gravity.NO_GRAVITY, popPos[0], popPos[1]);
        } else {
            mBottomRatePop.dismiss();
        }
    }

    private int[] getPopPos() {
        int[] resutlPos = new int[2];
        int[] location = new int[2];
        mTvRate.getLocationOnScreen(location);
        resutlPos[0] = location[0];
        int[] location2 = new int[2];
        mChildViewL.getLocationOnScreen(location2);
        resutlPos[1] = location2[1] - FrameworkUtils.dip2px(mController.getContext(), 170 - ((4 - countLines) * 40));
        return resutlPos;
    }

    private void createRatePop() {
        ViewGroup contentView = (ViewGroup) mInflater.inflate(R.layout.view_player_pop_rate, null);
        if (contentView == null) {
            return;
        }
        initRatePopView(contentView);
        mBottomRatePop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mBottomRatePop.setOutsideTouchable(true);
        mBottomRatePop.setFocusable(true);
        mBottomRatePop.setBackgroundDrawable(mController.getContext().getResources()
                .getDrawable(R.drawable.icon_pop_bg));
    }

    private void initRatePopView(ViewGroup contentView) {
        OnClickListener listener = new OnClickListener() {
            String path;

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.pop_rate_1) {
                    // 超清
                    if (STREAM_SUPER.equals(mCurrentRate)) {
                        mBottomRatePop.dismiss();
                        return;
                    }
                    if (mController.mShouleUseSource.equals("1")) {// pc
                        path = mController.mUrlPc;
                        mController.changeRate(path);
                    } else {
//                        path = getFormatUrlForM3u8(mController.mUrlMobile, SystemUser.RATE_SUPPER);
                        changeUrlFromNet("hd2");
                    }
                    mCurrentRate = SystemUser.RATE_SUPPER;
                    mTvRate.setText("超清");
                } else if (v.getId() == R.id.pop_rate_2) {
                    // 高清
                    if (SystemUser.RATE_HEIGHTY.equals(mCurrentRate)) {
                        mBottomRatePop.dismiss();
                        return;
                    }
//                    path = getFormatUrlForM3u8(mController.mUrlMobile, SystemUser.RATE_HEIGHTY);
                    changeUrlFromNet("high");
                    mCurrentRate = SystemUser.RATE_HEIGHTY;
                    mTvRate.setText("高清");
                } else if (v.getId() == R.id.pop_rate_3) {
                    // 标清
                    if (SystemUser.RATE_NORMAL.equals(mCurrentRate)) {
                        mBottomRatePop.dismiss();
                        return;
                    }
//                    path = getFormatUrlForM3u8(mController.mUrlMobile, SystemUser.RATE_NORMAL);
                    changeUrlFromNet("normal");
                    mCurrentRate = SystemUser.RATE_NORMAL;
                    mTvRate.setText("标清");
                } else if (v.getId() == R.id.pop_rate_4) {
                    // 流畅
                    if (SystemUser.RATE_FLUENT.equals(mCurrentRate)) {
                        mBottomRatePop.dismiss();
                        return;
                    }
//                    path = getFormatUrlForM3u8(mController.mUrlMobile, SystemUser.RATE_FLUENT);
                    changeUrlFromNet("low");
                    mCurrentRate = SystemUser.RATE_FLUENT;
                    mTvRate.setText("流畅");
                } else {
                    if (SystemUser.RATE_FLUENT.equals(SystemUser.RATE_FLUENT)) {
                        mBottomRatePop.dismiss();
                        return;
                    }
//                    path = getFormatUrlForM3u8(mController.mUrlMobile, SystemUser.RATE_FLUENT);
                    changeUrlFromNet("low");
                    mCurrentRate = SystemUser.RATE_FLUENT;
                    mTvRate.setText("流畅");
                }
//                mController.changeRate(path);
                mBottomRatePop.dismiss();
            }
        };
        if (!TextUtils.isEmpty(mVideoStreamsizes.low)) {
            contentView.findViewById(R.id.pop_rate_4).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.pop_rate_4).setOnClickListener(listener);
            countLines++;
        }
        if (!TextUtils.isEmpty(mVideoStreamsizes.normal)) {
            contentView.findViewById(R.id.pop_rate_3).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.pop_rate_3).setOnClickListener(listener);
            countLines++;
        }
        if (!TextUtils.isEmpty(mVideoStreamsizes.high)) {
            contentView.findViewById(R.id.pop_rate_2).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.pop_rate_2).setOnClickListener(listener);
            countLines++;
        }
        if (!TextUtils.isEmpty(mVideoStreamsizes.hd2)) {
            contentView.findViewById(R.id.pop_rate_1).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.pop_rate_1).setOnClickListener(listener);
            countLines++;
        }
    }

    /**
     * 切换清晰度时请求接口
     *
     * @param stream
     */
    private void changeUrlFromNet(final String stream) {
        JsonObject params = new JsonObject();
        params.addProperty("id", mController.getVideo().id);
        String url = Utils.processUrl(ModuleType.BASE, ApiType.BASE_streamSizes, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext, url, "changeUrl_streamSizes", new RequestCallBack<List<StreamSize>>() {
            @Override
            public void onSuccess(List<StreamSize> data, Response resp) {
                Logger.d(TAG, "onSuccess_changeUrlFromNet " + resp.msg);
                if (data != null && data.size() != 0) {
                    for (int i = 0; i < data.size(); i++) {
                        StreamSize streamSize = data.get(i);
                        if (stream.equals(streamSize.stream_type)) {
                            Logger.d(TAG, "streamSize.url= " + streamSize.url);
                            mController.mUrlMobile = streamSize.url;
                        }
                    }
//                    String path = getFormatUrlForM3u8(mController.mUrlMobile, SystemUser.RATE_SUPPER);
                    String path = mController.mUrlMobile;
                    Logger.d(TAG, "path= " + path);
                    mController.changeRate(path);
                }
            }

            @Override
            public void onFailure(Response resp) {
                Logger.d(TAG, "onFailure_changeUrlFromNet " + resp.msg);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private String getFormatUrlForM3u8(String url, String rate) {
        if (TextUtils.isEmpty(url))
            return null;

        int headerIndex = url.indexOf("&type=");
        // eg:content="&type=hd2"
        String content = url.substring(headerIndex, headerIndex + 9);
        String[] split = content.split("=");
        split[1] = rate;
        String goalContent = split[0] + "=" + split[1];
        return url.replaceAll(content, goalContent);
    }


    private String getLastStream() {
        if (mVideoStreamsizes == null) {
            return STREAM_NORMAL;
        }

        if (!TextUtils.isEmpty(mVideoStreamsizes.hd2)) {
            return STREAM_SUPER;
        } else if (!TextUtils.isEmpty(mVideoStreamsizes.high)) {
            return STREAM_HIGH;
        } else if (!TextUtils.isEmpty(mVideoStreamsizes.normal)) {
            return STREAM_NORMAL;
        } else if (!TextUtils.isEmpty(mVideoStreamsizes.low)) {
            return STREAM_LOW;
        } else {
            return STREAM_NORMAL;
        }
    }
}
