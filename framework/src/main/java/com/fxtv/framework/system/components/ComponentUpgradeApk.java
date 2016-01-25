package com.fxtv.framework.system.components;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.R;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemVersionUpgrade.IApkUpgradeCallBack;
import com.fxtv.framework.system.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ComponentUpgradeApk extends BaseComponent {
    private static final String TAG = "UpgradeApkComponent";

    private static final int MSG_TYPE_UPDATE = 0;
    private static final int MSG_TYPE_OVER = 1;
    private static final int MSG_TYPE_ERROR = 2;

    private Context mContext;
    private String mServiceVersion;
    private String mApkFileName;
    // 1:正常 2：强制升级
    private String mType;
    private String mUrl;

    private boolean mIsDownloading;
    private String mPath;
    private String mApkFilePath;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TYPE_UPDATE:
                    break;

                case MSG_TYPE_ERROR:
                    // mNotificationManager.cancel(0);
                    // mIsDownloading = false;
                    break;

                case MSG_TYPE_OVER:
                    mNotificationManager.cancel(0);
                    installApk();
                    mNotification = null;
                    mIsDownloading = false;
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public ComponentUpgradeApk(Context context, String path) {
        mContext = context;
        mPath = path;
    }

    public void checkApkUpdate(final String url, final IApkUpgradeCallBack callBack) {
        if (callBack != null) {
            SystemManager.getInstance().getSystem(SystemHttp.class)
                    .get(mContext, url, "versonUpdateApi", false, false, new RequestCallBack<String>() {

                        @Override
                        public void onSuccess(String data, Response resp) {
                            try {
                                JSONObject versonInfObject = new JSONObject(data);
                                mServiceVersion = versonInfObject.getString("version");
                                mUrl = versonInfObject.getString("apk_url");
                                mApkFileName = versonInfObject.getString("apk_name");
                                mType = versonInfObject.getString("type");

                                boolean shouldUpgrade = checkApkShouldUpdate(FrameworkUtils.getVersion(mContext),
                                        mServiceVersion);
                                callBack.onResult(shouldUpgrade, mType.equals("2"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String msg = "checkApkUpdate,error msg=" + e.getMessage();
                                Logger.e(TAG, msg);
                                callBack.onError(msg);
                            }
                        }

                        @Override
                        public void onFailure(Response resp) {
                            callBack.onError(resp.msg);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }


    public void upgradeApk() {
        if (!mIsDownloading) {
            initNotification();
            downloadApk();
        }
    }

    private Notification mNotification = null;
    private NotificationManager mNotificationManager = null;

    private void downloadApk() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url = new URL(mUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(mPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    mApkFilePath = mPath + "/" + mApkFileName + ".apk";
                    File ApkFile = new File(mApkFilePath);
                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];
                    int lastprogress = 0;
                    while (true) {
                        int numread = is.read(buf);
                        count += numread;
                        int progress = (int) (((float) count / length) * 100);
                        if (progress != lastprogress) {
                            lastprogress = progress;
                            Logger.d(TAG, "update progress");
                            mNotification.setLatestEventInfo(mContext, "飞熊视频", progress + "%", null);
                            mNotificationManager.notify(0, mNotification);
                        }
                        if (numread <= 0) {
                            // 下载完成通知安装
                            mHandler.sendEmptyMessage(MSG_TYPE_OVER);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    }

                    fos.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_TYPE_ERROR);
                }
            }
        }).start();
        mIsDownloading = true;
    }

    private void initNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (mNotification == null) {
            mNotification = new Notification();
            mNotification.icon = R.drawable.ic_launcher;
            mNotification.tickerText = "开始下载";
            mNotification.setLatestEventInfo(mContext, "飞熊视频", "0%", null);
            mNotification.flags = Notification.FLAG_NO_CLEAR;
        }

        Logger.d(TAG, "initNotification");
        // 发出通知
        mNotificationManager.notify(0, mNotification);
    }

    /**
     * 判断apk 是否需要升级
     *
     * @param currentVersion
     * @param serviceVersion
     * @return
     */
    private boolean checkApkShouldUpdate(String currentVersion, String serviceVersion) {
        if (TextUtils.isEmpty(currentVersion) || TextUtils.isEmpty(serviceVersion)) {
            return false;
        }

        currentVersion = currentVersion.replace(".", ",").replace(",", "");
        serviceVersion = serviceVersion.replace(".", ",").replace(",", "");
        int currentV = Integer.parseInt(currentVersion);
        int newV = Integer.parseInt(serviceVersion);
        return newV > currentV;
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(mApkFilePath);
        if (!apkfile.exists()) {
            return;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

}
