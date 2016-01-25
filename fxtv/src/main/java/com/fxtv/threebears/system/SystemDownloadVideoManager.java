package com.fxtv.threebears.system;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.romlite.DatabaseHelper;
import com.fxtv.threebears.service.DownloadVideoService;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Jony on 15/12/25.
 */
public class SystemDownloadVideoManager extends SystemBase {
    private static final String TAG = "SystemDownloadVideoManager";

    private DownloadVideoService.IDownLoadCallBack mCallBack;
    private IServiceConnCallBack mServiceConnCallBack;

    private DownloadVideoService mService;
    private boolean mServiceIsConn;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "onServiceDisconnected");
            mServiceIsConn = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG, "onServiceConnected");
            if (service != null) {
                mServiceIsConn = true;
                mService = ((DownloadVideoService.MyBinder) service).getService();
                if (mServiceConnCallBack != null) {
                    mServiceConnCallBack.onConn(true);
                }
            } else {
                if (mServiceConnCallBack != null) {
                    mServiceConnCallBack.onConn(false);
                }
            }
        }
    };

    @Override
    protected void init() {
        super.init();
        if (!isServiceWork()) {
            startService();
        }

        bindService(null);
    }

    @Override
    protected void destroy() {
        super.destroy();
        unbindService();
        mService = null;
        mCallBack = null;
        mServiceConnCallBack = null;
    }

    /**
     * ------------------Business about---------------------
     */

    public void downloadVideo(final VideoCache videoCache, final IDownloadCallBack callBack) {
        Logger.d(TAG, "downloadVideo");
        if (!FrameworkUtils.isNetworkConnected(mContext)) {
            if (callBack != null) {
                callBack.onResult(false, "网络未连接,无法下载");
            }
            return;
        } else {
            if (!FrameworkUtils.isWifiConnected(mContext)
                    && !getSystem(SystemConfig.class).mCanDownloadUnderFlowEvn) {
                if (callBack != null) {
                    callBack.onResult(false, "3/4G网络下不允许下载");
                }
                return;
            }
        }

        int status = getVideoDownloadState(videoCache.vid);

        if (status == DownloadVideoService.DOWNLOAD_STATUS_COMPLETE) {
            if (callBack != null) {
                callBack.onResult(false, "该视频已经下载");
            }
            return;
        }

        if (status == DownloadVideoService.DOWNLOAD_STATUS_ING) {
            if (callBack != null) {
                callBack.onResult(false, "该视频正在下载中");
            }
            return;
        }

//        if (!checkVolume()) {
//            if(callBack != null){
//                callBack.onResult(false,"容量不足,无法下载");
//            }
//            return;
//        }

        if (mServiceIsConn) {
            mService.downloadVideo(videoCache);
            if (callBack != null) {
                callBack.onResult(true, "已添加至下载队列");
            }
        } else {
            if (!isServiceWork()) {
                startService();
            }

            bindService(new IServiceConnCallBack() {
                @Override
                public void onConn(boolean flag) {
                    if (flag) {
                        mService.downloadVideo(videoCache);
                        if (callBack != null) {
                            callBack.onResult(true, "已添加至下载队列");
                        }
                    } else {
                        if (callBack != null) {
                            callBack.onResult(false, "下载服务未开启,请稍后下载");
                        }
                    }
                }
            });
        }
    }

    public void pauseDownload(String tag) {
        mService.pauseDownload(tag);
    }

    public void pauseAllDownload() {
        mService.pauseAllDownload();
    }

    public void cancelDownloadingVideo(String tag,String downloadPath) {
        mService.cancelDownloadingVideo(tag,downloadPath);
    }

//    public void cancelAllDownloadingVideos() {
//        mService.cancelAllDownloadingVideos();
//    }

    /**
     * 获取正在下载的视频
     *
     * @return
     */
    public List<VideoCache> getDownloadingVideos() {
        List<VideoCache> result = null;

        try {
            Dao dao = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            QueryBuilder builder = dao.queryBuilder();
            builder.where().notIn("status", DownloadVideoService.DOWNLOAD_STATUS_COMPLETE);
            result = builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "getDownloadingVideos,happen error,msg=" + e.getMessage());
        }

        if (result != null) {
            Logger.d(TAG, "getDownloadingVideos,size=" + result.size());
        } else {
            Logger.d(TAG, "getDownloadingVideos,size=0");
        }
        return result;
    }

    /**
     * 获取已下载的视频
     *
     * @return
     */
    public List<VideoCache> getDownloadedVideos() {
        List<VideoCache> result = null;

        try {
            Dao dao = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            QueryBuilder builder = dao.queryBuilder();
            builder.where().eq("status", DownloadVideoService.DOWNLOAD_STATUS_COMPLETE);
            result = builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "getDownloadedVideos,happen error,msg=" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除已下载的视频
     *
     * @param tag
     */
    public void deleteDownloadedVideo(String tag,String downloadPath) {
        try {
            Dao dao = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            dao.deleteById(tag);
            FrameworkUtils.Files.delFolder(downloadPath);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "deleteDownloadedVideo,happen error,msg=" + e.getMessage());
        }
    }

    /**
     * 删除已下载的所有视频
     */
//    public void deleteAllDownloadedVideos(final IDeleteCallBack callBack) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Dao dao = DatabaseHelperFramework.getHelper().getDao(VideoCache.class);
//                    QueryBuilder builder = dao.queryBuilder();
//                    builder.where().eq("status", DownloadVideoService.DOWNLOAD_STATUS_COMPLETE);
//                    List<VideoCache> videos = builder.query();
//                    for (VideoCache videoCache : videos) {
//                        deleteDownloadedVideo(videoCache.vid,videoCache.downloadPath);
//                    }
//                    Utils.dismissProgressDialog();
//                    if (callBack != null) {
//                        callBack.onResult(true);
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    Logger.e(TAG, "deleteAllDownloadedVideos,happen error,msg=" + e.getMessage());
//                    if (callBack != null) {
//                        callBack.onResult(false);
//                    }
//                }
//            }
//        }).start();
//    }

    /**
     * 判断视频是否下载过
     *
     * @param tag tag
     * @return
     */
    public boolean isDownloaded(String tag) {
        int status = getVideoDownloadState(tag);
        return !(status == DownloadVideoService.DOWNLOAD_STATUS_NOT);
    }

    /**
     * 判断该视频的下载状态
     * <p/>
     * 未下载 -10
     */
    public int getVideoDownloadState(String tag) {
        int result = DownloadVideoService.DOWNLOAD_STATUS_NOT;
        try {
            Dao dao = DatabaseHelper.getHelper(mContext).getDao(VideoCache.class);
            VideoCache videoCache = (VideoCache) dao.queryForId(tag);
            if (videoCache != null) {
                result = videoCache.status;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "getVideoDownloadState,happen error,msg=" + e.getMessage());
        }
        return result;
    }

    private void startService() {
        Logger.d(TAG,"startService");
        Intent startIntent = new Intent(mContext, DownloadVideoService.class);
        mContext.startService(startIntent);
    }

    private void bindService(IServiceConnCallBack callBack) {
        Logger.d(TAG,"bindService");
        mServiceConnCallBack = callBack;
        Intent bindIntent = new Intent(mContext, DownloadVideoService.class);
        mContext.bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        Logger.d(TAG,"unbindService");
        mContext.unbindService(connection);
    }

    private boolean checkVolume() {
        Logger.d(TAG, "checkVolume,path=" + getSystem(SystemConfig.class).mDownLoadPath);
        File file = new File(getSystem(SystemConfig.class).mDownLoadPath);
        long totalSpace = file.getTotalSpace();
        long freeSpace = file.getFreeSpace();
        long usableSpace = file.getUsableSpace();
        Logger.d(TAG, "totalSpace=" + totalSpace);
        Logger.d(TAG, "freeSpace=" + freeSpace);
        Logger.d(TAG, "usableSpace=" + usableSpace);
        int size = (int) (usableSpace / 1024 / 1024);
        Logger.d(TAG, "checkVolume size = " + size);
        return size > 300;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @return true代表正在运行，false代表服务没有正在运行
     */
    private boolean isServiceWork() {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(200);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            Logger.d(TAG,"isServiceWork,mName="+mName);
            Logger.d(TAG,"isServiceWork,mName="+mName);
            if (mName.equals(DownloadVideoService.class.getCanonicalName())) {
                isWork = true;
                break;
            }
        }
        Logger.d(TAG,"isServiceWork,isWork="+isWork);
        return isWork;
    }

    public interface IDeleteCallBack {
        public void onResult(boolean flag);
    }

    private interface IServiceConnCallBack {
        public void onConn(boolean flag);
    }

    public interface IDownloadCallBack {
        public void onResult(boolean flag, String msg);
    }
}
