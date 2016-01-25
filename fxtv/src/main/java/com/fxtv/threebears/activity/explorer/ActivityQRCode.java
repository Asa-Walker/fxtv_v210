package com.fxtv.threebears.activity.explorer;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.qrview.CameraManager;
import com.fxtv.threebears.view.qrview.InactivityTimer;
import com.fxtv.threebears.view.qrview.QRCodeDecodeHandler;
import com.fxtv.threebears.view.qrview.ViewfinderView;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

/**
 * 二维码扫描界面
 *
 * @author Android2
 */
public class ActivityQRCode extends BaseActivity implements Callback {

    private ViewfinderView mViewFinderView;
    private InactivityTimer mInactivityTimer;

    private boolean isFirstOpen;

    private MediaPlayer mediaPlayer;

    private QRCodeDecodeHandler mCodeDecodeHandler;

    private Vector<BarcodeFormat> mDecodeFormats;

    private SurfaceView mSurfaceView;
    /**
     * 字符集
     */
    private String mCharacterSet;

    /**
     * 是否播放声音
     */
    private boolean playBeep;

    /**
     * 是否震动
     */
    private boolean vibrate;

    /**
     * 响声的音量
     */
    private static final float BEEP_VOLUME = 0.10f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        initView();

    }

    private void initView() {
        initActionBar();
        initViewFinderView();
        isFirstOpen = false;

    }

    private void initViewFinderView() {
        CameraManager.init(getApplication());
        mViewFinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        mInactivityTimer = new InactivityTimer(this);
    }

    private void initActionBar() {
        ImageView back = (ImageView) findViewById(R.id.img_back);
        TextView title = (TextView) findViewById(R.id.my_title);
        findViewById(R.id.ab_editor).setVisibility(View.GONE);

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("二维码扫描");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSurfaceView();
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        // 扫描完成后的声音和振动效果
        // playBeepSoundAndVibrate();
        super.onDestroy();
    }

    // 震动持续的时间
    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private void initSurfaceView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();

        if (isFirstOpen) {
            // 初始化摄像头
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mDecodeFormats = null;
        mCharacterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCodeDecodeHandler != null) {
            mCodeDecodeHandler.quitSynchronously();
            mCodeDecodeHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            // AssetFileDescriptor file =
            // getResources().openRawResourceFd(R.raw.beep);
            try {
                AssetFileDescriptor file = getAssets().openNonAssetFd("beep.ogg");
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            return;
        } catch (RuntimeException e1) {
            return;
        }
        if (mCodeDecodeHandler == null) {
            mCodeDecodeHandler = new QRCodeDecodeHandler(this, mDecodeFormats, mCharacterSet);
        }
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        onResultHandler(resultString, barcode);
    }

    /**
     * 跳转到上一个页面返回扫到的内容
     *
     * @param resultString
     * @param bitmap
     */
    private void onResultHandler(String resultString, Bitmap bitmap) {
        if (TextUtils.isEmpty(resultString)) {
            showToast("没有扫描到内容");
            return;
        }
        // Intent resultIntent = new Intent();
        // Bundle bundle = new Bundle();
        // bundle.putString("result", resultString);
        // // bundle.putParcelable("bitmap", bitmap);
        // resultIntent.putExtras(bundle);
        // this.setResult(10, resultIntent);
        if (!TextUtils.isEmpty(resultString)) {
            if (resultString.contains("FXTV_LOGIN")) {
                Logger.d("debug", "Qrcode=" + resultString);
                if (getSystem(SystemUser.class).isLogin()) {
                    loginTV(resultString);
                } else {
                    showToast("请先登录");
                    ActivityQRCode.this.finish();
                }
            } else {
                if (URLUtil.isValidUrl(resultString)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, (Uri.parse(resultString))).addCategory(
                            Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ActivityQRCode.this.finish();
                } else {
                    showToast("扫描的二维码无效");
                }
            }
        }
    }

    private void loginTV(String QRCode) {
        JsonObject params = new JsonObject();
        params.addProperty("qmtt", QRCode);
        Utils.showProgressDialog(this);
        String url = Utils.processUrl(ModuleType.TV, ApiType.TV_qrcodeLogin, params);
        getSystem(SystemHttp.class).get(this, url, "tvScanLogin", false, false, new RequestCallBack() {
            @Override
            public void onSuccess(Object data, Response resp) {
                Logger.d("debug", "onSuccess,json=" + data);
                showToast("成功登录TV端");
            }

            @Override
            public void onFailure(Response resp) {
                try {
                    showToast(resp.msg);
                } catch (Exception e) {
                    Logger.e("debug", "e=" + e);
                }
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
                ActivityQRCode.this.finish();
            }
        });
    }

    public void drawViewfinder() {
        mViewFinderView.drawViewfinder();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!isFirstOpen) {
            isFirstOpen = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isFirstOpen = false;
    }

    public ViewfinderView getViewfinderView() {
        return mViewFinderView;
    }

    public Handler getHandler() {
        return mCodeDecodeHandler;
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}
