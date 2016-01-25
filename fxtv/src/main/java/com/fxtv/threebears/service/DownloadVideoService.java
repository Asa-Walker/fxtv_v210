package com.fxtv.threebears.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.threebears.components.ComponentM3U8Downloader;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.romlite.DatabaseHelper;
import com.fxtv.threebears.system.SystemConfig;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jony on 15/12/23.
 */
public class DownloadVideoService extends Service {
    private static final String TAG = "DownloadVideoService";

    private Context mContext;
    private List<ComponentM3U8Downloader> mDownloaderList;
    private IDownLoadCallBack mCallBack;

    private MyBinder mBinder;

    private static final int THREAD_POOL_SIZE = 3;

    public static final int DOWNLOAD_STATUS_COMPLETE = 0;
    public static final int DOWNLOAD_STATUS_ING = 1;
    public static final int DOWNLOAD_STATUS_WAITING = 3;
    public static final int DOWNLOAD_STATUS_PAUSE = 2;
    public static final int DOWNLOAD_STATUS_FAILURE = -1;
    public static final int DOWNLOAD_STATUS_CANCEL = -10;
    public static final int DOWNLOAD_STATUS_NOT = -11;

    public static final String ACTION_DOWNLOAD_ING = "download_ing";
    public static final String ACTION_DOWNLOAD_WAITING = "download_waite";
    public static final String ACTION_DOWNLOAD_PAUSE = "download_pause";
    public static final String ACTION_DOWNLOAD_PROGRESS = "download_progress";
    public static final String ACTION_DOWNLOAD_SUCCESS = "download_success";
    public static final String ACTION_DOWNLOAD_CANCEL = "download_cancel";
    public static final String ACTION_DOWNLOAD_FAILURE = "download_failure";
    public static final String ACTION_DOWNLOAD_COMPLETE = "download_complete";

    private ExecutorService mExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "onCreate");
        mContext = getApplicationContext();
        mBinder = new MyBinder();
        mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        mDownloaderList = new ArrayList<ComponentM3U8Downloader>();
        initListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand");
//        flags = START_STICKY;

        List<VideoCache> videos = getDownloadingVideos();
        for (VideoCache videoCache : videos) {
            if (videoCache.status == DOWNLOAD_STATUS_ING) {
                videoCache.status = DOWNLOAD_STATUS_PAUSE;
                updateNativeVideoCache(videoCache);
            }

            if (FrameworkUtils.isWifiConnected(mContext)) {
                downloadVideo(videoCache);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Logger.d(TAG, "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Logger.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
        mExecutor.shutdown();
        mDownloaderList.clear();
        mDownloaderList = null;
    }

    /**
     * -----------------------Busines about-----------------------
     */

    public synchronized void downloadVideo(VideoCache videoCache) {
        Logger.d(TAG, "downloadVideo,net_url=" + videoCache.net_url);
        ComponentM3U8Downloader videoDownloader = new ComponentM3U8Downloader(mContext,videoCache.vid, videoCache, SystemManager.getInstance().getSystem(SystemConfig.class).mDownLoadPath, mCallBack);
        mDownloaderList.add(videoDownloader);
        mExecutor.execute(videoDownloader);
    }

    public void pauseDownload(String tag) {
        ComponentM3U8Downloader videoDownloader = getVideoDownloader(tag);
        if (videoDownloader != null) {
            videoDownloader.pause();
            mDownloaderList.remove(videoDownloader);
        } else {
            Logger.e(TAG, "pauseDownload,not find tag=" + tag);
        }
    }

    public void pauseAllDownload() {
        for (ComponentM3U8Downloader downloader : mDownloaderList) {
            downloader.pause();
        }
        mDownloaderList.clear();
    }

    public void cancelDownloadingVideo(String tag,String downloadPath) {
        ComponentM3U8Downloader videoDownloader = getVideoDownloader(tag);
        if (videoDownloader != null) {
            videoDownloader.cancel();
            mDownloaderList.remove(videoDownloader);
        } else {
            Logger.d(TAG, "cancelDownloadingVideo,not find the tag=" + tag);
        }

        deleteVideo(tag,downloadPath);
    }

//    public void cancelAllDownloadingVideos() {
//        for (ComponentM3U8Downloader downloader : mDownloaderList) {
//            downloader.cancel();
//        }
//        mDownloaderList.clear();
//    }

    public void deleteVideo(String tag,String downloadPath) {
        try {
            Dao dao = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            dao.deleteById(tag);
            FrameworkUtils.Files.delFolder(downloadPath);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "deleteVideo,happen error,msg=" + e.getMessage());
        }
    }

    private ComponentM3U8Downloader getVideoDownloader(String tag) {
        ComponentM3U8Downloader result = null;
        for (ComponentM3U8Downloader downloader : mDownloaderList) {
            if (downloader.getTag().equals(tag)) {
                result = downloader;
                break;
            }
        }

        return result;
    }

    private void initListener() {
        mCallBack = new IDownLoadCallBack() {
            @Override
            public void onStart(String tag) {
                Logger.d(TAG, "onStart,tag=" + tag);
                Intent intent = new Intent();
                intent.setAction(ACTION_DOWNLOAD_ING);
                intent.putExtra("tag", tag);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onPause(String tag) {
                Logger.d(TAG, "onPause,tag=" + tag);
                mDownloaderList.remove(tag);
                Intent intent = new Intent();
                intent.setAction(ACTION_DOWNLOAD_PAUSE);
                intent.putExtra("tag", tag);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onWait(String tag) {
                Logger.d(TAG, "onWait,tag=" + tag);
                Intent intent = new Intent();
                intent.setAction(ACTION_DOWNLOAD_WAITING);
                intent.putExtra("tag", tag);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onProgress(String tag, int percentage, int speed) {
                Logger.d(TAG, "onProgress,tag=" + tag + ",percentage=" + percentage);
                Intent intent = new Intent();
                intent.setAction(ACTION_DOWNLOAD_PROGRESS);
                intent.putExtra("tag", tag);
                intent.putExtra("percentage", percentage);
                intent.putExtra("speed", speed);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onCancel(String tag) {
                Logger.d(TAG, "onCancel,tag=" + tag);
                mDownloaderList.remove(tag);
                Intent intent = new Intent();
                intent.setAction(ACTION_DOWNLOAD_CANCEL);
                intent.putExtra("tag", tag);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onSuccess(String tag) {
                Logger.d(TAG, "onSuccess,tag=" + tag);
                mDownloaderList.remove(tag);
                Intent intent = new Intent();
                intent.setAction(ACTION_DOWNLOAD_SUCCESS);
                intent.putExtra("tag", tag);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onFailure(String tag, String reason) {
                Logger.e(TAG, "onFailure,tag=" + tag + ",reason=" + reason);
                mDownloaderList.remove(tag);
                Intent intent = new Intent();
                intent.setAction(ACTION_DOWNLOAD_FAILURE);
                intent.putExtra("tag", tag);
                intent.putExtra("reason", reason);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onComplete(String tag) {
                Logger.d(TAG, "onComplete,tag=" + tag);
                Intent intent = new Intent();
                intent.setAction(ACTION_DOWNLOAD_COMPLETE);
                intent.putExtra("tag", tag);
                mContext.sendBroadcast(intent);
            }
        };
    }

    public List<VideoCache> getDownloadingVideos() {
        List<VideoCache> result = null;

        try {
            Dao dao = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            QueryBuilder builder = dao.queryBuilder();
            builder.where().notIn("status", DownloadVideoService.DOWNLOAD_STATUS_COMPLETE).and().notIn("status", DownloadVideoService.DOWNLOAD_STATUS_FAILURE);
            result = builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "getDownloadingVideos,happen error,msg=" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新本地缓存
     */
    private void updateNativeVideoCache(VideoCache videoCache) {
        try {
            Dao video = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            video.createOrUpdate(videoCache);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "updateNativeVideoCache,happended");
        }
    }

    public interface IDownLoadCallBack {
        public void onStart(String tag);

        public void onPause(String tag);

        public void onWait(String tag);

        public void onProgress(String tag, int percentage, int speed);

        public void onCancel(String tag);

        public void onSuccess(String tag);

        public void onFailure(String tag, String reason);

        public void onComplete(String tag);
    }

    public class MyBinder extends Binder {
        public DownloadVideoService getService() {
            return DownloadVideoService.this;
        }
    }
}
