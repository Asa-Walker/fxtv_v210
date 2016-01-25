package com.fxtv.framework.system;

import android.os.Environment;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.SystemBase;

/**
 * @author FXTV-Android
 */
public class SystemFrameworkConfig extends SystemBase {
    /**
     * log 开关
     */
    public boolean mLog = true;

    /**
     * 崩溃处理
     */
    public boolean mCrashHander = false;

    /**
     * 崩溃log缓存本地
     */
    public boolean mCrashLogNative = false;

    /**
     * 崩溃log缓存服务器(umeng)
     */
    public boolean mCrashLogNet = false;

    /**
     * 程序崩溃重启
     */
    public boolean mCrashReset = false;

    /**
     * 版本自动更新
     */
    public boolean mVersionUpgrade = true;

    /**
     * 缓存根目录
     */
    public String mCacheDir = Environment.getExternalStorageDirectory() + "/fxtv";

    /**
     * http缓存 流量环境下更新时间
     */
    public long mHttpCacheGprsPastTime = 10 * 60 * 1000;

    /**
     * http缓存 wifi环境下更新时间
     */
    public long mHttpCacheWifiPastTime = 1 * 60 * 1000;

    public String mVersion;
    public final String platform = "android";

    /**
     * ------------------短信验证--------------------------
     */

    @Override
    protected void init() {
        super.init();
        mVersion = FrameworkUtils.getVersion(mContext);
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

}
