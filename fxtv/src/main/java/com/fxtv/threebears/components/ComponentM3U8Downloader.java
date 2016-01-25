package com.fxtv.threebears.components;

import android.content.Context;
import android.text.TextUtils;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.romlite.DatabaseHelper;
import com.fxtv.threebears.service.DownloadVideoService;
import com.fxtv.threebears.system.SystemCommon;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by Jony on 15/12/23.
 */
public class ComponentM3U8Downloader implements Runnable {
    private static final String TAG = "ComponentM3U8Downloader";

    private Context mContext;
    private String mNetFilePath;
    private String mNativeFilePath;
    private String mNativeImagePath;
    private String mDownloadPath;
    private VideoCache mVideoCache;
    private boolean mThreadFlag = true;
    private String mTag;
    private DownloadVideoService.IDownLoadCallBack mCallBack;
    private String mNativeM3u8Str;

    public ComponentM3U8Downloader(Context context,String tag, VideoCache video, String downLoadPath, DownloadVideoService.IDownLoadCallBack callBack) {
        mContext = context;
        this.mTag = tag;
        this.mVideoCache = video;
        this.mCallBack = callBack;
        mDownloadPath = downLoadPath + "/" + mTag + "/";
        mNetFilePath = mDownloadPath + "net_file";
        mNativeFilePath = mDownloadPath + "native_file";
        mNativeImagePath = mDownloadPath + "image";
        mThreadFlag = true;

        Logger.d(TAG, "tag=" + tag);
        Logger.d(TAG,"download path="+mDownloadPath);
        video.status = DownloadVideoService.DOWNLOAD_STATUS_WAITING;
        video.downloadPath = mDownloadPath;
        updateNativeVideoCache();
        if (mCallBack != null) {
            mCallBack.onWait(mTag);
        }
    }

    public void updateUrl(String url) {
        mVideoCache.net_url = url;
    }

    @Override
    public void run() {
        try {
            if (mVideoCache.status == DownloadVideoService.DOWNLOAD_STATUS_CANCEL || mVideoCache.status == DownloadVideoService.DOWNLOAD_STATUS_PAUSE) {
                Logger.e(TAG, "status is error!!!");
                return;
            }

            mVideoCache.status = DownloadVideoService.DOWNLOAD_STATUS_ING;
            updateNativeVideoCache();
            if (mCallBack != null) {
                mCallBack.onStart(mTag);
            }

            // 该视频的缓存目录
            File file = new File(mDownloadPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //FrameworkUtils.Files.downLoadFile(mVideoCache.image,)

            if(!FrameworkUtils.Files.isFileExist(mNativeImagePath)){
                FrameworkUtils.Files.downLoadFile(mVideoCache.image,mNativeImagePath);
                mVideoCache.image = mNativeImagePath;
                updateNativeVideoCache();
            }

            Logger.d(TAG, "net_url=" + mVideoCache.net_url);
            File netFile = FrameworkUtils.Files.downLoadFile(mVideoCache.net_url, mNetFilePath);
            if (netFile == null) {
                happenError(DownloadVideoService.DOWNLOAD_STATUS_FAILURE, "获取网络文件失败");
                return;
            }

            String netFileContent = FrameworkUtils.Files.readFile(netFile);
            if (TextUtils.isEmpty(netFileContent)) {
                happenError(DownloadVideoService.DOWNLOAD_STATUS_FAILURE, "获取网络文件失败");
                return;
            }

            if (mVideoCache.source == 0) {
                //移动源地址
                if (!netFileContent.contains("EXT-X-TARGETDURATION:-1")) {
                    parseAndDownM3U8File(netFileContent);
                } else {
                    happenError(DownloadVideoService.DOWNLOAD_STATUS_FAILURE, "视频地址失效");
                }
            } else if (mVideoCache.source == 1) {
                //PC源地址
                happenError(DownloadVideoService.DOWNLOAD_STATUS_FAILURE, "暂不支持PC源下载");
            } else {
                happenError(DownloadVideoService.DOWNLOAD_STATUS_FAILURE, "地址源类型不支持");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (SystemManager.getInstance().getSystem(SystemCommon.class).isNetworkConnected()) {
                Logger.e(TAG,"isNetwork");
                happenError(DownloadVideoService.DOWNLOAD_STATUS_FAILURE, "未知错误,code=411");
            } else {
                Logger.e(TAG,"isNotNetwork");
                happenError(DownloadVideoService.DOWNLOAD_STATUS_PAUSE, "无网络");
            }
        }
        Logger.d(TAG,"over!!!!");
    }

    /**
     * 解析文件并下载
     *
     * @param netM3u8FileContent 网络m3u8内容
     */
    private void parseAndDownM3U8File(final String netM3u8FileContent) throws Exception {
        final String[] partArray = netM3u8FileContent.split(",");
        mNativeM3u8Str = new String(netM3u8FileContent);
        for (int i = 1; i < partArray.length; i++) {
            if (!mThreadFlag) {
                break;
            }
            String partInfo = partArray[i];
            String url = partInfo.substring(0, partInfo.indexOf("#")).replace("\r\n", "");
            String fileName = partInfo.substring(partInfo.indexOf("ts_seg_no") + 10, partInfo.lastIndexOf("&"));
            // String[] tmp = partInfo.split("\\?");
            // 文件后缀
            // String suffix = tmp[0].substring(tmp[0].lastIndexOf("."),tmp[0].length());
            if (!FrameworkUtils.Files.isFileExist(mDownloadPath + fileName)) {
                long temp = System.currentTimeMillis();
                File file = FrameworkUtils.Files.downLoadFile(url, mDownloadPath + fileName);
                if (file == null) {
                    happenError(DownloadVideoService.DOWNLOAD_STATUS_FAILURE, "未知错误,code=410");
                    break;
                }

                temp = System.currentTimeMillis() - temp;
                int speed = getSpeed(temp, file.length());
                int progress = i * 100 / (partArray.length - 1);
                mVideoCache.percentage = progress;
                if (mThreadFlag) {
                    updateNativeVideoCache();
                    mCallBack.onProgress(mTag, progress, speed);
                }
            }
            mNativeM3u8Str = mNativeM3u8Str.replace(url, "file:/" + mDownloadPath + fileName);
        }

        if (mThreadFlag) {
            // 创建本地m3u8文件
            FrameworkUtils.Files.writeFile(mNativeFilePath, mNativeM3u8Str);
            mVideoCache.percentage = 100;
            mVideoCache.status = DownloadVideoService.DOWNLOAD_STATUS_COMPLETE;
            mVideoCache.url = mNativeFilePath;
            updateNativeVideoCache();
            if (mCallBack != null) {
                mCallBack.onSuccess(mTag);
            }
            complete();
        }

        if (mVideoCache.status == DownloadVideoService.DOWNLOAD_STATUS_CANCEL) {
            Logger.d(TAG,"inter cancel");
            FrameworkUtils.Files.delFolder(mDownloadPath);
        }
    }

    public String getTag() {
        return mTag;
    }

    public void pause() {
        Logger.d(TAG, "pause,tag=" + mTag);
        mThreadFlag = false;
        mVideoCache.status = DownloadVideoService.DOWNLOAD_STATUS_PAUSE;
        updateNativeVideoCache();
        if (mCallBack != null) {
            mCallBack.onPause(mTag);
        }
    }

    public int getCurrentStatus(){
        return mVideoCache.status;
    }

    public void cancel() {
        Logger.d(TAG, "cancel,tag=" + mTag);
        mThreadFlag = false;
        mVideoCache.status = DownloadVideoService.DOWNLOAD_STATUS_CANCEL;
        try {
            Dao dao = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            dao.deleteById(mTag);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "cancel,happen error");
        }

        if (mCallBack != null) {
            mCallBack.onCancel(mTag);
        }

        complete();
    }

    private void happenError(int targetState, String errorReason) {
        mThreadFlag = false;
        mVideoCache.status = targetState;
        mVideoCache.failureReason = errorReason;
        updateNativeVideoCache();
        if (mCallBack != null) {
            mCallBack.onFailure(mTag, errorReason);
        }
    }

    private void complete() {
        if (mCallBack != null) {
            mCallBack.onComplete(mTag);
        }
    }

    /**
     * 更新本地缓存
     */
    private void updateNativeVideoCache() {
        try {
            Dao video = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            video.createOrUpdate(mVideoCache);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "updateNativeVideoCache,happended");
        }
    }

    /**
     * @param s    毫秒
     * @param size b
     * @return
     */
    private int getSpeed(long s, long size) {
        int speed = 0;
        size /= 1024;
        if (s > 1000) {
            s /= 1000;
            speed = (int) (size / s);
        } else {
            speed = (int) size;
        }

        return speed;
    }
}
