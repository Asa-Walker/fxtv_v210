package com.fxtv.threebears.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemFrameworkConfig;
import com.umeng.message.PushAgent;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

public class SystemConfig extends SystemBase {
    private static final String TAG = "SystemConfig";
    private SharedPreferences mSharedPreferences;
    private static final String SP_NAME = "config";
    private static final String KEY_DOWNLOAD_POSITION = "key_download_position";
    private static final String KEY_DOWNLOAD_MAY_FLOW_ENV = "key_download_may_flow_env";
    private static final String KEY_DOWNLOAD_DEFINITION = "key_download_definition";
    private static final String KEY_VIDEO_PLAY_MAY_FLOW_ENV = "key_video_play_may_flow_env";
    private static final String KEY_RECEIVE_ABLE = "key_receive_able";
    private static final String KEY_DEVICE_ID_LOGIN = "key_device_id_login";
    private static final String KEY_DEVICE_ID_LOGOUT = "key_device_id_logout";
    public static final int DOWNLOAD_POSITION_SDCARD = 0;
    public static final int DOWNLOAD_POSITION_INTERNAL = 1;
    /**
     * 后台环境
     */
    public boolean DEBUG_ENV = false;
    public String HTTP_BASE_URL_TEST_2_0 = "http://api.feixiong.tv/ApiTest/";
    public String HTTP_BASE_URL_2_0 = "http://api.feixiong.tv/Api/";
    public String HTTP_BASE_URL_2_0_FOR_PHOTO = "http://bee.feixiong.tv/Api/";
    public String HTTP_BASE_URL_2_0_FOR_PHOTO_TEST = "http://bee.feixiong.tv/ApiTest/";

    public final String STORE_URL="http://api.feixiong.tv/h5/fx_store/goods_list.html";//飞熊商城url

    public String mDownLoadPath;
    // 允许流量环境下播放视频
    public boolean mCanPlayUnderFlowEvn;
    // 允许流量环境下下载视频
    public boolean mCanDownloadUnderFlowEvn;
    // 允许接受消息推送
    public boolean mCanReceiveMessage;
    // 是否保存在SD卡上
    public boolean mSaveSDCard;
    // StatFs关于获取内存大小的路径
    public String StatFsStoragePath;

    public static final int POST_IMG_MAX_W = 600;
    public static final int POST_IMG_MAX_H = 600;

    @Override
    protected void init() {
        super.init();
        mSharedPreferences = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        initDownLoadPath();
        initVariable();
        // 获取路径
        StatFsStoragePath = mContext.getCacheDir().getAbsolutePath();
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    /** ----------------------分割线------------------------- */
    /**
     * 设置下载路径
     *
     * @param position
     */
    public boolean setDownloadPosition(int position) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String path = "";
        // sdcard:true,internal:false
        if (position == DOWNLOAD_POSITION_INTERNAL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                if (externalFilesDir != null) {
                    String tempPath = externalFilesDir.getPath();
                    if (TextUtils.isEmpty(tempPath)) {
                        path = SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mCacheDir + "/videos";
                    } else {
                        path = tempPath + "/videos";
                    }
                }
            } else {
                path = SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mCacheDir + "/videos";
            }
            mSaveSDCard = false;
        } else if (position == DOWNLOAD_POSITION_SDCARD) {
            String extSDCardPath = FrameworkUtils.getExtSDCardPath(mContext);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String tempPath = getsdCardPrivateDir();
                if (TextUtils.isEmpty(tempPath)) {
                    if (TextUtils.isEmpty(extSDCardPath)) {
                        Logger.e(TAG, "sdk > kitkat, not find sdcard");
                        return false;
                    } else {
                        path = extSDCardPath + "/fxtv/videos";
                    }
                } else {
                    path = tempPath + "/videos";
                }
            } else {
                if (TextUtils.isEmpty(extSDCardPath)) {
                    Logger.e(TAG, "sdk < kitkat,not find sdcard");
                    return false;
                } else {
                    path = extSDCardPath + "/fxtv/videos";
                }
            }
            mSaveSDCard = true;
        }
        mDownLoadPath = path;
        Logger.d(TAG, "setDownloadPosition,path=" + path);
        editor.putBoolean(KEY_DOWNLOAD_POSITION, mSaveSDCard);
        editor.commit();
        return true;
    }

    @SuppressLint("NewApi")
    private String getsdCardPrivateDir() {
        try {
            File[] externalCacheDirs = mContext.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);
            for (File temp : externalCacheDirs) {
                String path = temp.getPath();
                Log.d(TAG, "getsdCardPrivateDir path=" + temp.getPath());
                if (path.toLowerCase(Locale.CHINA).contains("sdcard")) {
                    Log.d(TAG, "getsdCardPrivateDir gold path = " + path);
                    File file = new File(path + "/videos");
                    file.mkdirs();
                    return file.getPath();
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 设置 流量环境下是否可以播放视频
     *
     * @param mayPlay
     */
    public void setMayPlayUnderFlowEnv(boolean mayPlay) {
        mCanPlayUnderFlowEvn = mayPlay;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_VIDEO_PLAY_MAY_FLOW_ENV, mayPlay);
        editor.commit();
    }

    /**
     * 设置 流量环境下是否可以下载
     *
     * @param mayDownload
     */
    public void setMayDownloadUnderFlowEnv(boolean mayDownload) {
        mCanDownloadUnderFlowEvn = mayDownload;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_DOWNLOAD_MAY_FLOW_ENV, mayDownload);
        editor.commit();
    }

    /**
     * 设置 是否可以接受推送
     *
     * @param mayReceive
     */
    public void setMayReceive(boolean mayReceive) {
        mCanReceiveMessage = mayReceive;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_RECEIVE_ABLE, mayReceive);
        editor.commit();
    }

    /** ----------------------初始化------------------------- */
    /**
     * 初始化，用户相关设置变量
     */
    private void initVariable() {
        mCanPlayUnderFlowEvn = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(
                KEY_VIDEO_PLAY_MAY_FLOW_ENV, false);
        mCanDownloadUnderFlowEvn = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(
                KEY_DOWNLOAD_MAY_FLOW_ENV, false);
        mCanReceiveMessage = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(KEY_RECEIVE_ABLE,
                true);
        if (mCanReceiveMessage) {
            PushAgent.getInstance(mContext).enable();
        } else {
            PushAgent.getInstance(mContext).disable();
        }
    }

    /**
     * 初始化下载路径
     */
    private void initDownLoadPath() {
        // sdcard:true,internal:false
        boolean saveSDCard = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(
                KEY_DOWNLOAD_POSITION, false);
        if (saveSDCard) {
            Logger.d(TAG, "initDownLoadPath,default pos is sd card");
            if (!setDownloadPosition(DOWNLOAD_POSITION_SDCARD)) {
                setDownloadPosition(DOWNLOAD_POSITION_INTERNAL);
            }
        } else {
            Logger.d(TAG, "initDownLoadPath,default pos is interior strorage");
            setDownloadPosition(DOWNLOAD_POSITION_INTERNAL);
        }
    }

    /**
     * 根据下载路径获取剩余的空间大小
     *
     * @return
     */
    @SuppressLint("NewApi")
    public String getRestSpace() {
        DecimalFormat spaceFor = new DecimalFormat("######0.0");
        StatFs stat = new StatFs(mDownLoadPath);
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        // 由于API18（Android4.3）以后getBlockSize过时并且改为了getBlockSizeLong
        // 因此这里需要根据版本号来使用那一套API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();

            totalBlocks = stat.getBlockCountLong();

            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();

            totalBlocks = stat.getBlockCount();

            availableBlocks = stat.getAvailableBlocks();
        }
        String total = spaceFor.format((blockSize * totalBlocks * 1.0) / 1024 / 1024 / 1024);
        String rest = spaceFor.format((blockSize * availableBlocks * 1.0) / 1024 / 1024 / 1024);
        return "总空间:" + total + "GB  可用空间:" + rest + "GB";
    }

    /**
     * 获取剩余的存储空间(下载判断用)
     *
     * @return
     */
    @SuppressLint("NewApi")
    public long getLevelSpace() {
        DecimalFormat spaceFor = new DecimalFormat("######000");
        StatFs stat = new StatFs(mDownLoadPath);
        long blockSize;
        long availableBlocks;
        // 由于API18（Android4.3）以后getBlockSize过时并且改为了getBlockSizeLong
        // 因此这里需要根据版本号来使用那一套API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        String rest = spaceFor.format((blockSize * availableBlocks) / 1024 / 1024 / 1024);

        return Long.parseLong(rest);
    }
}
